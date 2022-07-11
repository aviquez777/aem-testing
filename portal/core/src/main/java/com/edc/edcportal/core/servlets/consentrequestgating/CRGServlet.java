package com.edc.edcportal.core.servlets.consentrequestgating;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcportal.core.consentrequestgating.CRGsonDataBindingUtil;
import com.edc.edcportal.core.consentrequestgating.helpers.CRGConstants;
import com.edc.edcportal.core.consentrequestgating.helpers.CRGHelper;
import com.edc.edcportal.core.consentrequestgating.pojo.ConsentRequestGatingDO;
import com.edc.edcportal.core.eloqua.pojo.EloquaCDO;
import com.edc.edcportal.core.eloqua.services.EloquaConfigService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Base64EnconderHelper;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.ServletResponseHelper;
import com.edc.edcportal.core.helpers.TranslationHelper;
import com.edc.edcportal.core.transactionhistory.helpers.THHelper;
import com.edc.edcportal.core.transactionhistory.services.THConfigService;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, 
            service = Servlet.class, 
            property = { 
                    "sling.servlet.extensions=json",
                    "sling.servlet.paths=" + CRGConstants.CRG_SERVLET_URL 
                    }
)

public class CRGServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1791962928375019918L;

    private static final Logger logger = LoggerFactory.getLogger(CRGServlet.class);

    @Reference
    @Inject
    private EloquaDAOService eloquaDAOService;

    @Reference
    @Inject
    private EloquaConfigService eloquaConfigService;

    @Reference
    @Inject
    private THConfigService thConfigService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String error = CRGConstants.API_ERROR;
        boolean hasGvienConsent = true;
        ConsentRequestGatingDO consentRequestGatingDO = new ConsentRequestGatingDO();
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        // CRG Variables
        String encodedPath = request.getParameter(CRGConstants.CRG_PAGEPATH);
        String decodedPath = Base64EnconderHelper.decodeString(encodedPath);
        Map<String, String> gatedUrlAndPartner = CRGHelper.getGatedUrlAndPartner(request, decodedPath);
        String gatedUrl = gatedUrlAndPartner.get(CRGConstants.GATED_URL_PROPERTY);
        String partnersCode = gatedUrlAndPartner.get(CRGConstants.MULTIFIELD_NAME);
        // Debugging info
        String params = "externalId: ".concat(externalId).concat(" gatedUrl: ").concat(gatedUrl)
                .concat(" partnersCode: ").concat(partnersCode);
        // Get the record
        if (StringUtils.isNotBlank(gatedUrl) && StringUtils.isNotBlank(partnersCode)) {
            try {
                hasGvienConsent = eloquaDAOService.checkConsentRequestGating(externalId, gatedUrl, partnersCode, thConfigService);
                error = null;
            } catch (EDCEloquaSystemException e) {
                logger.error("CRGServlet could not get consentRequestGatingDO params {} stack trace {} ", params,
                        e.getStackTrace());
            }
        } else {
            logger.error("CRGServlet could not find ore or more params {}", params);
        }
        if (StringUtils.isNotBlank(error)) {
            String translatedError = TranslationHelper.getI18nTranslation(error, request, null);
            consentRequestGatingDO.setErrorMessage(translatedError);
        }
        // We have consent, be my guest
        if (hasGvienConsent) {
            String url = LinkResolver.changeInternalURLtoExternal(request,
                    LinkResolver.addHtmlExtension(gatedUrl, Constants.Paths.CONTENT_EDC));
            consentRequestGatingDO.setUrl(url);
        }
        writeResponse(response, consentRequestGatingDO);
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        ConsentRequestGatingDO consentRequestGatingDO  = new ConsentRequestGatingDO();
        String error = CRGConstants.API_ERROR;
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);

        // CRG Variables
        Map<String, String> gatedUrlAndPartner = CRGHelper.getGatedUrlAndPartner(request, null);
        String gatedUrl = gatedUrlAndPartner.get(CRGConstants.GATED_URL_PROPERTY);
        String partnersCode = gatedUrlAndPartner.get(CRGConstants.MULTIFIELD_NAME);
        // Debugging info
        String params = "externalId: ".concat(externalId).concat(" gatedUrl: ").concat(gatedUrl)
                .concat(" partnersCode: ").concat(partnersCode);
        // Get the record
        if (StringUtils.isNotBlank(gatedUrl) && StringUtils.isNotBlank(partnersCode)) {
            try {
                String transactionID = externalId.concat(Constants.PIPE).concat(gatedUrl);
                Map<String, String> variables = new HashMap<>();
                variables.put("email", headers.get(Constants.Properties.HEADER_EMAIL_ID));
                variables.put("tcsCSV", partnersCode);
                variables.put("caslsCSV", partnersCode);
                variables.put("witVal", null);
                EloquaCDO eloquaCDO = THHelper.addOrUpdateRecord(transactionID, variables, request, eloquaConfigService, thConfigService);
                // No Errors from API
                if(eloquaCDO.getErrorMessage() == null) {
                    error = null;
                }
            } catch (IOException | JSONException e) {
                logger.error("CRGServlet could not POST new record {} stack trace {} ", params, e.getStackTrace());
            }
        }
        // No errors, good to go
        if (StringUtils.isBlank(error)) {
            String redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                    LinkResolver.addHtmlExtension(gatedUrl, Constants.Paths.CONTENT_EDC));
            consentRequestGatingDO.setUrl(redirectTo);
            consentRequestGatingDO.setErrorMessage(null);
        } else {
         // Boo boo
            response.setStatus(500);
            String translatedError = TranslationHelper.getI18nTranslation(error, request, null);
            consentRequestGatingDO.setErrorMessage(translatedError);
        }
        writeResponse(response, consentRequestGatingDO);
    }

    SlingHttpServletResponse writeResponse(SlingHttpServletResponse response, ConsentRequestGatingDO consentRequestGatingDO) throws IOException {
        String json = CRGsonDataBindingUtil.consentRequestGatingDOToJson(consentRequestGatingDO);
        return ServletResponseHelper.writeResponse(response, json);
    }        
}

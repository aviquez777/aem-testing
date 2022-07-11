package com.edc.edcportal.core.servlets.ci;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.pojo.CiCompanySearchDO;
import com.edc.edcportal.core.ci.pojo.CiSharedObject;
import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.ci.services.CiDAOService;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.RedirectHelper;
import com.edc.edcportal.core.helpers.Request;
import com.edc.edcportal.core.restful.RestClientConstants;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, service = Servlet.class, property = { "sling.servlet.extensions=json",
        "sling.servlet.methods=" + RestClientConstants.Methods.METHOD_POST,
        "sling.servlet.methods=" + RestClientConstants.Methods.METHOD_GET,
        "sling.servlet.paths=" + CiConstants.CI_COMPANY_SEARCH_SERVICE_SERVLET_URL })

public class CompanySearch extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 5984231611166784516L;
    private static final Logger log = LoggerFactory.getLogger(CompanySearch.class);

    @Reference
    @Inject
    private CiDAOService ciDAOService;

    @Reference
    @Inject
    private CiConfigService ciConfigService;

    @Reference
    @Inject
    private EloquaDAOService eloquaDAOService;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        // get the external id
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        String language = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase();
        try {
            EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
            String langFieldId = eloquaDAOService.getEloquaConfigService().getLangdId();
            language = eloquaUserProfileDO.getFormFieldsValues().get(langFieldId).getEloquaValue();
        } catch (EDCEloquaSystemException e) {
            log.error("CompanySearch Not externalId on headers {}", headers);
        }
        String redirectTo = Constants.Paths.CONTENT_EDC.concat(Constants.FORWARD_SLASH).concat(language)
                .concat(ciConfigService.getLandingSearchPageNode());
        redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                LinkResolver.addHtmlExtension(redirectTo, Constants.Paths.CONTENT_EDC));
        RedirectHelper.redirectTo(response, redirectTo);
    }

    @SuppressWarnings("squid:S2221")
    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter(CiConstants.COMPANY_SEARCH_FORM_FIELD);
        String country = request.getParameter(CiConstants.COUNTRY_SEARCH_FORM_FIELD);
        String language = request.getParameter(CiConstants.LANGUAGE_SEARCH_FORM_FIELD);
        // Try to get the language, default to "en"
        if (StringUtils.isBlank(language)) {
            Resource pageRes = Request.getCurrentPageResource(request, null);
            Page currentPage = pageRes.adaptTo(Page.class);
            language = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                    Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        }
        try {
            CiCompanySearchDO ciCompanySearchDO = ciDAOService.searchCompanyByName(name, country, language);
            CiSharedObject ciSharedObject = new CiSharedObject();
            ciSharedObject.setName(name);
            ciSharedObject.setCountry(country);
            ciSharedObject.setLanguage(language);

            // Create a temp object to store the results on session
            ciSharedObject.setCiCompanySearchDO(ciCompanySearchDO);
            request.getSession().setAttribute(CiConstants.SEARCH_SESSION_VAR, ciSharedObject);

            String redirectTo = Constants.Paths.CONTENT_EDC.concat(Constants.FORWARD_SLASH).concat(language)
                    .concat(ciConfigService.getSearchResultsPageNode());
            redirectTo = LinkResolver.changeInternalURLtoExternal(request,
                    LinkResolver.addHtmlExtension(redirectTo, Constants.Paths.CONTENT_EDC));

            RedirectHelper.redirectTo(response, redirectTo);
        } catch (Exception e) {
            log.error("CompanyInsight error searchCompanyByName ", e);
        }
    }
}
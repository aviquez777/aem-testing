package com.edc.edcportal.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.PagePathsHelper;
import com.edc.edcportal.core.helpers.RedirectHelper;

@SuppressWarnings("CQRules:CQBP-75")
@Component(immediate = true, 
            service = Servlet.class, 
            property = { 
                    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                    "sling.servlet.paths=" + Constants.Paths.OAUTH_REDIRECT_URL_EN,
                    "sling.servlet.paths=" + Constants.Paths.OAUTH_REDIRECT_URL_FR,
                    }
)

public class OauthRedirect extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(OauthRedirect.class);
    private static final long serialVersionUID = 6405694617359583726L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // prepare the redirect page
        String pageLanguage = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase();
        if (request.getPathInfo().contains(Constants.Paths.OAUTH_REDIRECT_URL_FR)) {
            pageLanguage = Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().toLowerCase();
        }
        String myEDCLandingPagePath = LinkResolver.changeInternalURLtoExternal(request, LinkResolver
                .addHtmlExtension(PagePathsHelper.getHomePagePath(pageLanguage), Constants.Paths.CONTENT_MYEDC));
        String redirectTo = StringUtils.remove(myEDCLandingPagePath, Constants.Paths.CONTENT_MYEDC);
        // Get the stateParm if any
        String stateParams = request.getParameter(Constants.Properties.QUERY_PARAM_STATE);
        String[] searchChars = new String[] { " ", "%20", ":" };
        String[] replaceChars = new String[] { "+", "+", "_" };
        stateParams = StringUtils.replaceEach(stateParams, searchChars, replaceChars);
        // if we have the state query string add it
        if (StringUtils.isNotBlank(stateParams)) {
            redirectTo = redirectTo.concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_QUESTION_MARK)
                    .concat(Constants.Properties.QUERY_PARAM_STATE)
                    .concat(Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_EQUAL_SIGN).concat(stateParams);
        }
        log.debug("OauthRedirect redirect to {}", redirectTo);
        RedirectHelper.redirectTo(response, redirectTo);
    }
}

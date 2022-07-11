package com.edc.edcportal.core.servlets;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.PagePathsHelper;

public class B2CServletActions {

    private B2CServletActions() {
        // Sonar Lint
    }

    public static EloquaUserProfileDO updateBasicProfile(SlingHttpServletRequest request,
            EloquaDAOService eloquaDAOService) throws EDCEloquaSystemException {
        Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
        // User info
        String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
        EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
        Map<String, String> headersForEloqua = LoginRequestHeadersUtil.getHeadersForEloqua(headers,
                eloquaDAOService.getEloquaConfigService());
        //Get the email info
        String emailKey = eloquaDAOService.getEloquaConfigService().getEmailIdFieldId();
        String oldEmail = eloquaUserProfileDO.getFormFieldsValues().get(emailKey).getEloquaValue();
        String newEmail = headersForEloqua.get(emailKey);
        // compare using equalsIgnoreCase 
        if(!newEmail.equalsIgnoreCase(oldEmail)) {
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getOldEmailId(), oldEmail);
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getEmailChangedId(), Constants.Properties.ELOQUA_EMAIL_CHANGED_FLAG_VALUE);
        }
        eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
        eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
        return eloquaUserProfileDO;
    }

    public static String redirectUserPath(SlingHttpServletRequest request, String pageLanguage, String actionType,
            Boolean result) {
        // no Lang use english
        if (StringUtils.isBlank(pageLanguage)) {
            pageLanguage = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation().toLowerCase();
        }

        String pageName = PagePathsHelper.getProfilePagePath(pageLanguage);

        if (pageName.equalsIgnoreCase(Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation())) {
            pageName = PagePathsHelper.getResourceNameFromAlias(request, pageName);
        }
        request.getSession().setAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR, result.toString() + "&" + actionType);
        pageName = LinkResolver.changeInternalURLtoExternal(request,
                LinkResolver.addHtmlExtension(pageName, Constants.Paths.CONTENT_MYEDC));
        return PagePathsHelper.removeContentPath(pageName);
    }
}

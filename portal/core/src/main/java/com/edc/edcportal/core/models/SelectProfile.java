package com.edc.edcportal.core.models;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.eloqua.pojo.EloquaDataItem;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.CookieHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LoginRequestHeadersUtil;
import com.edc.edcportal.core.helpers.RedirectHelper;
import com.edc.edcportal.core.services.FieldMappingConfigService;

@Model(adaptables = SlingHttpServletRequest.class)

public class SelectProfile {

    private static final Logger log = LoggerFactory.getLogger(SelectProfile.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    EloquaDAOService eloquaDAOService;

    @Inject
    FieldMappingConfigService fieldMappingConfigService;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void initModel() {
        // Get page language
        String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        // Get external system error page path
        String externalErrorPath = pageLanguage.equals("en") ? Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_EN
                : Constants.Paths.MYACCOUNT_SYSTEM_ERROR_PATH_FR;
        String redirectTo = null;
        try {
            try {
                // task 47788 Capture Language in Eloqua upon landing Select Profile page
                Map<String, String> headers = LoginRequestHeadersUtil.getHeaders(request);
                String externalId = headers.get(Constants.Properties.HEADER_EXTERNAL_ID);
                EloquaUserProfileDO eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
                String langKey = eloquaDAOService.getEloquaConfigService().getLangdId();
                String lang = LanguageUtil.getLanguage(request);
                Map<String, EloquaDataItem> profileFields = eloquaUserProfileDO.getFormFieldsValues();
                String initialTrafficSource = profileFields.containsKey(eloquaDAOService.getEloquaConfigService().getInitialTrafficSrc()) ? profileFields.get(eloquaDAOService.getEloquaConfigService().getInitialTrafficSrc()).getEloquaValue() : "";

                // we have no record, create the record just with the headers from Shibboleth
                Map<String, String> headersForEloqua = LoginRequestHeadersUtil.getHeadersForEloqua(headers,
                        eloquaDAOService.getEloquaConfigService());
                // task 47788 Capture Language in Eloqua add the language to headers
                headersForEloqua.put(langKey, lang);
                // UI 84500 Update Traffic src from Eloqua
                String trafficsrc = CookieHelper.getCookieValue(request, Constants.Properties.TRAFFIC_SOURCE_COOKIE_NAME);
                if (initialTrafficSource.isEmpty() && StringUtils.isNotBlank(trafficsrc)) {
                    headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getInitialTrafficSrc(), trafficsrc);
                }
                eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
                // send it to REST
                eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
            } catch (EDCEloquaSystemException e) {
                log.error(e.toString());
                redirectTo = externalErrorPath;
                RedirectHelper.redirectTo(response, redirectTo);
                return;
            }
            // Catch exception from RedirectHelper.redirectTo()
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public String getPostUrl() {
        return Constants.Paths.FRONT_CONTROLLER_SERVLET_URL;
    }
}

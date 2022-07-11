package com.edc.edcportal.core.servlets;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.helpers.FormFieldsUtil;
import com.edc.edcportal.core.models.ProfileTypeDefinition;
import com.edc.edcportal.core.services.FieldMappingConfigService;

/**
 * Action to take from the front controller.
 *
 */
public class ServletActions {

    protected static final Logger log = LoggerFactory.getLogger(ServletActions.class);

    private ServletActions() {
        // Sonar Qube
    }

    /**
     * selectProfileType update Eloqua CDO with the selected profile type.
     * 
     * @param request
     * @param eloquaDAOService
     * @param eloquaUserProfileDO
     * @param headersForEloqua
     * @return
     */
    public static String selectProfileType(SlingHttpServletRequest request, EloquaDAOService eloquaDAOService,
            EloquaUserProfileDO eloquaUserProfileDO, Map<String, String> headersForEloqua)
            throws EDCEloquaSystemException {
        String redirectTo = Constants.Paths.SELECT_PROFILE;
        String registerPagePath = Constants.Paths.REGISTER;
        // get the profile
        String profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                eloquaDAOService.getEloquaConfigService().getProfileFieldId());
        // No profile on record, get it form post
        if (StringUtils.isBlank(profileType)
                && StringUtils.isNotEmpty(request.getParameter(Constants.Properties.PROFILE_TYPE_FIELD))) {
            profileType = request.getParameter(Constants.Properties.PROFILE_TYPE_FIELD);
        }
        // if we have it from post, add it.
        if (StringUtils.isNotBlank(profileType)) {
            // add the profile type, alongside to the values to update
            headersForEloqua.put(eloquaDAOService.getEloquaConfigService().getProfileFieldId(), profileType);
            // add the headers as the values to update
            eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
            // send it to REST
            boolean result = eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
            // if saved, go to the register page
            if (result) {
                redirectTo = registerPagePath;
                request.getSession().setAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR,
                        Boolean.toString(result) + "&detailed");
            }
        }
        // Fixing Side effect of bug 390888
        return redirectTo.concat(Constants.HTML_EXTENSION);
    }

    /**
     * updateProfile update Eloqua CDO with the selected fields for the selected
     * profile type.
     * 
     * @param request
     * @param eloquaDAOService
     * @param eloquaUserProfileDO
     * @param headersForEloqua
     * @param fieldMappingConfigService
     * @param apimDAOService
     * @return
     */
    public static String updateProfile(SlingHttpServletRequest request, EloquaDAOService eloquaDAOService,
            EloquaUserProfileDO eloquaUserProfileDO, Map<String, String> headersForEloqua,
            FieldMappingConfigService fieldMappingConfigService, String action)
            throws EDCEloquaSystemException {
        // Set the redirect variables accordinlgly
        String redirectTo = Constants.Paths.PROFILE;
        String redirectToErr = Constants.Paths.EDIT_PROFILE;
        if(Constants.ACTION_NEW_PROFILE.equals(action)) {
            redirectTo = Constants.Paths.MYEDC_HOME_PAGE;
            redirectToErr = Constants.Paths.REGISTER;
        }
        // get the profile
        String profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                eloquaDAOService.getEloquaConfigService().getProfileFieldId());
        
        ProfileTypeDefinition profileTypeDefinition = FormFieldDefinitionsUtil.getProfileTypeDefinition(request, profileType);
        String[] filedList = FormFieldsUtil.getFormFileds(profileTypeDefinition.getFormFields());
        // get the fields mapping for the profile type
        Map<String, String> fieldMap = FormFieldDefinitionsUtil.getFieldMapping(request, profileTypeDefinition.getFormFields(),
                fieldMappingConfigService);
        eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithFieldData(eloquaUserProfileDO, fieldMap);
        // update the headers as well
        eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithMap(eloquaUserProfileDO, headersForEloqua);
        // update with whatever we have on the post data
        eloquaUserProfileDO = EloquaDAOUtils.updateEloquaUserProfileDOFromPost(eloquaUserProfileDO, filedList, request, fieldMappingConfigService);
        if(Constants.ACTION_RENEW_PROFILE.equals(action)) {
            redirectToErr = Constants.Paths.RENEWAL;
            eloquaUserProfileDO = EloquaDAOUtils.updateProfileDOWithRenewUTMS(eloquaUserProfileDO, request, fieldMappingConfigService);
        }
        
        // send it to REST
        boolean result = eloquaDAOService.updateUserProfile(eloquaUserProfileDO);
        // If not updated or not pop ready redirect to register or edit
        if(!result || !eloquaUserProfileDO.getPopReady()) {
            redirectTo = redirectToErr;
            // set the error flag to display on Front End, will clar on session helper when starting the login session
            request.getSession().setAttribute(Constants.Properties.PROFILE_HAS_ERRORS, true);
        }
        // Fixing Side effect of bug 390888
        return redirectTo.concat(Constants.HTML_EXTENSION);
    }
}

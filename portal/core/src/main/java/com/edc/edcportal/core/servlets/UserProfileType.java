package com.edc.edcportal.core.servlets;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.eloqua.EloquaConnectionManagerUtil;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.ServletResponseHelper;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET",
                "sling.servlet.paths=/bin/myedc/userprofiletype",
        },
        configurationPid = "com.edc.edcportal.core.servlets.UserProfileType"
)

public class UserProfileType extends SlingSafeMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(UserProfileType.class);
    private static final long serialVersionUID = 6483773290569461529L;

    @Reference
    @Inject
    private EloquaDAOService eloquaDAOService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        log.debug("UserProfileType GETTING");      
        ServletResponseHelper.writeResponse(response, createjSON(request).toString());
    }

    private JSONObject createjSON(SlingHttpServletRequest request) {
        JSONObject userInfo = new JSONObject();

        try {
            String profile = getProfileStatus(request);
            userInfo.put(Constants.Properties.PROFILE_TYPE_FIELD, profile);
        } catch (JSONException ex) {
            log.error("UserProfileType","JSON object creation error",ex);
        }

        return userInfo;
    }

    private String getProfileStatus(SlingHttpServletRequest request) {
        EloquaUserProfileDO eloquaUserProfileDO;

        try {
            String externalId = request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID);
            if (externalId != null) {
                eloquaUserProfileDO = eloquaDAOService.getUserProfileByExternalId(externalId);
                String profileType = EloquaConnectionManagerUtil.getELoquaValueFromDO(eloquaUserProfileDO,
                        eloquaDAOService.getEloquaConfigService().getProfileFieldId());
                if (!StringUtils.isBlank(profileType)) {
                    return profileType;
                } else {
                    return null;
                }
            }
        } catch (EDCEloquaSystemException e) {
            log.error(e.toString());
        }
        return null;
    }

    @Activate
    @Modified
    private void activate() {
        log.info("UserProfileType Servlet ------- Activated");
    }

}


package com.edc.edcportal.core.models;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class UpdateMessage {

    @Self
    private SlingHttpServletRequest request;

    private String profileUpdateStatus;
    private String actionType;

    /**
     * This method is responsible for initial assignment of model properties.
     * Initial values are loaded from the properties.
     *
     */
    @PostConstruct
    public void initModel() {
        HttpSession session = request.getSession();
        String temp = null;

        if (session.getAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR) != null ) {
            temp = request.getSession().getAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR).toString();
            this.profileUpdateStatus = temp.split("&")[0];
            this.actionType = temp.split("&")[1];
            request.getSession().removeAttribute(Constants.Properties.PROFILE_STATUS_SESSION_VAR);
        }
    }

    public String getLanguage() {
        return LanguageUtil.getLanguage(request);
    }

    public String getProfileUpdateStatus() { return  this.profileUpdateStatus; }

    public String getActionType() { return  this.actionType; }
}
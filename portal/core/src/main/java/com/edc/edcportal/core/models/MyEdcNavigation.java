package com.edc.edcportal.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.helpers.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class MyEdcNavigation {
    @Self
    private SlingHttpServletRequest request;

    private String linkUrlDashboard;
    private String linkUrlMyProfile;
    private String pagePath;

    @Inject
    private Page currentPage;

    @PostConstruct
    public void initModel() {
        String localPath = StringUtils.remove(currentPage.getPath(), Constants.Paths.CONTENT_MYEDC);
        String lang = Constants.FORWARD_SLASH.concat(getLanguage().toLowerCase());
        if (lang.equals("/en")) {
            this.linkUrlDashboard = lang.concat(Constants.Paths.MYACCOUNT).concat(Constants.Paths.MYEDC_HOME_PAGE).concat(Constants.HTML_EXTENSION);
            this.linkUrlMyProfile = lang.concat(Constants.Paths.MYACCOUNT).concat(Constants.Paths.PROFILE).concat(Constants.HTML_EXTENSION);
        } else {
            this.linkUrlDashboard = lang.concat(Constants.Paths.MYACCOUNT_FR).concat(Constants.Paths.MYEDC_HOME_PAGE_FR).concat(Constants.HTML_EXTENSION);
            this.linkUrlMyProfile = lang.concat(Constants.Paths.MYACCOUNT_FR).concat(Constants.Paths.PROFILE_FR).concat(Constants.HTML_EXTENSION);
            localPath = frenchPath(localPath);
        }
        this.pagePath = localPath;
    }

    private String frenchPath(String path) {
        path = StringUtils.replace(path, Constants.Paths.MYACCOUNT, Constants.Paths.MYACCOUNT_FR);
        if (path.contains(Constants.Paths.MYEDC_HOME_PAGE)) {
            path = StringUtils.replace(path, Constants.Paths.MYEDC_HOME_PAGE, Constants.Paths.MYEDC_HOME_PAGE_FR);
        } else if (path.contains(Constants.Paths.PROFILE)) {            
            path = StringUtils.replace(path, Constants.Paths.PROFILE, Constants.Paths.PROFILE_FR);
        }
        return path;
    }

    public String getLanguage() {
        return LanguageUtil.getLanguage(request);
    }

    public String getLinkUrlDashboard() {
        return this.linkUrlDashboard;
    }

    public String getLinkUrlMyProfile() {
        return this.linkUrlMyProfile;
    }

    public boolean isDashboard() {
        return this.linkUrlDashboard.contains(pagePath);
    }

    public boolean isMyProfile() {
        return this.linkUrlMyProfile.contains(pagePath);
    }

}
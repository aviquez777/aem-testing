package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.helpers.Constants;

@Model(adaptables = SlingHttpServletRequest.class)
public class CheckRunModeHelper {

    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    private boolean isAuthor = false;
    private boolean isPublish = false;

    @PostConstruct
    public void initModel() {
        this.isAuthor = slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_AUTHOR);
        this.isPublish = slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_PUBLISH);
    }

    public boolean isAuthor() {
        return this.isAuthor;
    }

    public boolean isPublish() {
        return this.isPublish;
    }
}

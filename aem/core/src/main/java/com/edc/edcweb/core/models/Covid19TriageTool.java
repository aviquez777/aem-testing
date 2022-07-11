package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;

@Model(adaptables = SlingHttpServletRequest.class)
public class Covid19TriageTool {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;
    
    @Reference
    @Inject
    private SlingSettingsService slingSettingsService;

    private String jsonUrl;

    @PostConstruct
    public void initModel() {
        jsonUrl = currentPage.getPath();
        if (slingSettingsService.getRunModes().toString().contains(Constants.RUN_MODE_PUBLISH)) {
            // remove the /content/edc for publsher only
            jsonUrl = StringUtils.replace(jsonUrl, Constants.Paths.CONTENT_EDC, "");
        }
        String jsonDialogProperty = properties.get(Constants.Paths.COVID_19_TRIAGE_TOOOL_JSON_DIALOG_PROPERTY, String.class);
        jsonUrl = jsonUrl.concat(Constants.Paths.FORWARD_SLASH).concat(Constants.Paths.COVID_19_TRIAGE_TOOOL_JCR_CONTENT).concat(jsonDialogProperty);
    }

    public String getJsonUrl() {
        return jsonUrl;
    }

}

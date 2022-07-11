package com.edc.edcportal.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class FormExtraInfoObjects {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private String path;

    private Map<String, String> options = new HashMap<>();

    @PostConstruct
    public void initModel() {

        if (path != null) {
            String enAbbr = Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation();
            String lang = getLanguageAbbr(request);
            Resource fieldResource = request.getResourceResolver().getResource(path);
            if (fieldResource != null) {
                FormFieldVariations formFieldExtraInfo = fieldResource.adaptTo(FormFieldVariations.class);
                options.put(Constants.EXTRA_INFO_LOV_API, formFieldExtraInfo.getLovApiName());
                options.put(Constants.EXTRA_INFO_FORM_GROUP_CLASS, formFieldExtraInfo.getFormGroupClass());
                options.put(Constants.EXTRA_INFO_CCS_CLASS, formFieldExtraInfo.getCssClass());
                options.put(Constants.EXTRA_INFO_START_FORM_ROW, formFieldExtraInfo.getStartFormRow());
                options.put(Constants.EXTRA_INFO_END_FORM_ROW, formFieldExtraInfo.getEndFormRow());
                options.put(Constants.EXTRA_INFO_END_VALIDATION_RULE,formFieldExtraInfo.getValidationRule());
                // Language dependent fields
                options.put(Constants.EXTRA_INFO_FIELD_TYPE, formFieldExtraInfo.getFieldType());
                options.put(Constants.EXTRA_INFO_TITLE, enAbbr.equalsIgnoreCase(lang) ? formFieldExtraInfo.getEnTitle()
                        : formFieldExtraInfo.getFrTitle());
                options.put(Constants.EXTRA_INFO_SUBTITLE, enAbbr.equalsIgnoreCase(lang) ? formFieldExtraInfo.getEnSubtitle()
                        : formFieldExtraInfo.getFrSubtitle());
                options.put(Constants.EXTRA_INFO_TOOLTIP, enAbbr.equalsIgnoreCase(lang) ? formFieldExtraInfo.getEnTooltip()
                        : formFieldExtraInfo.getFrTooltip());
                options.put(Constants.EXTRA_INFO_LABEL, enAbbr.equalsIgnoreCase(lang) ? formFieldExtraInfo.getEnLabel()
                        : formFieldExtraInfo.getFrLabel());
                options.put(Constants.EXTRA_INFO_PLACEHOLDER, enAbbr.equalsIgnoreCase(lang) ? formFieldExtraInfo.getEnPlaceholder()
                        : formFieldExtraInfo.getFrPlaceholder());
            }
        }
    }

    private static String getLanguageAbbr(SlingHttpServletRequest request) {

        String langAbbr = "";
        String url = request.getRequestURL().toString();
        langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(url,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        return langAbbr;
    }

    public Map<String, String> getOptions() {
        return options;
    }
}

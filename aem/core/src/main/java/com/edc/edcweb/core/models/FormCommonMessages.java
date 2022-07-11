package com.edc.edcweb.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import com.edc.edcweb.core.helpers.FormFieldsUtil;

/**
 * Model to handle globally the error messages for the forms
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class FormCommonMessages {
    private static final Logger log = LoggerFactory.getLogger(FormCommonMessages.class);

    @Self
    private SlingHttpServletRequest request;

    private Map<String,String> formErrorMessages = new HashMap<>();

    @PostConstruct
    public void initModel() {
        this.formErrorMessages = FormFieldsUtil.getFormErrorsListMap(this.request);
    }

    public Map<String, String> getFormErrorMessages() {
        return this.formErrorMessages;
    }
}

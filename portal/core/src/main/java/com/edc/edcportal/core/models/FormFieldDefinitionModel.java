package com.edc.edcportal.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.helpers.FormFieldDefinitionsUtil;
import com.edc.edcportal.core.services.FieldMappingConfigService;

@Model(adaptables = SlingHttpServletRequest.class)
public class FormFieldDefinitionModel {

    @Self
    private SlingHttpServletRequest request;
    
    
    @Inject
    private FieldMappingConfigService fieldMappingConfigService;

    private Map<String, FormFieldDefintion> fieldDefinitions = new HashMap<>();

    @PostConstruct
    public void initModel() {
        fieldDefinitions = FormFieldDefinitionsUtil.getDefinitions(request, fieldMappingConfigService);
    }

    public Map<String, FormFieldDefintion> getFieldDefinitions() {
        return fieldDefinitions;
    }

}

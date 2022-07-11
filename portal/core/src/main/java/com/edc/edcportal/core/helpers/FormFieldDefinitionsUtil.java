package com.edc.edcportal.core.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.models.FormFieldDefintion;
import com.edc.edcportal.core.models.ProfileTypeDefinition;
import com.edc.edcportal.core.services.FieldMappingConfigService;

public class FormFieldDefinitionsUtil {

    private FormFieldDefinitionsUtil() {
        // empty constructor
    }

    private static final Logger log = LoggerFactory.getLogger(FormFieldDefinitionsUtil.class);

    public static Map<String, FormFieldDefintion> getDefinitions(SlingHttpServletRequest request, // NOSONAR
            FieldMappingConfigService fieldMappingConfigService) {
        Map<String, FormFieldDefintion> fields = new LinkedHashMap<>();

        Resource fieldResource = request.getResourceResolver()
                .getResource(Constants.DataPaths.MYEDCDATA_FORM_FIELD_DEFINITIONS);

        if (fieldResource != null) {
            Iterable<Resource> children = fieldResource.getChildren();
            Iterator<Resource> itr = children.iterator();

            while (itr.hasNext()) {
                Resource item = itr.next();
                FormFieldDefintion fieldDefnition = item.adaptTo(FormFieldDefintion.class);
                if (fieldDefnition != null) {
                    String fieldName = fieldDefnition.getFieldName();
                    if (fieldName != null) {
                        try {
                            String[] arr = FormFieldsUtil.getFieldMappingFromConfig(fieldName,
                                    fieldMappingConfigService);
                            if (arr != null && arr.length > 1) {
                                fieldDefnition.setEloquaid(arr[1]);
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | NoSuchMethodException | SecurityException e) {
                            log.debug("Error getting method {} {}", fieldDefnition, e);
                        }
                        String lovPath = fieldDefnition.getLovPath();
                        if (Constants.FIELD_TYPE_SELECT.equals(fieldDefnition.getFieldType())
                                && StringUtils.isBlank(lovPath)) {
                            lovPath = Constants.DataPaths.MYEDCDATA + fieldName;
                            fieldDefnition.setLovPath(lovPath);
                        }
                        fields.put(fieldName, fieldDefnition);
                    } else {
                        log.debug("Not Field Name for {}", fieldDefnition);
                    }
                }
            }
        }
        return fields;
    }

    public static Map<String, String> getFieldMapping(SlingHttpServletRequest request, String formFields, // NOSONAR
            FieldMappingConfigService fieldMappingConfigService) {
        Map<String, String> fields = new LinkedHashMap<>();

        Resource fieldResource = request.getResourceResolver()
                .getResource(Constants.DataPaths.MYEDCDATA_FORM_FIELD_DEFINITIONS);

        if (fieldResource != null) {
            String[] fieldsToUse = FormFieldsUtil.getFormFileds(formFields);
            for (String field : fieldsToUse) {
                Resource item = fieldResource.getChild(field);
                if (item != null) {
                    FormFieldDefintion fieldDefnition = item.adaptTo(FormFieldDefintion.class);
                    if (fieldDefnition != null) {
                        String fieldName = fieldDefnition.getFieldName();
                        String eloquaId = null;
                        try {
                            String methodName = "get".concat(StringUtils.capitalize(fieldName)).concat("Map");
                            Method method = fieldMappingConfigService.getClass().getDeclaredMethod(methodName);
                            String[] arr = (String[]) method.invoke(fieldMappingConfigService);
                            eloquaId = FieldMapUtils.getEloquaId(arr);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | NoSuchMethodException | SecurityException e) {
                            log.debug("FormFieldDefinitionsUtil getFieldMapping Error getting method {} {}",
                                    fieldDefnition, e);
                        }

                        if (StringUtils.isNotBlank(eloquaId)) {
                            fields.put(eloquaId, fieldName);
                        } else {
                            log.debug("FormFieldDefinitionsUtil getFieldMapping Not EloquaID {} for {}", eloquaId,
                                    fieldDefnition.getFieldName());
                        }
                    }
                } else {
                    log.debug("FormFieldDefinitionsUtil getFieldMapping Not FormFieldDefintion for {}", field);
                }
            }
        }
        return fields;
    }

    public static ProfileTypeDefinition getProfileTypeDefinition(SlingHttpServletRequest request, String profileType) {
        ProfileTypeDefinition profileTypeDefinition = new ProfileTypeDefinition();
        Resource fieldResource = request.getResourceResolver().getResource(Constants.DataPaths.MYEDCDATA_PROFILE_TYPE);
        if (fieldResource != null) {
            Resource profileRes = fieldResource.getChild(profileType);
            if (profileRes != null) {
                profileTypeDefinition = profileRes.adaptTo(ProfileTypeDefinition.class);
            }
        }
        return profileTypeDefinition;
    }
}

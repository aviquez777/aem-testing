package com.edc.edcweb.core.gatedcontentaccess;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessConstats;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessUtils;
import com.edc.edcweb.core.gatedcontentaccess.pojo.GatedContentAccesJsonResponse;
import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfConstants;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;

public class GlgfDataObjectTransformationHelper {

    GlgfDataObjectTransformationHelper() {
        // SonarQube
    }

    /**
     * Transform the EloquaContact fields into a GatedContentAccesJsonResponse which
     * contains only the questions not previously answered, the forced ones or the
     * dependent ones
     * 
     * @param eloquaContact
     * @param questions
     * @return GatedContentAccesJsonResponse, empty object if not fields required
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static GatedContentAccesJsonResponse getCGLGFJsonResponse(EloquaContact eloquaContact, Resource questions) {
        GatedContentAccesJsonResponse json = new GatedContentAccesJsonResponse();
        List<String> fields = new LinkedList<>();
        Iterator<Resource> questionList = questions.listChildren();
        ResourceResolver resourceResolver = questions.getResourceResolver();
        while (questionList.hasNext()) {
            Resource question = questionList.next();
            String questionToUsePath = question.getValueMap().get(GlgfConstants.QUESTION_TO_USE, String.class);
            Resource questionRes = resourceResolver.getResource(questionToUsePath);
            String fieldName = questionRes.getValueMap().get(GlgfConstants.QUESTION_FIELD_NAME, String.class);
            boolean questionOverride = question.getValueMap().get(GatedContentAccessConstats.QUESTION_OVERRIDE_PROPERTY,
                    false);
            String[] fieldsArr = fieldName.split(",");
            // Question Override, add it
            if (questionOverride) {
                addFields(fieldsArr, fields);
            } else {
                checkValues(eloquaContact, fieldsArr, fields);
            }
            json.setFields(fields);
        }
        return json;
    }

    private static void checkValues(EloquaContact eloquaContact, String[] fieldsArr, List<String> fields) {
        // check if the value is empty
        for (String fieldToCheck : fieldsArr) {
            // Do not Check optional fields
            if (!fieldToCheck.equals(GlgfConstants.QUESTION_PHONE_EXT_FIELD_NAME) && !fieldToCheck.equals(GlgfConstants.QUESTION_ADD_LINE_2)) {
                boolean showField = GatedContentAccessUtils.isShowField(eloquaContact, fieldToCheck);
                if (showField) {
                    addFields(fieldsArr, fields);
                    break;
                }
            }
        }
    }

    private static void addFields(String[] fieldsArr, List<String> fields) {
        if (fieldsArr.length == 1) {
            fields.add(fieldsArr[0]);
        } else {
            fields.addAll(Arrays.asList(fieldsArr));
        }
    }
}

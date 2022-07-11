package com.edc.edcweb.core.gatedcontentaccess;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessConstats;
import com.edc.edcweb.core.gatedcontentaccess.helpers.GatedContentAccessUtils;
import com.edc.edcweb.core.gatedcontentaccess.pojo.GatedContentAccesJsonResponse;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;


public class DataObjectTransformationHelper {

    private DataObjectTransformationHelper() {
        // Sonar Lint
    }

    /**
     * Select which component to use, the original gated access form or the new
     * generic one
     * 
     * @param eloquaContact
     * @param questions
     * @return jsno ith questions to show
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static GatedContentAccesJsonResponse populateJsonResponse(EloquaContact eloquaContact, Resource questions) {
        GatedContentAccesJsonResponse json;
        // use Gated Content Access form
        if (questions.getParent().getResourceType().equals(GatedContentAccessConstats.FORM_GLGF_RESOURCE_TYPE)) {
            json = GlgfDataObjectTransformationHelper.getCGLGFJsonResponse(eloquaContact, questions);
        } else {
            json = getGCAJsonResponse(eloquaContact, questions);
        }
        return json;
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
    public static GatedContentAccesJsonResponse getGCAJsonResponse(EloquaContact eloquaContact, Resource questions) {
        GatedContentAccesJsonResponse json = new GatedContentAccesJsonResponse();
        List<String> fields = new LinkedList<>();
        Map<String, List<String>> dependencyMap = new HashMap<>();
        Iterator<Resource> questionList = questions.listChildren();
        while (questionList.hasNext()) {
            Resource question = questionList.next();
            String fieldName = question.getValueMap().get(GatedContentAccessConstats.QUESTION_TYPE_PROPERTY,
                    String.class);
            boolean questionOverride = question.getValueMap().get(GatedContentAccessConstats.QUESTION_OVERRIDE_PROPERTY,
                    false);
            if (StringUtils.isNotBlank(fieldName)) {
                if (questionOverride) {
                    fields.add(fieldName);
                } else {
                    Resource dependsOn = question.getChild(GatedContentAccessConstats.QUESTION_DEPENDS_ON_PROPERTY);
                    // Set any dependencies to check later on
                    if (dependsOn != null) {
                        setDependencyMap(dependencyMap, fieldName, dependsOn);
                    }
                    // check if the value is empty
                    boolean showField = GatedContentAccessUtils.isShowField(eloquaContact, fieldName);
                    if (showField) {
                        fields.add(fieldName);
                    }
                }
            }
            checkDependencies(dependencyMap, fields);
            json.setFields(fields);
        }
        return json;
    }

    /**
     * Set the dependencyMap with the dependent field as key, to compare later
     * 
     * @param dependencyMap: to store the data
     * @param fieldName:     to use as key
     * @param dependsOn:     multifield resource
     */
    private static void setDependencyMap(Map<String, List<String>> dependencyMap, String fieldName,
            Resource dependsOn) {
        Iterator<Resource> dependencies = dependsOn.listChildren();
        List<String> dependencyList = new LinkedList<>();
        while (dependencies.hasNext()) {
            Resource dependency = dependencies.next();
            String dependencyName = dependency.getValueMap().get(GatedContentAccessConstats.QUESTION_TYPE_PROPERTY,
                    String.class);
            if (StringUtils.isNotBlank(dependencyName)) {
                dependencyList.add(dependencyName);
            }
        }
        dependencyMap.put(fieldName, dependencyList);
    }

    /**
     * if the dependent is not on the field list, but any of the other dependents
     * are, add the depended to the list
     * 
     * @param dependencyMap
     * @param fields
     */
    private static void checkDependencies(Map<String, List<String>> dependencyMap, List<String> fields) {
        for (Map.Entry<String, List<String>> entry : dependencyMap.entrySet()) {
            String dependent = entry.getKey();
            // if the dependent is not on the field list, but any of the other dependents
            // are, add the depended to the list
            if (!fields.contains(dependent) && CollectionUtils.containsAny(fields, entry.getValue())) {
                fields.add(dependent);
            }
        }
    }
}

package com.edc.edcweb.core.helpers.itm;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcweb.core.helpers.formvalidation.FormCleaner;

public class ITMHelper {

    private ITMHelper() {
        // Sonar lint
    }

    /**
     * parseITMResponses Get the submitted ITM values and build a map with the
     * letter as key and values as list, to populate the Expected JSON Key
     * 
     * @param request
     * @param itmValues
     * @return Map with the letter as key and values as list, to populate the
     *         Expected JSON Key
     */
    public static Map<String, List<String>> parseITMResponses(SlingHttpServletRequest request, String itmValues) {
        Map<String, List<String>> responses = new LinkedHashMap<>();
        String[] itmValuesArr = StringUtils.split(itmValues);
        for (String item : itmValuesArr) {
            // Expect a two letter array
            if (StringUtils.length(item) == 1) {
                // apply the same vaiue to all the answers
                applySameResponseToAll(responses, item);
            } else {
                // Use the values
                getDiscreteValues(responses, item, request);
            }
        }
        return responses;
    }

    /**
     * applySameResponseToAll User selected I do not know (K) / Prefer not to answer
     * (P) / None of the above (A)
     * 
     * @param responses
     * @param item      the actual response
     */
    private static void applySameResponseToAll(Map<String, List<String>> responses, String item) {
        // Create an array with possible values
        String[] itmIdxArr = { ITMConstants.ITM_MAJORITY_VALUE, ITMConstants.ITM_MINORITY_VALUE,
                ITMConstants.ITM_LEADERSHIP_VALUE };
        List<String> answers = new LinkedList<>();
        // Loop Over the possible values and assign as necessary
        for (String index : itmIdxArr) {
            if (responses.containsKey(index)) {
                answers = responses.get(index);
            }
            if (!answers.contains(item)) {
                answers.add(item);
            }
            responses.put(index, answers);
        }
    }

    /**
     * getDiscreteValues Split the two letters, first one is the question selected
     * (M,N,L), second one is the sub question (W,G,L, etc) Build a map with the
     * letter as key and values as list, to populate the Expected JSON Key
     * 
     * @param responses
     * @param item
     * @param request
     */
    private static void getDiscreteValues(Map<String, List<String>> responses, String item,
            SlingHttpServletRequest request) {
        List<String> answers = new LinkedList<>();
        String index = StringUtils.left(item, 1);
        String value = StringUtils.right(item, 1);
        // example MO = Other minority, index = M, value = O
        if (ITMConstants.ITM_GROUP_OTHER_VALUE.equalsIgnoreCase(value)) {
            String otherFieldName = item.toLowerCase().concat(ITMConstants.ITM_GROUP_OTHER_TEXT);
            // Fetch the value from the input field
            String otherVal = FormCleaner.cleanAll(request.getParameter(otherFieldName));
            if (StringUtils.isNotBlank(otherVal)) {
                // Concatenate as requested "O:<Field Value>",
                // otherwise use the "O" already in value (Task 368086)
                value = ITMConstants.ITM_GROUP_OTHER_VALUE_FOR_LIST.concat(otherVal);
            }
        }
        if (responses.containsKey(index)) {
            answers = responses.get(index);
        }
        answers.add(value);
        responses.put(index, answers);
    }

}

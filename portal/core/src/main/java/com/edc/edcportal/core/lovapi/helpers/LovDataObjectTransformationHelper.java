package com.edc.edcportal.core.lovapi.helpers;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.lovapi.pojo.GenericLov;
import com.edc.edcportal.core.lovapi.pojo.LovItem;

/**
 * Transforms data between API and required format
 *
 */
public class LovDataObjectTransformationHelper {

    private LovDataObjectTransformationHelper() {
        // Sonar Lint
    }

    /**
     * Transform the lov list from API to the AEM's policy structure to reuse same
     * mark up
     * 
     * @param lov      List from API
     * @param language to use the proper name
     * @return List<LovItem> same elements on input list
     */
    public static List<LovItem> lovToLovItem(List<GenericLov> lov, String language) {
        List<LovItem> listLov = new LinkedList<>();
        if (lov != null) {
            for (GenericLov item : lov) {
                String itemVal = item.getValue();
                // Some items are null, should not be the case
                if (StringUtils.isNotBlank(itemVal)) {
                    LovItem lovItem = new LovItem();
                    String itemId = item.getEnName().replaceAll("\\s", "").toLowerCase();
                    lovItem.setLovValue(itemVal);
                    lovItem.setLovId(itemId);
                    // BUG 383256
                    lovItem.setLovEnName(item.getEnName());
                    // Use proper lov name
                    if (language.equalsIgnoreCase(Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation())) {
                        lovItem.setLovName(item.getEnName());
                    } else {
                        lovItem.setLovName(item.getFrName());
                    }
                    listLov.add(lovItem);
                }
            }
        }
        return listLov;
    }

    /**
     * Transform the single lov from API to the AEM's policy structure to reuse same
     * mark up
     * 
     * @param item     Single lov from API
     * @param language to use the proper name
     * @return List<LovItem> same elements on input list
     */
    public static LovItem singleLovToLovItem(GenericLov item, String language) {
        LovItem lovItem = new LovItem();
        if (item != null) {
            String itemId = item.getEnName().replaceAll("\\s", "").toLowerCase();
            lovItem.setLovValue(item.getValue());
            lovItem.setLovId(itemId);
            // Use proper lov name
            if (language.equalsIgnoreCase(Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation())) {
                lovItem.setLovName(item.getEnName());
            } else {
                lovItem.setLovName(item.getFrName());
            }
        }
        return lovItem;
    }

}

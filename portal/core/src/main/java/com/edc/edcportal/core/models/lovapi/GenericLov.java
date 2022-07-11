package com.edc.edcportal.core.models.lovapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.lovapi.LovApiJsonDataBindingUtil;
import com.edc.edcportal.core.lovapi.helpers.LovApiConstants;
import com.edc.edcportal.core.lovapi.helpers.LovDataObjectTransformationHelper;
import com.edc.edcportal.core.lovapi.pojo.GenericLovDO;
import com.edc.edcportal.core.lovapi.pojo.LovItem;
import com.edc.edcportal.core.lovapi.service.LovApiDAOService;

/**
 * 
 * GenericLov Create a Sling model which will receive the lov type as parameter
 * The sling model will call the respective API point as per the parameter The
 * sling model will receive the data from the API and transform it to be
 * consumed by the HTL Modify the HTL mark up to use the new sling model.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class GenericLov {

    private static final Logger log = LoggerFactory.getLogger(GenericLov.class);

    @Inject
    LovApiDAOService lovApiDAOService;

    @Inject
    private Page currentPage;

    // lov type as parameter from htl for re-usability
    @Inject
    @Optional
    private String lovType;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean splitList;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean otherLast;

    @Inject
    @Optional
    @Default(values = LovApiConstants.OTHER_VALUE)
    private String otherValue;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean returnOptionsMap;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean useEnValAsCode;

    // re-used the variable name as on MultiFieldPolicyReader
    // to minimize code changes
    private List<LovItem> valueMapList = Collections.emptyList();
    private List<LovItem> firstHalfList = Collections.emptyList();
    private List<LovItem> secondHalfList = Collections.emptyList();
    private Map<String, String> options = new LinkedHashMap<>();

    @PostConstruct
    protected void init() {
        if (lovType != null) {
            GenericLovDO genericLovDO = new GenericLovDO();
            String responseJson = lovApiDAOService.getLov(lovType);
            try {
                genericLovDO = LovApiJsonDataBindingUtil.jsonToGenericLovDO(responseJson);
            } catch (IOException e) {
                // Unable to transform, set the error message to display a message to the user
                genericLovDO.setErrorMessage("error");
                log.error("GenericLov error on jsonToGenericLovDO {}", e.getMessage());
            }
            // Return a similar object as the one MultiFieldPolicyReader
            String language = currentPage.getLanguage().getLanguage();
            valueMapList = LovDataObjectTransformationHelper.lovToLovItem(genericLovDO.getResult(), language);
            if (otherLast) {
                putOtherLast();
            }

            if (splitList) {
                splitTheList();
            }

            if (returnOptionsMap) {
                doOptionsMap();
            }
        } else {
            log.error("GenericLov no lov type received");
        }
    }

    private void doOptionsMap() {
        for (LovItem item : valueMapList) {
            String value = item.getLovValue();
            // Bug 383256 submit EN Name as Name if required
            if (useEnValAsCode) {
                value = item.getLovEnName();
            }
            options.put(value, item.getLovName());
        }
    }

    private void splitTheList() {
        // Collections.emptyList() does not allow adding items
        List<LovItem> tempFrstHalfList = new LinkedList<>();
        List<LovItem> tempSecondHalfList = new LinkedList<>();
        for (int i = 0; i < this.valueMapList.size(); i++) {
            if (i < valueMapList.size() / 2) {
                tempFrstHalfList.add(this.valueMapList.get(i));
            } else {
                tempSecondHalfList.add(this.valueMapList.get(i));
            }
        }
        this.firstHalfList = tempFrstHalfList;
        this.secondHalfList = tempSecondHalfList;
    }

    private void putOtherLast() {
        LovItem otherItem = null;
        for (LovItem item : this.valueMapList) {
            if (item.getLovValue().equals(otherValue)) {
                // Use Correct Value for Other
                if (useEnValAsCode) {
                    item.setLovEnName(item.getLovValue());
                }
                otherItem = item;
                this.valueMapList.remove(item);
                break;
            }
        }
        if (otherItem != null) {
            this.valueMapList.add(otherItem);
        }
    }

    // same getter as MultiFieldPolicyReader
    public List<LovItem> getValueMapList() {
        return valueMapList;
    }

    public void setValueMapList(List<LovItem> valueMapList) {
        this.valueMapList = valueMapList;
        valueMapList = new ArrayList<>(valueMapList);
        this.valueMapList = Collections.unmodifiableList(valueMapList);
    }

    public List<LovItem> getFirstHalfList() {
        return firstHalfList;
    }

    public void setFirstHalfList(List<LovItem> firstHalfList) {
        this.firstHalfList = firstHalfList;
        firstHalfList = new ArrayList<>(firstHalfList);
        this.firstHalfList = Collections.unmodifiableList(firstHalfList);
    }

    public List<LovItem> getSecondHalfList() {
        return secondHalfList;
    }

    public void setSecondHalfList(List<LovItem> secondHalfList) {
        this.secondHalfList = secondHalfList;
        secondHalfList = new ArrayList<>(secondHalfList);
        this.secondHalfList = Collections.unmodifiableList(secondHalfList);
    }

    public Map<String, String> getOptions() {
        return options;
    }

}

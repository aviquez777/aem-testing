package com.edc.edcportal.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.FormFieldsUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class FormCommonObjects {

    @Self
    private SlingHttpServletRequest request;
    
    @Inject
    @Optional
    private String path;

    public Map<String, String> getOptionsFromDataSourcePath() {
        Map<String, String> options = new HashMap<>();
        Boolean sort = false;
        if(path != null) {
            String[] topList = null;
            // sort only for countries and marketsInt
            if(path.contains("countries") || path.contains("marketsInt")) {
                sort = true;
                if(path.contains("countries")) {
                   topList = new String[2];
                   topList[0] = Constants.CAN_COUNTRY_CODE;
                   topList[1] = Constants.USA_COUNTRY_CODE;
                }
            }
            options = FormFieldsUtil.getFormFieldOptionsMap(request, path, sort, null, topList, null);
        }
        return options;
    }

}

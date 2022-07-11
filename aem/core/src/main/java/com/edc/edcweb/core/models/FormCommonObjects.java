package com.edc.edcweb.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class FormCommonObjects {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private String path;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean sort;

    /**
     * Comma separated string, with the values to be excluded from the list like
     * "CAN,USA"
     */
    @Inject
    @Optional
    private String list;

    public Map<String, String> getOptionsFromDataSourcePath() {
        Map<String, String> options = new HashMap<>();
        if (path != null) {
            String[] excludeList = list != null ? list.split(",") : null;
            String[] topList = null;
            // sort only for countries and marketsInt
            if (path.contains(Constants.Properties.COUNTRIES)
                    || path.contains(Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_MARKETSINS)) {
                sort = true;
                if (path.contains(Constants.Properties.COUNTRIES)) {
                    topList = new String[2];
                    topList[0] = Constants.Properties.CAN_COUNTRY_CODE;
                    topList[1] = Constants.Properties.USA_COUNTRY_CODE;
                }
            }
            options = FormFieldsUtil.getFormFieldOptionsMap(request, path, sort, excludeList, topList);
        }
        return options;
    }
}

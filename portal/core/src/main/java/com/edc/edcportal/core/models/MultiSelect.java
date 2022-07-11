package com.edc.edcportal.core.models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.helpers.FormFieldsUtil;

@Model(adaptables = SlingHttpServletRequest.class)
public class MultiSelect {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    @Optional
    private String lovPath;

    private Map<String, Map<String, String>> listOfValues = new LinkedHashMap<>();

    @PostConstruct
    public void initModel() {
        if (StringUtils.isNotBlank(lovPath)) {
            String lang = currentPage.getLanguage().getLanguage();
            listOfValues =  FormFieldsUtil.getMutiSelectOptions(request, lovPath, lang);
        }

    }

    public Map<String, Map<String, String>> getListOfValues() {
        return listOfValues;
    }

}
package com.edc.edcportal.core.models.ci;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMMode;
import com.edc.edcportal.core.ci.CiConstants;
import com.edc.edcportal.core.ci.helpers.DataObjectTransformationHelper;
import com.edc.edcportal.core.ci.services.CiConfigService;
import com.edc.edcportal.core.ci.services.CiDAOService;
import com.edc.edcportal.core.exception.EDCAPIMSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.JSONReaderHelper;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.RedirectHelper;

@Model(adaptables = SlingHttpServletRequest.class)

public class CompanyProfile {

    private static final Logger log = LoggerFactory.getLogger(CompanyProfile.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private CiDAOService ciDAOService;

    @Inject
    private CiConfigService ciConfigService;

    private Map<String, Map<String, String>> sectionsMap = new LinkedHashMap<>();
    private Map<String, String> sectionTitlesMap = new LinkedHashMap<>();
    private Map<String, String> fieldLabelsMap = new LinkedHashMap<>();
    private Map<String, String> sectionClassesMap = new LinkedHashMap<>();
    private Map<String, String> sectionNullMap = new LinkedHashMap<>();
    private String companyName;

    @PostConstruct
    public void initModel() throws IOException {
        String[] pageSelectors = request.getRequestPathInfo().getSelectors();
        boolean doRedirect = false;
        String redirectTo = getLanadingPageNode();
        // Get the company ID from page selector, we want only ONE selector
        if (pageSelectors.length == 1) {
            String companyIdSel = pageSelectors[0];
            if (StringUtils.isNotBlank(companyIdSel)) {
                try {
                    String language = LanguageUtil.getLanguageAbbreviationFromPath(request.getPathInfo(),
                            Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
                    JSONObject jsonObject = ciDAOService.getCompanyDetailsById(companyIdSel, language);
                    String companyId = JSONReaderHelper.getStringValue(CiConstants.JSON_COMPANY_ID_KEY, jsonObject);
                    if (StringUtils.isBlank(companyId)) {
                        doRedirect = true;
                    } else {
                        companyName = JSONReaderHelper.getStringValue(CiConstants.JSON_COMPANY_NAME_KEY, jsonObject);
                        Map<String, List<String>> sectionMapConfig = getSectionMapConfig();
                        String mapFieldsList = request.getResource().getValueMap().get("mapFieldsList", String.class);
                        if (StringUtils.isBlank(mapFieldsList)) {
                            mapFieldsList = ciConfigService.getMapKeys();
                        }
                        sectionsMap = DataObjectTransformationHelper.getSections(sectionMapConfig, jsonObject,
                                mapFieldsList);
                    }
                } catch (EDCAPIMSystemException | IOException e) {
                    log.error("CompanyProfile initModel error {}", e.getMessage());
                }
            }
        } else {
            // Redirect on Publish
            WCMMode mode = WCMMode.fromRequest(request);
            if (mode.equals(WCMMode.DISABLED)) {
                doRedirect = true;
            } else {
                // Add Dummy Data for author only
                doRedirect = false;
                companyName = "Sample Company Name";
                Map<String, List<String>> sectionMapConfig = getSectionMapConfig();
                sectionsMap = DataObjectTransformationHelper.getSectionsForAuthor(sectionMapConfig);
            }
        }
        if (doRedirect) {
            RedirectHelper.redirectTo(response, redirectTo);
        }
    }

    private Map<String, List<String>> getSectionMapConfig() {
        Map<String, List<String>> sectionMap = new LinkedHashMap<>();
        Resource fieldMapRes = request.getResource().getChild("fieldMap");
        if (fieldMapRes != null) {
            Iterator<Resource> keyMapRes = fieldMapRes.listChildren();
            while (keyMapRes.hasNext()) {
                List<String> list = new LinkedList<>();
                Resource keyRes = keyMapRes.next();
                String key = StringUtils.trim(keyRes.getValueMap().get("keyName", String.class));
                // Add the title
                String title = StringUtils.trim(keyRes.getValueMap().get("title", String.class));
                sectionTitlesMap.put(key, title);
                String sectionClass = StringUtils.trim(keyRes.getValueMap().get("sectionClass", String.class));
                if (StringUtils.isNotBlank(sectionClass)) {
                    sectionClassesMap.put(key, sectionClass);
                }
                // Check if there's a value to show as default
                sectionNullMap.put(key, StringUtils.trim(keyRes.getValueMap().get("displayIfNull", String.class)));
                Resource fieldListRes = keyRes.getChild("fieldList");
                if (fieldListRes != null) {
                    Iterator<Resource> fieldsRes = fieldListRes.listChildren();
                    while (fieldsRes.hasNext()) {
                        Resource fieldNameRes = fieldsRes.next();
                        String fieldName = StringUtils.trim(fieldNameRes.getValueMap().get("fieldName", String.class));
                        list.add(fieldName);
                        // get the field label if any
                        String fieldLabel = StringUtils
                                .trim(fieldNameRes.getValueMap().get("fieldLabel", String.class));
                        if (StringUtils.isNotBlank(fieldLabel)) {
                            fieldLabelsMap.put(fieldName, fieldLabel);
                        }
                    }
                }
                sectionMap.put(key, list);
            }
        }
        return sectionMap;
    }

    private String getLanadingPageNode() {
        String language = LanguageUtil.getLanguageAbbreviationFromPath(request.getPathInfo(),
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String pageUrl = Constants.Paths.CONTENT_EDC.concat(Constants.FORWARD_SLASH).concat(language)
                .concat(ciConfigService.getLandingSearchPageNode());
        pageUrl = LinkResolver.changeInternalURLtoExternal(request,
                LinkResolver.addHtmlExtension(pageUrl, Constants.Paths.CONTENT_EDC));
        return pageUrl;
    }

    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("searchPageNode", ciConfigService.getSearchResultsPageNode());
        data.put("companyName", companyName);
        data.put("mapBaseUrl", getMapBaseUrl());
        data.put("mapLinkVar", CiConstants.MAP_LINK_VAR);
        return data;
    }

    public Map<String, Map<String, String>> getSectionsMap() {
        return sectionsMap;
    }

    public Map<String, String> getSectionTitlesMap() {
        return sectionTitlesMap;
    }

    public Map<String, String> getFieldLabelsMap() {
        return fieldLabelsMap;
    }

    public Map<String, String> getSectionClassesMap() {
        return sectionClassesMap;
    }

    private String getMapBaseUrl() {
        String mapBaseUrl = request.getResource().getValueMap().get("mapBaseUrl", String.class);
        if (StringUtils.isBlank(mapBaseUrl)) {
            mapBaseUrl = ciConfigService.getMapBaseUrl();
        }
        return mapBaseUrl;
    }

    public Map<String, String> getSectionNullMap() {
        return sectionNullMap;
    }
}
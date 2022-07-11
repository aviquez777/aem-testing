/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;
import com.edc.edcweb.core.helpers.ehh.EHHHelper;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

/**
 *
 * @author lauren.alfaro
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class EhhFilter {

	private static final Logger log= LoggerFactory.getLogger(EhhFilter.class);
    private String startText;
    private String middleText;
    private String endText;
    private String searchPagePath;
    private Map<String, String> markets = new LinkedHashMap<>();

    @Self
    SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @PostConstruct
    protected void init() {

    	 log.info(EhhFilter.class.getName() + " Method Name init() [IN]");

        request.setAttribute("multifield", "categories");
        markets = EHHHelper.getMarketsMap(properties.get("marketstag", String.class), currentPage, request);

        // Accessibility policies
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);

        if (contentPolicy != null) {
            ValueMap policy_properties = contentPolicy.getProperties();

            this.startText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_START_DIRECTIONS_TEXT, String.class);
            this.middleText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_MIDDLE_DIRECTIONS_TEXT, String.class);
            this.endText = policy_properties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_END_DIRECTIONS_TEXT, String.class);
        }

        // build the search results page path based on the current language
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        searchPagePath = languageAbbreviation.equalsIgnoreCase(ConstantsEHH.EHHProperties.ENGLISH_ABBR) ? ConstantsEHH.Paths.EXPORT_HELP_HUB_SEARCH : ConstantsEHH.Paths.EXPORT_HELP_HUB_SEARCH_ALIAS;
        searchPagePath = Constants.Paths.CONTENT_EDC + "/" + languageAbbreviation.toLowerCase() + searchPagePath;
        searchPagePath = LinkResolver.addHtmlExtension(searchPagePath);
        searchPagePath = LinkResolver.changeInternalURLtoExternal(request, searchPagePath);

        log.info(EhhFilter.class.getName() + " Method Name init() [OUT]");
    }

    public Map<String, String> getMarkets() {
        return markets;
    }

    public String getStartText() {
        return startText;
    }

    public String getMiddleText() {
        return middleText;
    }

    public String getEndText() {
        return endText;
    }

    public String getSearchPagePath() {
        return searchPagePath;
    }
}

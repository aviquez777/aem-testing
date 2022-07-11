package com.edc.edcweb.core.models.ehh;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;
import com.edc.edcweb.core.helpers.LanguageUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This class is the model for the EHH Search CTA
 * to determine the search results page path
 *
 * @author  ACN
 * @version 1.0
 * @since   2019-02-08
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class EHHSearch {
    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private String searchPagePath;

    @PostConstruct
    public void initModel() {
        // build the search results page path based on the current language
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        searchPagePath = languageAbbreviation.equalsIgnoreCase(ConstantsEHH.EHHProperties.ENGLISH_ABBR) ? ConstantsEHH.Paths.EXPORT_HELP_HUB_SEARCH : ConstantsEHH.Paths.EXPORT_HELP_HUB_SEARCH_ALIAS;
        searchPagePath = Constants.Paths.CONTENT_EDC + "/" + languageAbbreviation.toLowerCase() + searchPagePath;
    }

    /**
     * Returns the search results page path
     * @return the path
     */
    public String getSearchPagePath() {
        return searchPagePath;
    }
}

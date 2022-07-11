package com.edc.edcweb.core.search.filters;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filters pages by template and tags to match.
 */
public class ResourcePageFilter extends PageFilter {

    /**
     * Tags to match.
     */
    private String[] tagsToMatch;

    /**
     * Constructor.
     *
     * @param tagsToMatch Tags to match.
     * @param hideNav     hide the ones with this flag on
     */
    public ResourcePageFilter(String[] tagsToMatch) {
        if (tagsToMatch != null) {
            this.tagsToMatch = Arrays.copyOf(tagsToMatch, tagsToMatch.length);
        }
    }

    /**
     * If a page should be included or not.
     *
     * @param page Page
     * @return True if the page matches the criteria, false otherwise.
     */
    @Override
    public boolean includes(Page page) {
        return (tagsToMatch == null || tagsToMatch.length == 0 || contains(page, tagsToMatch)) && super.includes(page);
    }

    /**
     * Verifies if the provided page contains any of the given tags.
     *
     * @param page Page
     * @return True if the page contains any of the given tags, false otherwise
     */
    private boolean contains(Page page, String[] tags) {
        List<String> pageTags = Arrays.stream(page.getTags()).map(Tag::getTagID).collect(Collectors.toList());
        return Arrays.stream(tags).anyMatch(pageTags::contains);
    }
}

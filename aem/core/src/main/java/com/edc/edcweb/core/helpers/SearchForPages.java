package com.edc.edcweb.core.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchForPages
{
	private static final Logger log = LoggerFactory.getLogger(SearchForPages.class);

	private SearchForPages()
	{

	}

	private static void  logDebugTagIds( String[] tagIds, boolean matchAny, String startingPage )
	{
		if(log.isDebugEnabled( ))
       {

		   log.debug("TagIds:");
		   log.debug("----------------------");
	       for(String tagId : tagIds)
	       {
	    	   log.debug(tagId);
	       }
		   log.debug("matchAny: " + matchAny);
		   log.debug("startingPage: " + startingPage);
       }

	}
	/**
	 * Get all pages that have the given tag ids.
	 *
	 * @param  request  Originating request ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
	 * @param  tagIds  String array containing all tags to be found. Depending on the value of {@code matchAny} a page either has to have all or any of these tags to be included in the results.
	 * @param  matchAny  If true, a page with just one of the {@code tagIds} will be included in the results. If false, only pages with all will be included.
	 * @param  startingPage  Page at which we will begin our search.
	 * @return Pages that match the search criteria.
	 */
	public static List<Page> getPagesByTagIds(SlingHttpServletRequest request, String[] tagIds, boolean matchAny, String startingPage)
	{
       List<Page> listItems = new ArrayList<>();
       //----------------------------------------------------------------------
       // SearchForPages.logDebugTagIds(tagIds, matchAny, startingPage );  // Commenting out since this is throwing an error

       //----------------------------------------------------------------------
       if (ArrayUtils.isNotEmpty(tagIds)) {
           Page rootPage = request.getResourceResolver().resolve(startingPage).adaptTo(Page.class);
    	   log.debug("rootPage: " + rootPage);
           if (rootPage != null) {
        	   log.debug("rootPage.getPath(): " + rootPage.getPath());
               TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
               if (tagManager != null) {
                   RangeIterator<Resource> resourceRangeIterator = tagManager.find(rootPage.getPath(), tagIds, matchAny);
                   PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);

                   SearchForPages.fillListPage(resourceRangeIterator, pageManager, listItems);

               }
           }
       }
       listItems.sort(new ArticlePageHelper.ListSort(ArticlePageHelper.SortOrder.DESC));
       return listItems;
   }

    private static void fillListPage(RangeIterator<Resource> resourceRangeIterator, PageManager pageManager, List<Page> listItems) {
        if (resourceRangeIterator != null) {

            while ((pageManager != null) && (resourceRangeIterator.hasNext())) {
                Page containingPage = pageManager.getContainingPage(resourceRangeIterator.next());
                Set<?> pageTags = new HashSet<>(Arrays.asList(containingPage.getProperties().get(Constants.Properties.CQ_TAGS, String[].class)));
               //--------------------------------------------------
                // Get the base page type, for example
                // "edc/components/structure/article/page".
                //--------------------------------------------------
                String containingPageType = null;
                try{
                    //On some test pages, this method throws a null pointer exception error.
                    //Should not happen, but better catch it
                    containingPageType = containingPage.getTemplate().getPageTypePath();
                } catch (Exception e) {
                    log.debug("error on getPageTypePath: " + containingPage.getName());
                }
                boolean wantThisPage = false;
                //--------------------------------------------------
                // Iterate all acceptable page types and set flag to
                // determine if we want this page in our results.
                //--------------------------------------------------
                for (String pageType : Constants.ArrayValues.PAGE_TYPES_TO_INCLUDE_IN_TAG_BASED_PAGE_SEARCH.toArray()) {
                    if (pageType.equals(containingPageType)) {
                        wantThisPage = true;
                        break;
                    }
                }

                if (wantThisPage) {
                    listItems.add(containingPage);
                }
            }
        }
    }

}

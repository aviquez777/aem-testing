package com.edc.edcweb.core.helpers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants.ArticleContentType;

/**
 * @author Peter Crummey
 * @version 0.0.4-SNAPSHOT
 * @since 0.0.4-SNAPSHOT
 * 
 * 
 * Helper class to perform operations on Article page(s).
 * 
 * 
 */
public class ArticlePageHelper
{

	private static final Logger log = LoggerFactory.getLogger(ArticlePageHelper.class);
	private boolean isPremium;
	private boolean useDefautltTag = true;
	private ArticleContentType articleContentType;
	private String title;
	
    /**
     * Get Article Page properties from the given parameters, such as whether this
     * article is premium and its content type details.
     * <p>
     * Once this method has been called, others such as <code>isPremium</code> may
     * be called.
     * 
     * UI #16801 introduced tag priority, the order should be defined on
     * com.edc.edcweb.core.helpers.Constants.ArticleContentType
     *
     * @param request Sling HTTP request
     * @param page    Article page object ({@see com.day.cq.wcm.api.Page} Page)
     * 
     */
    public void getPropertiesFromPage(SlingHttpServletRequest request, Page page) {
        if (page != null) {
            // Initialize variable
            String tagId = null;
            // Create a list array containing all the page tags ids
            List<String> pageTags = Arrays.stream(page.getTags()).map(Tag::getTagID).collect(Collectors.toList());
            // Loop over the ArticleContentType enum ordered by Tag Priority
            for (ArticleContentType contentType : ArticleContentType.values()) {
                // Get the Content Type Tag Id to Compare against the page tags
                tagId = contentType.getTagId();
                if (pageTags.contains(tagId)) {
                    // if Tag is on the list, assign it and stop processing
                    this.useDefautltTag = false;
                    this.articleContentType = contentType;
                    break;
                }
            }
            // Set the default to article
            if (this.useDefautltTag) {
                this.articleContentType = Constants.ArticleContentType.ARTICLE;
                tagId = Constants.ArticleContentType.ARTICLE.getTagId();
            }
            // Check for premium tag
            if (pageTags.contains(Constants.TAGS_EDC_PREMIUM)) {
                this.isPremium = true;
            }
            // Set the title
            this.title = TagHelper.getLocalizedTagTitle(TagHelper.getTagManager(request), page, tagId);
        }
    }
	/**
	 * Determine if this article is a premium article.
	 *
	 * @return  True if the article is premium
	 */	
	public boolean isPremium()
	{
		return this.isPremium;
	}

	/**
	 * Return the content type CSS class name of this article page (e.g., blog, article, etc.).
	 *
	 * @return  Content type CSS class name
	 */	
	public String getContentTypeClassName()
	{
		String classNm="";
		if(this.articleContentType != null)
		{
			classNm = this.articleContentType.getClassName();
		}
		return classNm;
	}

	/**
	 * Return the language-appropriate content type title of this article page (e.g., Blog, Article, etc.).
	 *
	 * @return  Language-appropriate content type title
	 */	
	public String getContentTypeTitle()
	{
		return this.title;
	}
	
	/**
	 * Returns the correct sort date for the given article page. The correct sort date is either first published,
	 * last published or default date depending on the status of this page and the setting in the Article Hero component.
	 * <p>
	 * If the article's sort date type is "first published" or "no date" and there is a "first published date", that date is returned. 
	 * <p>
	 * If the article's sort date type is "last published" and there is a "last published date", that date is returned.
	 * <p>
	 * If neither of these is available, the given default date will be returned.
	 *
	 * @param  page  Article page object ({@see com.day.cq.wcm.api.Page} Page).
	 * @param  valueMap  Properties for that page in a ValueMap. 
	 * @param  defaultDate  Default date to be returned if article page date can not otherwise be determined. 
	 * @return Calendar
	 */	
	public static Calendar getArticlePageSortDate(Page page, ValueMap valueMap, Calendar defaultDate)
	{
		String sortDateType = "default";
		Calendar pageDate  = defaultDate;
		if(valueMap.containsKey(Constants.Properties.PUBLISH_DATE))
		{
			sortDateType = valueMap.get(Constants.Properties.PUBLISH_DATE, String.class);
			if(sortDateType != null)
			{
				//-------------------------------------------------------------
				// If page's sort date type is "first" or "nodate", use the
				// first published date (if it exists)
				//-------------------------------------------------------------
				if((sortDateType.equals(Constants.Properties.FIRST))  ||  (sortDateType.equals(Constants.Properties.NO_DATE)))
				{
					//---------------------------------------------------------
					// If First Published does not have value, keep default
					//---------------------------------------------------------
					if(valueMap.containsKey(Constants.Properties.FIRST_PUBLISHED))
					{
						pageDate = valueMap.get(Constants.Properties.FIRST_PUBLISHED, Calendar.class);
					}
				//-------------------------------------------------------------
				// If the sort date type is "last", get the replication status
				// and retrieve the last published date from it
				//-------------------------------------------------------------
				} else if(sortDateType.equals(Constants.Properties.LAST))
				{
					ReplicationStatus repStatus = ReplicationStatusHelper.getReplicationStatus(page);
					if((repStatus != null)  &&  (repStatus.getLastPublished() != null))
					{
						pageDate = repStatus.getLastPublished();
					} 
					// If no agent use cq:lastReplicated date
					if(repStatus == null && valueMap.containsKey(Constants.Properties.LAST_REPLICATED)) {
					    pageDate = valueMap.get(Constants.Properties.LAST_REPLICATED, Calendar.class);
					}
				}
			}
		}
		if(log.isDebugEnabled())
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			log.debug("Page Title: " + page.getPageTitle());
			log.debug("\tdefaultDate: " + (defaultDate != null ? formatter.format(defaultDate.getTime()) : null) +
					  "  publishDate: " + sortDateType + 
					  "  pageDate: " + (pageDate != null ? formatter.format(pageDate.getTime()) : null));
		}
		return pageDate;
	}

	public enum SortOrder {
		ASC("asc"),
		DESC("desc");
		
		private String value;
		
		SortOrder(String value) {
			this.value = value;
		}
		
		public static SortOrder fromString(String value) {
			for (SortOrder s : values()) {
				if (StringUtils.equals(value, s.value)) {
					return s;
				}
			}
			return ASC;
		}
	}

	/**
	 * Implements a {@see java.util.Comparator} Comparator that will determine how two articles should be sorted.  
	 */	
	public static class ListSort implements Comparator<Page>, Serializable
	{
	
		private static final long serialVersionUID = 204096578105548876L;
		private SortOrder sortOrder;
		
		public ListSort(SortOrder sortOrder)
		{
			this.sortOrder = sortOrder;
		}

		/**
		 * Compare two article pages and determine which of the two is more recent. 
		 *
		 * @param  page1  Article page object ({@see com.day.cq.wcm.api.Page} Page).
		 * @param  page2  Article page object ({@see com.day.cq.wcm.api.Page} Page).
		 * @return A negative integer, zero, or a positive integer as the first page (page1) is less than, equal to, or greater than the second (page2), respectively.
		 */	
		@Override
		public int compare(Page page1, Page page2)
		{
			int i = 0;
			//------------------------------------------------------------------
			ValueMap page1ValueMap = page1.getProperties();
			ValueMap page2ValueMap = page2.getProperties();
			//------------------------------------------------------------------
			Calendar page1Date = ArticlePageHelper.getArticlePageSortDate(page1, page1ValueMap, page1ValueMap.get(Constants.Properties.JCR_CREATED, Calendar.class));
			Calendar page2Date = ArticlePageHelper.getArticlePageSortDate(page2, page2ValueMap, page2ValueMap.get(Constants.Properties.JCR_CREATED, Calendar.class));
			//------------------------------------------------------------------
			if ( page1Date != null && page2Date != null )
			{
				i = page1Date.compareTo(page2Date);
				
				if (sortOrder == SortOrder.DESC) {
					i = i * -1;
				}
				
			}
			
			return i;
		}
	
	}

}
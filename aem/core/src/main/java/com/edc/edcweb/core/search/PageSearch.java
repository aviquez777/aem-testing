package com.edc.edcweb.core.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.search.filters.SearchFilter;

public class PageSearch
{
	//-------------------------------------------------------------------------
	// Maximum levels to search for pages. Can be -1 for unlimited. 
	//-------------------------------------------------------------------------
	private long maxSearchLevel;
	private SearchFilter filter;
	
	public PageSearch(long maximumSearchLevel, SearchFilter searchFilter)
	{
		//---------------------------------------------------------------------
		// Maximum search level cannot be less than -1 or equal to zero...
		//---------------------------------------------------------------------
		if((maximumSearchLevel < -1)  ||  (maximumSearchLevel == 0))
		{
			throw new IllegalArgumentException("maximumSearchLevel must be -1 or a positive (non-zero) number - received: " + maximumSearchLevel);
		}
		this.maxSearchLevel = maximumSearchLevel;
		//---------------------------------------------------------------------
		// Search filter cannot be null
		//---------------------------------------------------------------------
		if(searchFilter == null)
		{
			throw new IllegalArgumentException("searchFilter must NOT be null");
		}
		this.filter = searchFilter;
	}
	
	public List<PageList> searchPages(Page startingPage)
	{
		List<PageList> pageList = new ArrayList<>();
		populateLinksModel(startingPage, pageList, 1);
		return pageList;
	}

	private void populateLinksModel(Page resource, List<PageList> currentPage, long currentLevel)
	{
		if((resource != null)  &&  ((this.maxSearchLevel == -1)  ||  (currentLevel <= this.maxSearchLevel)))
		{
			Iterator<Page> resources = resource.listChildren();
			while (resources.hasNext()) {
				Page childPage = resources.next();
				PageList withChildren = new PageList(childPage);
				//-------------------------------------------------------------
				// If the page is filtered in, add it to the collection and
				// continue searching with its children. If a page is filtered
				// out, its children are ignored.
				//-------------------------------------------------------------
				if(this.filter.isFiltered(childPage))
				{
					currentPage.add(withChildren);
					populateLinksModel(childPage, withChildren.getChildren(), (currentLevel + 1));
				}
				//-------------------------------------------------------------
			}
		}
	}
}

package com.edc.edcweb.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.ArticlePageHelper.SortOrder;

public class PageList
{
	private Page page;
	private List<PageList> children = new ArrayList<>();

	public PageList(Page page)
	{
		this.page = page;
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<PageList> getChildren() {
		return  children;
	
	}
	
	public void addChild(PageList child)
	{
		children.add(child);
	}

	/**
	 * Implements a {@see java.util.Comparator} Comparator that will determine how two PageList's should be sorted.  
	 */	
	public static class ListSort implements Comparator<PageList>, Serializable
	{
		private static final long serialVersionUID = 368197541656656151L;
		private ArticlePageHelper.ListSort listSort;
		
		public ListSort(SortOrder sortOrder)
		{
			this.listSort = new ArticlePageHelper.ListSort(sortOrder);
		}

		/**
		 * Compare two PageList's and determine which of the two is more recent. 
		 *
		 * @param  pageList1  PageList object.
		 * @param  pageList2  PageList object.
		 * @return A negative integer, zero, or a positive integer as the first page (page1) is less than, equal to, or greater than the second (page2), respectively.
		 */	
		@Override
		public int compare(PageList pageList1, PageList pageList2)
		{
			//------------------------------------------------------------------
			return this.listSort.compare(pageList1.getPage(), pageList2.getPage());
		}

	}
}

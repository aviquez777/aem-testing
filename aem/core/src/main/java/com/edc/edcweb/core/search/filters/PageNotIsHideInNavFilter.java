package com.edc.edcweb.core.search.filters;

import com.day.cq.wcm.api.Page;

public class PageNotIsHideInNavFilter implements SearchFilter
{
	@Override
	public boolean isFiltered(Page page)
	{
		return((page != null)  &&  (!page.isHideInNav()));
	}

	@Override
	public boolean isReadyToSearch()
	{
		return true;
	}
}

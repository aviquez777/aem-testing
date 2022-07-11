package com.edc.edcweb.core.search.filters;

import com.day.cq.wcm.api.Page;

public class AllPagesFilter implements SearchFilter
{
	@Override
	public boolean isFiltered(Page page)
	{
		return true;
	}

	@Override
	public boolean isReadyToSearch()
	{
		return true;
	}
}

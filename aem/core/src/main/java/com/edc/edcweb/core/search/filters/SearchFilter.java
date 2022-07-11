package com.edc.edcweb.core.search.filters;

import com.day.cq.wcm.api.Page;

public interface SearchFilter
{
	public boolean isFiltered(Page page);
	
	public boolean isReadyToSearch();
}

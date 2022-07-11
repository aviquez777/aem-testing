package com.edc.edcweb.core.search.filters;

import com.day.cq.wcm.api.Page;
import com.day.cq.tagging.Tag;

public class PagesWithTagIdFilter implements SearchFilter
{
	private String[] tagIds;
	
	@Override
	public boolean isFiltered(Page page)
	{
		Tag [] tags = page.getTags();
		boolean tagFound = false;
		//---------------------------------------------------------------------
		if((tags != null)  &&  (this.tagIds != null)  &&  (this.tagIds.length != 0))
		{
			for(Tag tag : tags)
			{
				for(String tagId : this.tagIds)
				{
					if(tag.getTagID().equals(tagId))
					{
						tagFound = true;
						break;
					}
				}
			}
		}
		return tagFound;
	}
	
	public void setTagId(String[] tagIds)
	{
		this.tagIds = tagIds.clone();
	}

	@Override
	public boolean isReadyToSearch()
	{
		return this.tagIds != null;
	}
}

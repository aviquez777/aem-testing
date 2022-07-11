package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;


@Model(adaptables = Resource.class)
public class FilterTag
{

	public FilterTag(String id, String title) {
		this.id = id;
		this.title = title;
	}
	
	@Inject
	@Optional
	private String id;

	@Inject
	@Optional
	private String title;

	// accessor methods
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
}

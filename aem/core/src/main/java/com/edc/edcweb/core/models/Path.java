package com.edc.edcweb.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class Path {
	@Inject
	@Optional
	private String path;

	// access methods
	
	public String getPath() {
		return path;
	}
}

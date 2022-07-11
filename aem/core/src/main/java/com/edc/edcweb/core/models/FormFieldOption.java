package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class FormFieldOption {

	@Inject
	@Named("enName")
	@Optional
	private String enName;
	
	@Inject
	@Named("frName")
	@Optional
	private String frName;
	
	@Inject
	@Named("value")
	@Optional
	private String value;
	
	public String getEnName() {
		return enName;
	}
	
	public String getFrName() {
		return frName;
	}

	public String getValue() {
		return value;
	}
	
	
}

package com.edc.edcweb.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class FormFieldOptionGRC {

	@Inject
	@Named("enName")
	@Optional
	private String enName;

	@Inject
	@Named("frName")
	@Optional
	private String frName;

	@Inject
	@Named("tagValue")
	@Optional
	private String tagValue;

	public String getEnName() {
		return enName;
	}

	public String getFrName() {
		return frName;
	}

	public String getTagValue() {
		return tagValue;
	}


}

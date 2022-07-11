package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**

 * Defines the {@code OnPageNavigationLinks} Sling Model used for the {@code /apps/edc/content/onpagenavigation} component.

 */

@Model(adaptables = Resource.class)

public class OnPageNavigationLinks {
	@Inject
	@Named("text")
	@Optional
	private String text;

	@Inject
	@Named("target")
	@Optional
	private String target;

	public String getText() {
		return text;
	}

	public String getTarget() {
		return target;
	}
}

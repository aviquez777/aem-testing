package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;

import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Model to retrieve information for a CTA link, properties being ctatext, ctaurl and ctatarget (e.g., existing window, new window, etc.).
 * 
 * 
 */
@Model(adaptables = Resource.class)
public class CTALink
{
	
	@Inject
	@Named("ctatext")
	@Optional
	private String ctatext;

	@Inject
	@Named("ctaurl")
	@Optional
	private String ctaurl;

	@Inject
	@Named("ctatarget")
	@Optional
	private String ctatarget;

	
	public String getCtatext() {
		return ctatext;
	}

	public String getCtaurl() {
		return LinkResolver.addHtmlExtension(ctaurl);
	}

	public String getCtatarget() {
		return ctatarget;
	}
}

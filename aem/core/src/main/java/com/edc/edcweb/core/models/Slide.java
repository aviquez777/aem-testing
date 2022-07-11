package com.edc.edcweb.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;


@Model(adaptables = Resource.class)
public class Slide
{

	@Inject
	@Named("fileReference")
	@Optional
	private String fileReference;

	@Inject
	@Named("tabletFileReference")
	@Optional
	private String tabletFileReference;

	@Inject
	@Named("teaserimage")
	@Optional
	private String teaserimage;

	@Inject
	@Named("imageAlt")
	@Optional
	private String imageAlt;

	@Inject
	@Named("quote")
	@Optional
	private String quote;

	@Inject
	@Named("quoteby")
	@Optional
	private String quoteBy;

	@Inject
	@Named("companyrole")
	@Optional
	private String companyRole;

	@Inject
	@Named("linktext")
	@Optional
	private String linkText;	
	
	@Inject
	@Named("linkurl")
	@Optional
	private String linkUrl;	
	
	@Inject
	@Named("linktarget")
	@Optional
	private String linkTarget;
	
	@Inject
	@Named("firstattributiontext")
	@Optional
	private String firstAttributionText;
	
	@Inject
	@Named("firstattributiontitle")
	@Optional
	private String firstAttributionTitle;

	@Inject
	@Named("secondattributiontext")
	@Optional
	private String secondAttributionText;
	
	@Inject
	@Named("secondattributiontitle")
	@Optional
	private String secondAttributionTitle;	
	
	@Inject
	@Named("thirdattributiontext")
	@Optional
	private String thirdAttributionText;
	
	@Inject
	@Named("thirdattributiontitle")
	@Optional
	private String thirdAttributionTitle;
	
	@Inject
	@Self
	private TextUrlLinks cta;
	
	// getters
		
	public String getQuote() {
		return quote;
	}

	public String getQuoteBy() {
		return quoteBy;
	}

	public String getCompanyRole() {
		return companyRole;
	}

	public String getLinkText() {
		return linkText;
	}

	public String getLinkUrl() {
		return LinkResolver.addHtmlExtension(linkUrl);
	}

	public String getLinkTarget() {
		return linkTarget;
	}

	public String getFirstAttributionTitle() {
		return firstAttributionTitle;
	}

	public String getFirstAttributionText() {
		return firstAttributionText;
	}

	public String getSecondAttributionTitle() {
		return secondAttributionTitle;
	}

	public String getSecondAttributionText() {
		return secondAttributionText;
	}

	public String getThirdAttributionTitle() {
		return thirdAttributionTitle;
	}

	public String getThirdAttributionText() {
		return thirdAttributionText;
	}	
	
	public TextUrlLinks getCta() {
		return cta;
	}

	public String getFileReference() {
		return fileReference;
	}

	public String getTabletFileReference() {
		return tabletFileReference;
	}

	public String getTeaserimage() {
		return teaserimage;
	}

	public String getImageAlt() {
		return imageAlt;
	}
}

package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Model(adaptables = Resource.class)
public class BreakpointImage
{
	private static final Logger log = LoggerFactory.getLogger(BreakpointImage.class);
    	
	@Inject
	@Named("phoneFileReference")
	private String phoneFileReference;	

	@Inject
	@Named("tabletFileReference")
	@Optional
	private String tabletFileReference;

	@Inject
	@Named("desktopFileReference")
	@Optional
	private String desktopFileReference;		

	@Inject
	@Named("alt")
	@Optional
	private String alt;	
	
	@Inject
	@Named("lazyload")
	@Optional
	private String lazyload;	

	public String getPhoneFileReference() {
		return phoneFileReference;
	}


	public String getTabletFileReference() {
		return tabletFileReference;
	}


	public String getDesktopFileReference() {
		return desktopFileReference;
	}


	public String getAlt() {
		return alt;
	}


	public String getLazyload() {
		return lazyload;
	}	
	
	// getters
	
}

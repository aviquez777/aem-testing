package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.edc.edcweb.core.helpers.Constants;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code data for  ContactUs Tabs Region Data.
 * 
 */

@Model(adaptables = Resource.class)
public class ContactUsTabsRegion
{
	private static final Logger log=LoggerFactory.getLogger(ContactUsTabsRegion.class);
	
	@Self
	private Resource resource;
	
	@Inject
	@Named("regions")
	@Optional
	private String region;		
	
	@Inject
	@Named("tabTarget")
	@Optional
	private String tabTarget;		

	private List<ContactUsTabsOffice> offices;
	
	@PostConstruct
	public void initModel() {	
		 
		offices= new ArrayList<>();
		Resource resourceTags = resource.getChild(Constants.Properties.CITIES);
		if ( resourceTags == null ){
			log.debug("ContactUsTabsRegion initModel, resourceTags == null"); 
			return;
		}

		try{	 
			populateOffices( resourceTags );

		} catch (Exception e) {
			log.error("error ",e);
		}
	}	
	
	  
	private void populateOffices(Resource resourceTags) {
	 
		Iterator<Resource> linkOffice = resourceTags.listChildren();

		while (linkOffice.hasNext())
		{
			Resource linkRsc = linkOffice.next();
			ContactUsTabsOffice  officeData = linkRsc.adaptTo(ContactUsTabsOffice.class);
			offices.add(officeData);
		}
				
	}

	// getters	
	public String getRegion() {
		return region;
	}
	 
	public List<ContactUsTabsOffice> getOffices(){
		return this.offices;
	}
	
	public String getTabTarget(){
		return this.tabTarget;
	}
}

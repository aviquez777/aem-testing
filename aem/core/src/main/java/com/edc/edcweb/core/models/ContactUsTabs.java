package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;


/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code for ContactUs Tabs
 * 
 */

@Model(adaptables = SlingHttpServletRequest.class) 
public class ContactUsTabs
{
	private static final Logger log = LoggerFactory.getLogger(ContactUsTabs.class);

	@ScriptVariable
	private ValueMap properties;

	@Inject @Default(values="") 
	private Resource resource; 

	@Self
	private SlingHttpServletRequest request;

	@Inject
	@Optional
	private Page currentPage;

	private List<ContactUsTabsRegion> regionsCanada;
	private List<ContactUsTabsRegion> regionsAbroad;
	private List<ContactUsTabsRegion> regionsInternational;

	/**
	 * Populates the model.  
	 *
	 */		 
	@PostConstruct
	public void initModel() {	
		log.debug(" initModel  " );
		 
		regionsCanada = new ArrayList<>();
		regionsAbroad = new ArrayList<>();
		regionsInternational = new ArrayList<>();
		
		Resource resourceTags = resource.getChild(Constants.Properties.REGIONALOFFICES);
		if ( resourceTags == null ){
			log.debug("no resource found [" + Constants.Properties.REGIONALOFFICES +  "]" );
			return;
		}
		try	{	 
			populateRegionOffices(resourceTags);
		} catch (Exception e){
			log.error("error ",e );
		}

	}	

	private void populateRegionOffices(Resource resourceTags ) {
		
		Iterator<Resource> linkRegion = resourceTags.listChildren();

		while (linkRegion.hasNext()){
			Resource linkRsc = linkRegion.next();
			ContactUsTabsRegion  regiondata = linkRsc.adaptTo(ContactUsTabsRegion.class);
			 
			 if ( regiondata != null ){
				 if( regiondata.getTabTarget().equalsIgnoreCase(Constants.Properties.CANADA)){
					 this.regionsCanada.add(regiondata);
				 }
				 else if( regiondata.getTabTarget().equalsIgnoreCase(Constants.Properties.ABROAD)){
					 this.regionsAbroad.add(regiondata);
				 }
				 else if( regiondata.getTabTarget().equalsIgnoreCase(Constants.Properties.INTERNATIONAL)){
					 this.regionsInternational.add(regiondata);
				 }
			 }
		}
		
		return;
	}	 

	public List<ContactUsTabsRegion> getCanadaOffices(){
		return this.regionsCanada;
	}
	
	public List<ContactUsTabsRegion> getInternationalOffices(){
		return this.regionsInternational;
	}
	
	public List<ContactUsTabsRegion> getAbroadOffices(){
		return this.regionsAbroad;
	}

}

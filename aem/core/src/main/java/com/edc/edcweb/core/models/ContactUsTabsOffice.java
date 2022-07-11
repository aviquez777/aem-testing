package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
 
/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code data for  ContactUS Tabs Offices Data.
 * 
 */

@Model(adaptables = Resource.class)
public class ContactUsTabsOffice
{	 
	@Inject
	@Named("city")
	@Optional
	private String city;		

	@Inject
	@Named("address")
	@Optional
	private String address;
	 
	@PostConstruct
	public void initModel(){	
	}	
	
	public String  getCity(){
		return this.city;
	}
	 
	public String  getAddress(){
		return this.address;
	}
	 
}

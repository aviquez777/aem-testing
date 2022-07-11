package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code data for countrysearch and countryfeaturedlist.
 * 
 */

@Model(adaptables = Resource.class)
public class CountrySearchData
{
	 
	@Inject
	@Named("countryname")
	@Optional
	private String countryname;		

	@Inject
	@Named("positionLabel")
	@Optional
	private String positionLabel;	
	
	// @Inject
	// @Named("ratingLabel")
	// @Optional
	// private String ratingLabel;	
	
	// @Inject
	// @Named("rating")
	// @Optional
	// private String rating;
	
	
	// biographical text
	@Inject
	@Named("position")
	@Optional
	private String position;		

	@Inject
	@Named("region")
	@Optional
	private String region;	
	
	@Inject
	@Named("regionLabel")
	@Optional
	private String regionLabel;	
	
	
	@Inject
	@Named("countrypageurl")
	@Optional
	private String countrypageurl;	
	
	@Inject
	@Named("countryID")
	@Optional
	private String countryID;
	
	@Inject
	@Named("countryTag")
	@Optional
	private String countryTag;
	
	@Inject
	@Named("isPremium")
	@Optional
	private Boolean isPremium;
	
	@Inject
	@Named("fta")
	@Optional
	private String fta;
	
	 
	private Boolean isLetter = false;
	private String letterName;
	@PostConstruct
	public void initModel() {	
		this.countryID="";

	}	
	// public void initValues(String icountryName, String iregion, String iregionLabel, String icountryPageUrl, String iposition,String ipositionLabel, String irating, String iratingLabel, String iCountryTag,String iCountryID, String iFTA, Boolean iIsPremium ) {	
	public void initValues(String icountryName, String iregion, String iregionLabel, String icountryPageUrl, String iposition,String ipositionLabel, String iCountryTag,String iCountryID, String iFTA, Boolean iIsPremium ) {	
		 this.region = iregion;
		 this.regionLabel = iregionLabel;
		 this.countryname = icountryName;
		 this.countrypageurl = icountryPageUrl;
		 this.position = iposition;
		 this.positionLabel = ipositionLabel;
		//  this.rating = irating;
		//  this.ratingLabel = iratingLabel;
		 this.countryTag = iCountryTag;
		 this.countryID = iCountryID;
		 this.fta = iFTA;
		 this.isPremium = iIsPremium;
	}	
	
	public void initAsLetter(String iLetter ) {	
		this.letterName = iLetter;
		this.countryname = iLetter;
		this.isLetter = true;
		this.isPremium = false;
	}	
	
	// getters	
	public String getRegion() {
		return region;
	}
	
	public String getRegionLabel() {
		return regionLabel;
	}
	
	public String getPosition() {
		return position;
	}	
	
	public String getPositionLabel() {
		return positionLabel;
	}
	
	// public String getRatingLabel() {
	// 	return ratingLabel;
	// }
	
	// public String getRating() {
	// 	return rating;
	// }
	
	public String getCountryTag() {
		return countryTag;
	}
	
	public String getCountryID() {
		return countryID;
	}
	
	public String getCountryName() {
		return countryname;
	}
	
	public String getLetterName() {
		return letterName;
	}
	
	public Boolean getIsLetter() {
		return isLetter;
	}
	
	public String getPremium() {
		return this.isPremium.toString();
	}
	
	public String getCountryPageUrl() {
		return LinkResolver.addHtmlExtension(countrypageurl);
	}	
		
	public String getFTA() {
		return this.fta;
	}

}

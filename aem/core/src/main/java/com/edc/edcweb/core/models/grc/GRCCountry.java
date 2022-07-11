package com.edc.edcweb.core.models.grc;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code data for GRC Search result.
 * 
 */
public class GRCCountry
{
	private String countryCode; 
	private String countryName; 
	private String countryNameEn; 
	private String url;
	
	public String getCountryCode() {
		return countryCode.toUpperCase();
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getCountryNameEn() {
		return countryNameEn;
	}
	public void setCountryNameEn(String countryNameEn) {
		this.countryNameEn = countryNameEn;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	} 

	
	
}

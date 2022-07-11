package com.edc.edcweb.core.helpers;

import java.util.HashMap;

public class HTTPRequestParams {

	private String url;
	private String requestBody;
	private String requestType;
	
	HashMap<String, String> requestProperties = new HashMap<>();
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getRequestBody() {
		return requestBody;
	}
	
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	
	public HashMap<String, String> getRequestProperties() {
		return requestProperties;
	}
	
	public void setRequestProperties(HashMap<String, String> requestProperties) {
		this.requestProperties = requestProperties;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	
}

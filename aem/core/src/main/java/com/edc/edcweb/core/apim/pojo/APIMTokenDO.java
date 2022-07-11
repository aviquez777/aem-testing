package com.edc.edcweb.core.apim.pojo;

public class APIMTokenDO {

	private String accessToken;
	private String tokenType;

	private int expiresIn;
	private int extExpiresIn;
	private int expiresOn;
	private int notBefore;
	private String resource;
	
	
	
	public APIMTokenDO() {
		super();
	}

	public APIMTokenDO(String accessToken, String tokenType, int expiresIn, int extExpiresIn, int expiresOn, int notBefore, String resource) {
		super();
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.extExpiresIn = extExpiresIn;
		this.expiresOn = expiresOn;
		this.notBefore = notBefore;
		this.resource = resource;
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public int getExtExpiresIn() {
		return extExpiresIn;
	}

	public void setExtExpiresIn(int extExpiresIn) {
		this.extExpiresIn = extExpiresIn;
	}

	public int getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(int expiresOn) {
		this.expiresOn = expiresOn;
	}

	public int getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(int notBefore) {
		this.notBefore = notBefore;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
}

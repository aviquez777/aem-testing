package com.edc.edcportal.core.services;

/**
 * System configuration service
 *
 */
public interface MyEDCConfigSystemService {
    /**
     * Get the AIM login url
     * 
     * @return AIM login url
     */
    String getLoginUrl();

    /**
     * Get DTM url.
     * 
     * @return DTM url.
     */
    String getDtmLibURL();

    /**
     * Method to get One Trurst's Full Javascript's EN EU URI
     *
     * @return One Trurst's Full Javascript's EN EU URI
     */
    String getOneTrustScriptUrlEuEn();

    /**
     * Method to get One Trurst's Full Javascript's FR EU URI
     *
     * @return One Trurst's Full Javascript's FR EU URI
     */
    String getOneTrustScriptUrlEuFr();

    /**
     * Method to get One Trurst's Full Javascript's EN Non EU URI
     *
     * @return One Trurst's Full Javascript's EN Non EU URI
     */
    String getOneTrustScriptUrlNonEuEn();

    /**
     * Method to get One Trurst's Full Javascript's FR Non EU U URI
     *
     * @return One Trurst's Full Javascript's FR Non EU URI
     */
    String getOneTrustScriptUrlNonEuFr();

    /**
     * Method to get One Trurst's Script source
     *
     * @return One Trurst's Full Script source
     */
    String getOneTrustScriptSrc();

    /**
     * Method to get One Trurst's Script source
     *
     * @return One Trurst's Full Script source
     */
    String  getOneTrustScriptIntegrityHash();

    /**
     * Method to get One Trurst's Domain script
     *
     * @return One Trurst's Full Javascript's domain script
     */
    String getOneTrustDomainSrc();

    /**
     * Method to get Webinar's window time to set it as live before it actually
     * starts Task 93133
     * 
     * @return Window time in minutes
     */
    int getWebinarsWindowTime();

    /**
     * Method to get Canadian Post AddressComplete Service to use
     * 
     * @return Key
     */
    String getAddressCompleteService();

    /**
     * Method to get Canadian Post AddressComplete CSS URL
     * 
     * @return CSS URL
     */
    String getAddressCompleteCSSUrl();

    /**
     * Method to get Canadian Post AddressComplete JavaScript URL
     * 
     * @return JavaScript URL
     */
    String getAddressCompleteJSUrl();

    /**
     * Method to get Canadian Post AddressComplete Key
     * 
     * @return Key
     */
    String getAddressCompleteKey();

}

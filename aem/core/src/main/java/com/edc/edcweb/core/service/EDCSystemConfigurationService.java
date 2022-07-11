package com.edc.edcweb.core.service;

/**
 * Interface EDCSystemConfigurationService.
 *
 * @author gavik.wadhwa
 *
 */
public interface EDCSystemConfigurationService {

    /**
     * Method to get site domain.
     *
     * @return site domain name.
     */
    String getSiteDomainName();

    /**
     * Method to get live chat domain.
     *
     * @return live chat domain name.
     */
    String getLiveChatDomainName();

    /**
     * Method to get DTM url.
     *
     * @return DTM url.
     */
    String getDtmLibURL();

    /**
     * Method to get Google Site Verification
     *
     * @return Google Site Verification code
     */
    String getGoogleSiteVerification();

    /**
     * Method to get Ms Validate
     *
     * @return Ms Validate code
     */
    String getMsValidate();

    /**
     * Method to check If tracking scripts should load on author side
     *
     * @return true If tracking scripts should load on author side
     */
    String getEnableAuthor();

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
     * Method to get One Trust's Script SRI Hash
     *
     * @return One Trurst's Script SRI Hash
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
     * Method to get the List of the values for the WIT Webinar's Radio buttons Task
     * 99285
     *
     * @return String with the list of values
     */
    String[] getWitWebinarLOV();

    /**
     * Method to get the Field List for the AEM: BCAP Form - Inclusion and Diversity Task
     * 99285
     *
     * @return String with the Field List
     */
    String[] getBcapIncDivFieldNames();

    /**
     * Method to get the List of the values for the AEM: BCAP Form - Inclusion and Diversity Task
     * 99285
     *
     * @return String with the list of values
     */
    String[] getBcapIncDivFieldLOV();

    /**
     * Method to get the List of the logout urls first English then French
     * 99285
     *
     * @return String with the urls
     */
    String[] getLogoutUrls();

    /**
     * Method to get the email from Address
     * @return email from address
     */
    String getEmailFrom();
    /**
     * Method to get AddressComplete Service to use
     * 
     * @return Key
     */
    String getAddressCompleteService();

    /**
     * Method to get AddressComplete CSS URL
     * 
     * @return CSS URL
     */
    String getAddressCompleteCSSUrl();

    /**
     * Method to get AddressComplete JavaScript URL
     * 
     * @return JavaScript URL
     */
    String getAddressCompleteJSUrl();

    /**
     * Method to get AddressComplete Key
     * 
     * @return Key
     */
    String getAddressCompleteKey();
}
package com.edc.edcweb.core.serviceImpl.pojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class EloquaContact {

    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }


    public String getAttr( String attrName ) {
        if ( properties.containsKey(attrName))
        {
            return  properties.get(attrName);
        }
        else {
            return "";
        }
    }

    public void  setAttr(String attrName, String value ) {
        if(value!=null) {
            properties.put(attrName, value);
        }
    }

    public String getCompanyName() {
        return getAttr("companyName");
    }

    public void setCompanyName(String companyName) {
        setAttr("companyName",companyName);
    }

    public String getEmailAddress() {
        return getAttr("emailAddress");
    }

    public void setEmailAddress(String emailAddress) {
        setAttr("emailAddress",emailAddress);
    }

    public String getFirstName() {
        return getAttr("firstName");
    }

    public void setFirstName(String firstName) {
        setAttr("firstName",firstName);
    }

    public String getLanguage() {
        return getAttr("language");
    }

    public void setLanguage(String language) {
        setAttr("language",language);
    }

    public String getLastName() {
        return getAttr("lastName");
    }

    public void setLastName(String lastName) {
        setAttr("lastName",lastName);
    }

    public String getTitle() {
        return getAttr("title");
    }

    public void setTitle(String title) {
        setAttr("title",title);
    }

    public String getTradeStatus() {
        return getAttr("tradeStatus");
    }

    public void setTradeStatus(String tradeStatus) {
        setAttr("tradeStatus",tradeStatus);
    }

    public String getCompanyProvince() {
        return getAttr("companyProvince");
    }

    public void setCompanyProvince(String province) {
        setAttr("companyProvince",province);
    }

    public String getPainPoint() {
        return getAttr("painPoint");
    }

    public void setPainPoint(String painPoint) {
        setAttr("painPoint",painPoint);
    }

    public String getIndustry() {
        return getAttr("industry");
    }

    public void setIndustry(String industry) {
        setAttr("industry",industry);
    }

    public String getCompanyCountry() {
        return getAttr("companyCountry");
    }

    public void setCompanyCountry(String companyCountry) {
        setAttr("companyCountry",companyCountry);
    }

    public String getAnnualSales() {
        return getAttr("annualSales");
    }

    public void setAnnualSales(String annualSales) {
        setAttr("annualSales",annualSales);
    }

    public String getGUID() {
        return getAttr("GUID");
    }

    public void setGUID(String GUID) {
        setAttr("GUID",GUID);
    }

    public String getCompanyAddress1() {
        return getAttr("companyAddress1");
    }

    public void setCompanyAddress1(String companyAddress1) {
        setAttr("companyAddress1",companyAddress1);
    }

    public String getCompanyAddress2() {
        return getAttr("companyAddress2");
    }

    public void setCompanyAddress2(String companyAddress2) {
        setAttr("companyAddress2",companyAddress2);
    }

    public String getCompanyCity() {
        return getAttr("companyCity");
    }

    public void setCompanyCity(String companyCity) {
        setAttr("companyCity",companyCity);
    }

    public String getMainPhone() {
        return getAttr("mainPhone");
    }

    public void setMainPhone(String mainPhone) {
        setAttr("mainPhone",mainPhone);
    }

    public String getEmployees() {
        return getAttr("employees");
    }

    public void setEmployees(String employees) {
        setAttr("employees",employees);
    }

    // FORM 3330 uses employees1 as field name
    public String getEmployees1() {
        return getAttr("employees1");
    }

    public void setEmployees1(String employees1) {
        setAttr("employees1",employees1);
    }

    public String getCompanyPostal() {
        return getAttr("companyPostal");
    }

    public void setCompanyPostal(String companyPostal) {
        setAttr("companyPostal",companyPostal);
    }

    public String getMarketsInt() {
        return getAttr("marketsInt");
    }

    public void setMarketsInt(String marketsInt) {
        setAttr("marketsInt",marketsInt);
    }

    // FORM 3330 uses marketsInt-large as field name
    // Must remove the "-" so we used it as valid method name
    public String getMarketsIntlarge() {
        return getAttr("marketsIntlarge");
    }

    public void setMarketsIntlarge(String marketsIntlarge) {
        setAttr("marketsIntlarge", marketsIntlarge);
    }

    public String getKnowledgeCustomerStage() {
        return getAttr("knowledgeCustomerStage");
    }

    public void setKnowledgeCustomerStage(String knowledgeCustomerStage) {
        setAttr("knowledgeCustomerStage",knowledgeCustomerStage);
    }

    public String getCaslConsent() {
        return getAttr("caslConsent");
    }

    public void setCaslConsent(String caslConsent) {
        setAttr("caslConsent",caslConsent);
    }

    //Hidden fields
    public String getFullFromPage () {
        return getAttr("fullFromPage ");
    }

    public void setFullFromPage(String fullFromPage) {
        setAttr("fullFromPage",fullFromPage);
    }

    public String getFromPage () {
        return getAttr("fromPage");
    }

    public void setFromPage(String fromPage) {
        setAttr("fromPage",fromPage);
    }

    public String getGatedURL() {
        return getAttr("gatedURL");
    }

    public void setGatedURL(String gatedURL) {
        setAttr("gatedURL",gatedURL);
    }

    public String getLang() {
        return getAttr("lang");
    }

    public void setLang(String lang) {
        setAttr("lang",lang);
    }

    public String getDocID() {
        return getAttr("docID");
    }

    public void setDocID(String docID) {
        setAttr("docID",docID);
    }

    public String getExportJourney() {
        return getAttr("exportJourney");
    }

    public void setExportJourney(String exportJourney) {
        setAttr("exportJourney",exportJourney);
    }

    public String getExportPain() {
        return getAttr("exportPain");
    }

    public void setExportPain(String exportPain) {
        setAttr("exportPain",exportPain);
    }

    public String getBuyerJourney() {
        return getAttr("buyerJourney");
    }

    public void setBuyerJourney(String buyerJourney) {
        setAttr("buyerJourney",buyerJourney);
    }

    public String getAssetTier() {
        return getAttr("assetTier");
    }

    public void setAssetTier(String assetTier) {
        setAttr("assetTier",assetTier);
    }

    public String getOwnerID() {
        return getAttr("ownerID");
    }

    public void setOwnerID(String ownerID) {
        setAttr("ownerID",ownerID);
    }

    public String getPersonaID() {
        return getAttr("personaID");
    }

    public void setPersonaID(String personaID) {
        setAttr("personaID",personaID);
    }

    public String getInquiryID() {
        return getAttr("inquiryID");
    }
    public void setInquiryID(String inquiryID) {
        setAttr("inquiryID",inquiryID);
    }

    public String getParagraphTextTAS() {
        return getAttr("paragraphTextTAS");
    }

    public void setParagraphTextTAS(String paragraphTextTAS) {
        setAttr("paragraphTextTAS",paragraphTextTAS);
    }

    public String getPAUFlag() {
        return getAttr("PAUFlag");
    }

    public void setPAUFlag(String PAUFlag) {
        setAttr("PAUFlag",PAUFlag);
    }

    public String getPAUDate() {
        return getAttr("PAUDate");
    }

    public void setPAUDate(String PAUDate) {
        setAttr("PAUDate",PAUDate);
    }

    public String getCoreCustomer() {
        return getAttr("coreCustomer");
    }

    public void setCoreCustomer(String coreCustomer) {
        setAttr("coreCustomer", coreCustomer);
    }
    
    public String getFiLoan() {
        return getAttr("fiLoan");
    }

    public void setFiLoan(String fiLoan) {
        setAttr("fiLoan", fiLoan);
    }

    public String getCovidWC() {
        return getAttr("covidWC");
    }

    public void setCovidWC(String covidWC) {
        setAttr("covidWC", covidWC);
    }

    public String getSalesTiming() {
        return getAttr("salesTiming");
    }

    public void setSalesTiming(String salesTiming) {
        setAttr("salesTiming", salesTiming);
    }

    public String getNonCDNCurrencyExperience() {
        return getAttr("nonCDNCurrencyExperience");
    }

    public void setNonCDNCurrencyExperience(String nonCDNCurrencyExperience) {
        setAttr("nonCDNCurrencyExperience", nonCDNCurrencyExperience);
    }

    public String getFxContractExperience() {
        return getAttr("fxContractExperience");
    }

    public void setFxContractExperience(String fxContractExperience) {
        setAttr("fxContractExperience", fxContractExperience);
    }

    public String getCoMainPhoneExt() {
        return getAttr("coMainPhoneExt");
    }

    public void setCoMainPhoneExt(String coMainPhoneExt) {
        setAttr("coMainPhoneExt", coMainPhoneExt);
    }

    public String getFiName() {
        return getAttr("fiName");
    }

    public void setFiName(String fiName) {
        setAttr("fiName", fiName);
    }

    public String getFiContactRole() {
        return getAttr("fiContactRole");
    }

    public void setFiContactRole(String fiContactRole) {
        setAttr("fiContactRole", fiContactRole);
    }

    public String getFiClientAnnualSales() {
        return getAttr("fiClientAnnualSales");
    }
    
    public void setFiClientAnnualSales(String fiClientAnnualSales) {
        setAttr("fiClientAnnualSales", fiClientAnnualSales);
    }

    public String getCustomerField1() {
        return getAttr("customerField1");
    }
    
    public void setCustomerField1(String customerField1) {
        setAttr("customerField1", customerField1);
    }

    public String getCustomerField2() {
        return getAttr("customerField2");
    }
    
    public void setCustomerField2(String customerField2) {
        setAttr("customerField2", customerField2);
    }

    public String getCustomerField3() {
        return getAttr("customerField3");
    }
    
    public void setCustomerField3(String customerField3) {
        setAttr("customerField3", customerField3);
    }
    public String getCustomerField4() {
        return getAttr("customerField4");
    }
    
    public void setCustomerField4(String customerField4) {
        setAttr("customerField4", customerField4);
    }

    public String getCustomerField5() {
        return getAttr("customerField5");
    }

    public void setCustomerField5(String customerField5) {
        setAttr("customerField5", customerField5);
    }

    public String getCustomerField6() {
        return getAttr("customerField6");
    }
    
    public void setCustomerField6(String customerField6) {
        setAttr("customerField6", customerField6);
    }

    public String getCustomerField7() {
        return getAttr("customerField7");
    }

    public void setCustomerField7(String customerField7) {
        setAttr("customerField7", customerField7);
    }

    public String getCustomerField8() {
        return getAttr("customerField8");
    }

    public void setCustomerField8(String customerField8) {
        setAttr("customerField8", customerField8);
    }

    public String getCustomerField9() {
        return getAttr("customerField9");
    }

    public void setCustomerField9(String customerField9) {
        setAttr("customerField9", customerField9);
    }

    public String getCustomerField10() {
        return getAttr("customerField10");
    }

    public void setCustomerField10(String customerField10) {
        setAttr("customerField10", customerField10);
    }

    public String getITMValue() {
        return getAttr("ITMValue");
    }

    public void setITMValue(String itmValue) {
        setAttr("ITMValue", itmValue);
    }

    public String getITMOther() {
        return getAttr("ITMOther");
    }

    public void setITMOther(String itmOther) {
        setAttr("ITMOther", itmOther);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder stringBuffer=new StringBuilder("Eloqua field update includes");

        if(StringUtils.isNotEmpty(getFirstName())){
            stringBuffer.append(" : firstName - " +getFirstName());
        }
        if(StringUtils.isNotEmpty(getLastName())){
            stringBuffer.append(" : lastName - " +getLastName());
        }
        if(StringUtils.isNotEmpty(getEmailAddress())){
            stringBuffer.append(" : emailAddress - " +getEmailAddress());
        }
        if(StringUtils.isNotEmpty(getTradeStatus())){
            stringBuffer.append(" : tradeStatus - " +getTradeStatus());
        }
        if(StringUtils.isNotEmpty(getCompanyName())){
            stringBuffer.append(" : companyName - " +getCompanyName());
        }
        if(StringUtils.isNotEmpty(getMainPhone())){
            stringBuffer.append(" : mainPhone - " +getMainPhone());
        }
        if(StringUtils.isNotEmpty(getCompanyAddress1())){
            stringBuffer.append(" : companyAddress1 - " +getCompanyAddress1());
        }
        if(StringUtils.isNotEmpty(getCompanyCity())){
            stringBuffer.append(" : companyCity - " +getCompanyCity());
        }
        if(StringUtils.isNotEmpty(getCompanyProvince())){
            stringBuffer.append(" : companyProvince - " +getCompanyProvince());
        }
        if(StringUtils.isNotEmpty(getCompanyPostal())){
            stringBuffer.append(" : companyPostal - " +getCompanyPostal());
        }
        if(StringUtils.isNotEmpty(getCompanyCountry())){
            stringBuffer.append(" : companyCountry - " +getCompanyCountry());
        }
        if(StringUtils.isNotEmpty(getAnnualSales())){
            stringBuffer.append(" : annualSales - " +getAnnualSales());
        }
        if(StringUtils.isNotEmpty(getEmployees())){
            stringBuffer.append(" : employees - " +getEmployees());
        }
        if(StringUtils.isNotEmpty(getPainPoint())){
            stringBuffer.append(" : painPoint - " +getPainPoint());
        }
        if(StringUtils.isNotEmpty(getIndustry())){
            stringBuffer.append(" : industry - " +getIndustry());
        }
        if(StringUtils.isNotEmpty(getMarketsInt())){
            stringBuffer.append(" : marketsInt - " +getMarketsInt());
        }
        if(StringUtils.isNotEmpty(getCaslConsent())){
            stringBuffer.append(" : caslConsent - " +getCaslConsent());
        }
        if(StringUtils.isNotEmpty(getParagraphTextTAS())){
            stringBuffer.append(" : paragraphTextTAS - " +getParagraphTextTAS());
        }
        if(StringUtils.isNotEmpty(getPAUFlag())){
            stringBuffer.append(" : getPAUFlag - " +getPAUFlag());
        }
        if(StringUtils.isNotEmpty(getPAUDate())){
            stringBuffer.append(" : getPAUDate - " +getPAUDate());
        }
        if(StringUtils.isNotEmpty(getCoreCustomer())){
            stringBuffer.append(" : getCoreCustomer - " +getCoreCustomer());
        }
        // Gated Lead Gen Form for Campaigns
        if(StringUtils.isNotEmpty(getCoMainPhoneExt())){
            stringBuffer.append(" : getCoMainPhoneExt - " +getCoMainPhoneExt());
        }
        if(StringUtils.isNotEmpty(getFiLoan())){
            stringBuffer.append(" : getFiLoan - " +getFiLoan());
        }
        if(StringUtils.isNotEmpty(getCovidWC())){
            stringBuffer.append(" : getCovidWC - " +getCovidWC());
        }
        if(StringUtils.isNotEmpty(getSalesTiming())){
            stringBuffer.append(" : getSalesTiming - " +getSalesTiming());
        }
        if(StringUtils.isNotEmpty(getNonCDNCurrencyExperience())){
            stringBuffer.append(" : getNonCDNCurrencyExperience - " +getNonCDNCurrencyExperience());
        }
        if(StringUtils.isNotEmpty(getFxContractExperience())){
            stringBuffer.append(" : getFxContractExperience - " +getFxContractExperience());
        }
        if(StringUtils.isNotEmpty(getFiName())){
            stringBuffer.append(" : getFiName - " +getFiName());
        }
        if(StringUtils.isNotEmpty(getFiContactRole())){
            stringBuffer.append(" : getFiContactRole - " +getFiContactRole());
        }
        if(StringUtils.isNotEmpty(getFiClientAnnualSales())){
            stringBuffer.append(" : getFiClientAnnualSales - " +getFiClientAnnualSales());
        }
        if(StringUtils.isNotEmpty(getCustomerField1())){
            stringBuffer.append(" : getCustomerField1 - " +getCustomerField1());
        }
        if(StringUtils.isNotEmpty(getCustomerField2())){
            stringBuffer.append(" : getCustomerField2 - " +getCustomerField2());
        }
        if(StringUtils.isNotEmpty(getCustomerField3())){
            stringBuffer.append(" : getCustomerField3 - " +getCustomerField3());
        }
        if(StringUtils.isNotEmpty(getCustomerField1())){
            stringBuffer.append(" : getCustomerField1 - " +getCustomerField1());
        }
        if(StringUtils.isNotEmpty(getCustomerField4())){
            stringBuffer.append(" : getCustomerField4 - " +getCustomerField4());
        }
        if(StringUtils.isNotEmpty(getCustomerField5())){
            stringBuffer.append(" : getCustomerField5 - " +getCustomerField5());
        }
        if(StringUtils.isNotEmpty(getCustomerField6())){
            stringBuffer.append(" : getCustomerField6 - " +getCustomerField6());
        }
        if(StringUtils.isNotEmpty(getCustomerField7())){
            stringBuffer.append(" : getCustomerField7 - " +getCustomerField7());
        }
        if(StringUtils.isNotEmpty(getCustomerField8())){
            stringBuffer.append(" : getCustomerField8 - " +getCustomerField8());
        }
        if(StringUtils.isNotEmpty(getCustomerField9())){
            stringBuffer.append(" : getCustomerField9 - " +getCustomerField9());
        }
        if(StringUtils.isNotEmpty(getCustomerField10())){
            stringBuffer.append(" : getCustomerField10 - " +getCustomerField10());
        }
        if(StringUtils.isNotEmpty(getITMValue())){
            stringBuffer.append(" : getITMValue - " +getITMValue());
        }
        if(StringUtils.isNotEmpty(getITMOther())){
            stringBuffer.append(" : getITMOther - " +getITMOther());
        }

        return stringBuffer.toString();
    }

}

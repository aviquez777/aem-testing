package com.edc.edcweb.core.service;

import java.util.ArrayList;

import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaDocHistory;
import com.edc.edcweb.core.serviceImpl.pojo.MyEDCTransaction;

public interface EloquaService {

    String getFormSubmitURL();
    String getUrlToGetBaseUrl();
    String getEndPointFallBack();
    String getTokenUrl();
    String getSiteId();
    String getNewsletterFormName();
    String getScheduleCallFormName();
    String getScheduleCallFormId();
    String getProductInquiryFormName();
    String getContactUsFormName();
    String getContactUsFormId();
    String getMSTLFormName();
    String getMSTLFormId();
    String getMatchmakingFormName();
    String getSubscriptionsFormId();
    String getSubscriptionsFormName();
    String getExportHelpRequestFormName();
    String getExportHelpRequestFormId();
    String getTAPFormName();
    String getTAPFormId();
    String getAPSGFormName();
    String getAPSGFormId();
    String getBrokerRegistrationFormName();
    String getBrokerRegistrationFormId();
    String getKnowledgeCostumerFormName();
    String getInListServiceProviderIntakeFormName();
    String getInListServiceProviderIntakeFormId();
    String getEhFormName();
    String getEhFormId();
    String getEhLookupIdVisitor();
    String getEhLookupIdPrimary();

    EloquaContact getEloquaContact(String email);
    ArrayList<EloquaDocHistory> getEloquaDocHistory(String email, String docId);
    String updateEloquaContact(EloquaContact contactBean, boolean addToNewsLetter, boolean updateEmailDocID, boolean isExportHelp, boolean checkPau,boolean needUpdatePAUDate);

    String getProgressProfilingFormName();
    String getProgressProfilingFormId();
    String getProgressProfilingServiceIDCompanyName();
    String getProgressProfilingServiceIDUserName();
    String getProgressProfilingServiceIDPassword();
    String getOath2ClientID();
    String getOath2ClientSecurity();
    String getCDOUserInfo();
    String getCDODocHistory();
    String getCompanyName();
    String getEmailAddress();
    String getFirstName();
    String getLanguage();
    String getLastName();
    String getTitle();
    String getTradeStatus();
    String getCompanyProvince();
    String getPainPoint();
    String getIndustry();
    String getCompanyCountry();
    String getAnnualSales();
    String getGUID();
    String getCompanyAddress1();
    String getCompanyAddress2();
    String getCompanyCity();
    String getMainPhone();
    String getEmployees();
    String getCompanyPostal();
    String getMarketsInt();
    String getKnowledgeCustomerStage();
    String getDocHistoryTimesAccessed();
    String getDocHistoryEmailAddressWithDocID();
    String getDocHistoryEmailAddress();
    String getDocHistoryDocID();
    String getPAUFlag();
    String getPAUDate();
    String getCoreCustomer();
    String getMyEDCProfileCDOId();

    String getFirstNameFieldId();
    String getLastNameFieldId();
    String getJobTitleFieldId();
    String getTradeStatusFieldId();
    String getMainPhoneFieldId();
    String getCompanyAddress1FieldId();
    String getCompanyCityFieldId();
    String getCompanyProvinceFieldId();
    String getCompanyPostalFieldId();
    String getCompanyCountryFieldId();
    String getAnnualSalesFieldId();
    String getEmployeesFieldId();
    String getCompanyIdFieldId();
    String getIndustryFieldId();
    String getMarketsIntFieldId();
    String getPainPointFieldId();

    /**
     * checkMyEDCAccountExistsByEMailId check if given email has
     * and MyEDC Account
     * @param emailId email to check account
     * @return true if there's a record, false otherwise
     */
    Boolean checkMyEDCAccountExistsByEMailId(String emailId);
    String getMyEDCTransForm();
    String getMyEDCTransFormName();
    String getMyEDCTransUniqueCode();
    String getMyEDCTransExternalID();
    String getMyEDCTransTimeStamp();
    String getMyEDCTransAemPath();
    String getMyEDCTransCounter();
    String getMyEDCEmail();
    String getMyEDCTrafficSrc();

    MyEDCTransaction getMyEDCTransaction(String uniqueCode);
    String updateMyEDCTransaction(MyEDCTransaction myEDCTransaction, boolean update);
}

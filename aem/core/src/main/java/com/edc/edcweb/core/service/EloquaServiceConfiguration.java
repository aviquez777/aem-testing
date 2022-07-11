package com.edc.edcweb.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eloqua Service Configuration", description = "Eloqua configuration for EDC integration")
public @interface EloquaServiceConfiguration {

    @AttributeDefinition(
            name = "Form submit URL",
            description = "Eloqua form submit URL",
            type = AttributeType.STRING)
    String formSubmitURL() default "https://s189013793.t.eloqua.com/e/f2";

    @AttributeDefinition(
            name = "Get Base Url",
            description = "Endpoint to Get Base Url ",
            type = AttributeType.STRING)
    String urlToGetBaseUrl() default "https://login.eloqua.com/id";

    @AttributeDefinition(
            name = "Fallback endpoint",
            description = "Fallback endpoint if cannot retrieve ",
            type = AttributeType.STRING)
    String endPointFallBack() default "https://secure.p01.eloqua.com/API/REST/{version}/";

    @AttributeDefinition(
            name = "Get Token Url",
            description = "Endpoint to Get the token ",
            type = AttributeType.STRING)
    String tokenUrl() default "https://login.eloqua.com/auth/oauth2/token";

    @AttributeDefinition(
            name = "Eloqua Site ID",
            description = "Eloqua site ID")
    String siteId()  default "189013793";

    @AttributeDefinition(
            name = "APSG Program form name",
            description = "Eloqua form name for APSG",
            type = AttributeType.STRING)
    String APSGFormName() default "MIP16-APSG-229";

    @AttributeDefinition(
            name = "APSG form Id",
            description = "Eloqua form Id APSG",
            type = AttributeType.STRING)
    String APSGFormId() default "SubLevel1";

    @AttributeDefinition(
            name = "Eloqua Newsletter form name",
            description = "Eloqua form name for newsletter subscription",
            type = AttributeType.STRING)
    String newsletterFormName() default "MIP2-209-TESTONLY";

    @AttributeDefinition(
            name = "Schedule a Call form name",
            description = "Eloqua form name for schedule a call form",
            type = AttributeType.STRING)
    String scheduleCallFormName() default "MIP8-CallBack-194";

    @AttributeDefinition(
            name = "Schedule a Call form Id",
            description = "Eloqua form Id for schedule a call form",
            type = AttributeType.STRING)
    String scheduleCallFormId() default "form194";

    @AttributeDefinition(
            name = "Contact us form name",
            description = "Eloqua form name for contact us form",
            type = AttributeType.STRING)
    String contactUsFormName() default "MIP8-EventTemplate-207";

    @AttributeDefinition(
            name = "Contact us form Id",
            description = "Eloqua form Id for contact us form",
            type = AttributeType.STRING)
    String contactUsFormId() default "form207";

    @AttributeDefinition(
            name = "MSTL form name",
            description = "Eloqua form name for MSTL form",
            type = AttributeType.STRING)
    String mstlFormName() default "MIP5-MSTL-336";

    @AttributeDefinition(
            name = "MSTL form Id",
            description = "Eloqua form Id for MSTL form",
            type = AttributeType.STRING)
    String mstlFormId() default "form336";

    @AttributeDefinition(
            name = "Product Inquiry form name",
            description = "Eloqua form name for product inquiry form",
            type = AttributeType.STRING)
    String productInquiryFormName() default "MIP23-INT-281";

    @AttributeDefinition(
            name = "Matchmaking form name",
            description = "Eloqua form name for matchmaking form",
            type = AttributeType.STRING)
    String matchmakingFormName() default "MIP35-MM-192";

    @AttributeDefinition(
            name = "Subscriptions form name",
            description = "Eloqua form name for subscriptions form",
            type = AttributeType.STRING)
    String subscriptionsFormName() default "MIP2-201";

    @AttributeDefinition(
            name = "Subscriptions form Id",
            description = "Eloqua form Id for subscriptions form",
            type = AttributeType.STRING)
    String subscriptionsFormId() default "form201";

    @AttributeDefinition(
            name = "Export help request form name",
            description = "Eloqua form name for export help request form",
            type = AttributeType.STRING)
    String exportHelpRequestFormName() default "MIP19-TASQ-196";

    @AttributeDefinition(
            name = "Export help request form Id",
            description = "Eloqua form Id export help request form",
            type = AttributeType.STRING)
    String exportHelpRequestFormId() default "TASLevel5";

    @AttributeDefinition(
            name = "Trade Accelerator Program form name",
            description = "Eloqua form name for Trade Accelerator Program form",
            type = AttributeType.STRING)
    String TAPFormName() default "MIP65-TAP-229";

    @AttributeDefinition(
            name = "Trade Accelerator Program form Id",
            description = "Eloqua form Id Trade Accelerator Program form",
            type = AttributeType.STRING)
    String TAPFormId() default "TAPLevel5";

    //Broker Registration Form

    @AttributeDefinition(
            name = "Trade Accelerator Program form name",
            description = "Eloqua form name for Trade Accelerator Program form",
            type = AttributeType.STRING)
    String BrokerRegistrationFormName() default "MIP14-BR-248";

    @AttributeDefinition(
            name = "Trade Accelerator Program form Id",
            description = "Eloqua form Id Trade Accelerator Program form",
            type = AttributeType.STRING)
    String BrokerRegistrationFormId() default "231";

    @AttributeDefinition (
            name = "Knowledge Costumer form name",
            description = "Eloqua form name for knowledge costumer form",
            type = AttributeType.STRING)
    String knowledgeCostumerFormName() default "MIP19-PAU-222";

    @AttributeDefinition(
            name = "InList Service Provider Intake form name",
            description = "Eloqua form name for InList Service Provider Intake form",
            type = AttributeType.STRING)
    String inListServiceProviderIntakeFormName() default "MIP60-FF-241";

    @AttributeDefinition(
            name = "InList Service Provider Intake form Id",
            description = "Eloqua form Id InList Service Provider Intake form",
            type = AttributeType.STRING)
    String inListServiceProviderIntakeFormId() default "242";

    @AttributeDefinition(
            name = "Eloqua EH form name",
            description = "Eloqua form name for EH form",
            type = AttributeType.STRING)
    String ehFormName() default "MIP19-EHQ-303";

    @AttributeDefinition(
            name = "Eloqua EH form id",
            description = "Eloqua form id for EH form",
            type = AttributeType.STRING)
    String ehFormId() default "MIP19-EHQ-303";

    @AttributeDefinition(
            name = "Eloqua EH form LookupIdVisitor for Prepopulation",
            description = "Eloqua form LookupIdVisitor for Prepopulation",
            type = AttributeType.STRING)
    String ehLookupIdVisitor() default "";

    @AttributeDefinition(
            name = "Eloqua EH form LookupIdPrimary for Prepopulation",
            description = "Eloqua form LookupIdPrimary for Prepopulation",
            type = AttributeType.STRING)
    String ehLookupIdPrimary() default "";

    //Progressive Profiling common settings

    @AttributeDefinition(
            name = "Eloqua progress profiling form name",
            description = "Eloqua form name for EDC progressive profiling")
    String progressProfilingFormName() default "MIP18-KC-197";

    @AttributeDefinition(
            name = "Eloqua progress profiling form Id",
            description = "Eloqua form Id for EDC progressive profiling")
    String progressProfilingFormId() default "KCLevel5";

    @AttributeDefinition(
            name = "Service account company name of Eloqua RESTful service",
            description = "Service account company name for Eloqua RESTful services")
    String progressProfilingServiceIDCompanyName() default "EDCTestQAC";

    @AttributeDefinition(
            name = "Service account ID of Eloqua RESTful service",
            description = "Service account id for Eloqua RESTful services")
    String progressProfilingServiceIDUserName() default "AEM.Integration";

    @AttributeDefinition(
            name = "Service account password for Eloqua RESTful services",
            description = "Service account password for Eloqua RESTful services")
    String progressProfilingServiceIDPassword() default "@3m1p@lqJm!";

    @AttributeDefinition(
            name = "Client ID for Eloqua Oath2 authentication",
            description = "Client ID for Eloqua Oath2 authentication")
    String oath2ClientID() default "4378c90e-1b40-4099-aa81-ece34aac9759";

    @AttributeDefinition(
            name = "Client security for Eloqua Oath2 authentication",
            description = "Client security for Eloqua Oath2 authentication")
    String oath2ClientSecurity() default "1I6CpfBBGvEWUYnc2xAqGLmc7NpG5PrFJPNlbwZEQTf8hBIraQ83Pv7YyAGOKnwkiSu5838MPlXcc32fDsrMeHohLcVbvn8NMZ5J";


    //CDO for getting user info. QAC=65   PROD=65
    @AttributeDefinition(
            name = "CDO ID for getting user information",
            description = "Eloqua CDO ID used in RESTful API to get user information")
    String CDOUserInfo() default "65";

    //CDO for getting doc history. QAC=63   PROD=63
    @AttributeDefinition(
            name = "CDO ID for getting document view history",
            description = "Eloqua CDO ID used in RESTful API to get user's document history")
    String CDODocHistory()  default "63";

    //Progressive Profiling field mapping for restful response - user information

    @AttributeDefinition(
            name = "Company",
            description = "Company")
    String companyName()  default "868";

    @AttributeDefinition(
            name = "Email Address",
            description = "Email Address")
    String emailAddress()  default "865";

    @AttributeDefinition(
            name = "First Name",
            description = "First Name")
    String firstName() default "871";

    @AttributeDefinition(
            name = "Language",
            description = "Language")
    String language() default "805";

    @AttributeDefinition(
            name = "Last Name",
            description = "Last Name")
    String lastName() default "872";

    @AttributeDefinition(
            name = "Title",
            description = "Title")
    String title() default "873";

    @AttributeDefinition(
            name = "Export Status",
            description = "Export Status")
    String tradeStatus() default "884";

    @AttributeDefinition(
            name = "State or Province",
            description = "State or Province")
    String companyProvince()  default "883";

    @AttributeDefinition(
            name = "Export Challenges",
            description = "Export Challenges")
    String painPoint() default "882";

    @AttributeDefinition(
            name = "EDC Supply Chain",
            description = "EDC Supply Chain")
    String industry() default "881";

    @AttributeDefinition(
            name = "Country",
            description = "Country")
    String companyCountry() default "880";

    @AttributeDefinition(
            name = "Annual Revenue",
            description = "Annual Revenue")
    String annualSales() default "879";

    @AttributeDefinition(
            name = "GUID",
            description = "GUID")
    String GUID() default "877";

    @AttributeDefinition(
            name = "Address 1",
            description = "Address 1")
    String companyAddress1() default "866";

    @AttributeDefinition(
            name = "Address 2",
            description = "Address 2")
    String companyAddress2()  default "878";

    @AttributeDefinition(
            name = "City",
            description = "City")
    String companyCity() default "867";

    @AttributeDefinition(
            name = "Company Main Phone",
            description = "Company Main Phone")
    String mainPhone() default "869";

    @AttributeDefinition(
            name = "Employees",
            description = "Employees")
    String employees() default "870";

    @AttributeDefinition(
            name = "Zip or Postal Code",
            description = "Zip or Postal Code")
    String companyPostal() default "874";

    @AttributeDefinition(
            name = "Countries Planning on Exporting",
            description = "Countries Planning on Exporting")
    String marketsInt() default "3799";

    @AttributeDefinition(
            name = "Premium Access User Flag",
            description = "Premium Access User Flag")
    String PAUFlag() default "1385";

    @AttributeDefinition(
            name = "Premium Access User Date",
            description = "Premium Access User Date")
    String PAUDate() default "1386";

    @AttributeDefinition(
            name = "Core Customer",
            description = "Core Customer")
    String coreCustomer() default "1628";

    //This one has no UI item to match
    @AttributeDefinition(
            name = "Knowledge Customer Stage",
            description = "Knowledge Customer Stage")
    String knowledgeCustomerStage() default "876";

    //Progressive Profiling field mapping for restful response - doc history

    @AttributeDefinition(
        name = "# of Times Accessed",
        description = "# of Times Accessed")
    String docHistoryTimesAccessed()  default "862";

    @AttributeDefinition(
            name = "EmailAddress|DocID",
            description = "EmailAddress|DocID")
    String docHistoryEmailAddressWithDocID() default "860";

    @AttributeDefinition(
            name = "EmailAddress",
            description = "EmailAddress")
    String docHistoryEmailAddress() default "858";

    @AttributeDefinition(
            name = "Document Id",
            description = "Document Id")
    String docHistoryDocID() default "859";

    //MyEDC Profile field Id
    @AttributeDefinition(name = "My Edc Profile CDO Id", description = "Id for the MyEDC Profile CustomObjectData to be used ", type = AttributeType.STRING)
    String getMyEDCProfileCDOId() default "73";

    //MyEDC Transactions field mapping for restful response - premium content transactions
    @AttributeDefinition(
            name = "MyEDC Transactions Form ID",
            description = "MyEDC Transactions Form ID")
    String MyEDCTransForm() default "97";

    @AttributeDefinition(
            name = "MyEDC Transactions Form Name",
            description = "MyEDC Transactions Form Name")
    String myEDCTransFormName() default "myEDC - Transactions";

    @AttributeDefinition(
            name = "MyEDC Transactions Unique Code",
            description = "ACC ExternalID | AEM path")
    String myEDCTransUniqueCode() default "1569";

    @AttributeDefinition(
            name = "MyEDC Transactions External ID",
            description = "ACC ExternalID")
    String myEDCTransExternalID() default "1502";

    @AttributeDefinition(
            name = "MyEDC Transactions Last accessed timestamp",
            description = "Last accessed timestamp")
    String myEDCTransTimeStamp() default "1504";

    @AttributeDefinition(
            name = "MyEDC TransactionsAEM path ",
            description = "AEM path ")
    String myEDCTransAemPath() default "1505";

    @AttributeDefinition(
            name = "MyEDC Transactions Counter",
            description = "Counter")
    String myEDCTransCounter() default "1568";
    
    @AttributeDefinition(
            name = "MyEDC Email",
            description = "Email")
    String myEDCEmail() default "1716";
    
    // Eloqua Traffic Source in CDO
    @AttributeDefinition(
            name = "MyEDC Traffic Source",
            description = "Traffic Source")
    String myEDCTrafficSrc() default "1754";

    // Webinar Partner CASL in CDO
    @AttributeDefinition(
            name = "MyEDC Partner's CASL",
            description = "MyEDC Partner's CASL")
    String myEDCPartnersCASL() default "1756";

    // Gated Lead Gen Form for Campaigns
    @AttributeDefinition(
            name = "coMainPhoneExt",
            description = "coMainPhoneExt")
    String coMainPhoneExt();

    @AttributeDefinition(
            name = "Has FI Loan",
            description = "Has FI Loan")
    String fiLoan() default "3259";

    @AttributeDefinition(
            name = "WC Need from COVID",
            description = "WC Need from COVID")
    String covidWC() default "3260";

    @AttributeDefinition(
            name = "Sales Timing",
            description = "Sales Timing")
    String salesTiming();

    @AttributeDefinition(
            name = "nonCDNCurrencyExperience",
            description = "nonCDNCurrencyExperience")
    String nonCDNCurrencyExperience();
    
    @AttributeDefinition(
            name = "fxContractExperience",
            description = "fxContractExperience")
    String fxContractExperience();

    @AttributeDefinition(
            name = "fiName",
            description = "fiName")
    String fiName();

    @AttributeDefinition(
            name = "fiContactRole",
            description = "fiContactRole")
    String fiContactRole();

    @AttributeDefinition(
            name = "fiClientAnnualSales",
            description = "fiClientAnnualSales")
    String fiClientAnnualSales();

    @AttributeDefinition(
            name = "customerField1",
            description = "customerField1")
    String customerField1();

    @AttributeDefinition(
            name = "customerField2",
            description = "customerField2")
    String customerField2();

    @AttributeDefinition(
            name = "customerField3",
            description = "customerField3")
    String customerField3();

    @AttributeDefinition(
            name = "customerField4",
            description = "customerField4")
    String customerField4();

    @AttributeDefinition(
            name = "customerField5",
            description = "customerField5")
    String customerField5();

    @AttributeDefinition(
            name = "customerField6",
            description = "customerField16")
    String customerField6();

    @AttributeDefinition(
            name = "customerField7",
            description = "customerField7")
    String customerField7();

    @AttributeDefinition(
            name = "customerField8",
            description = "customerField8")
    String customerField8();

    @AttributeDefinition(
            name = "customerField9",
            description = "customerField9")
    String customerField9();

    @AttributeDefinition(
            name = "customerField10",
            description = "customerField10")
    String customerField10();

    @AttributeDefinition(
            name = "ITMValue",
            description = "ITMValue")
    String itmValue();

    @AttributeDefinition(
            name = "ITMOther",
            description = "ITMOther")
    String itmOther();
}

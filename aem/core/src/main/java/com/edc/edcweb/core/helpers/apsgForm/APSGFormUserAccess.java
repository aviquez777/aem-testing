package com.edc.edcweb.core.helpers.apsgForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageToggleUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.models.EloquaData;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaDocHistory;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *
 * APSGFormUserAccess is used to act as business layer for the process.
 * The user access gather the EloquaContact and the form data and is used to verify access.
 * fieldsRequiredFromEloqua and fieldsSubmittedToEloqua are used to communicate to the client and update Eloqua
 */

public class APSGFormUserAccess {
    private static final Logger log = LoggerFactory.getLogger(APSGFormUserAccess.class);

    private String docID = "";
    private String inputEmail = "";
    private Boolean newUser = true;

    private EloquaService eloquaService = null;
    private EloquaContact eloquaContact = new EloquaContact();
    private HashMap<String, String []> fieldsSubmittedToEloqua = new HashMap<>();

    private boolean updateDocHistory = false;

    public APSGFormUserAccess(String email, String docID) {
        log.debug("Contructor: {}, idocID {}", email, docID);

        this.inputEmail = email;
        this.docID = docID;
        this.eloquaService = getEloquaService();
    }

    private void addSubmittedField(String fieldId, String value) {
         String arr[]= new String[1];
         arr[0] = value;
         this.addSubmittedField(fieldId, arr);
    }

    public boolean isUserAccessedDocBefore() {
        log.debug("isUserAccessedDocBefore email [{}] docId [{}] ", this.inputEmail, this.docID);
        boolean accessedBefore = false;
        String checkDocID = this.docID;

        // For Help Request Form, docId is null
        if (checkDocID == null || checkDocID.isEmpty()) {
            return false;
        }

        ArrayList<EloquaDocHistory> docHistory = this.eloquaService.getEloquaDocHistory(this.inputEmail, checkDocID);

        if (docHistory != null && docHistory.size()>0) {
            // Regular docID: only one result returned
            String timeAccessed = docHistory.get(0).getTimesAccessed();

            if (timeAccessed != null && !timeAccessed.isEmpty()) {
                accessedBefore = true;
            }
        }

        log.debug("isUserAccessedDocBefore docId [{}] accessedBefore:{} ", checkDocID, accessedBefore);
        return accessedBefore;
    }

    public String initializeEloquaContact(){
        this.eloquaContact = this.eloquaService.getEloquaContact(this.inputEmail);
        String response  = "";

        if (this.eloquaContact.getEmailAddress().isEmpty()) {
            log.debug("New user [{}] which is not exist in Eloqua ", this.inputEmail);
            eloquaContact.setEmailAddress(this.inputEmail);

            // Create the contact in Eloqua as soon as they submit the email
            response = this.eloquaService.updateEloquaContact(eloquaContact, true, false, false, false, false);
        } else {
            // We know eloqua show this is an existing user.
            response = "200";
            this.newUser = false;
        }

        log.debug("initializeEloquaContact: existing user [{}] retrieved.",this.inputEmail);
        return response;
    }

    public void addSubmittedField( String fieldId, String [] values) {
        String joined2 = String.join("::", values);
        log.debug("addSubmittedField[{}] : {}", fieldId, joined2 );
        fieldsSubmittedToEloqua.put(fieldId, values);
    }

    public String updateEloquaContact() {
        String statusCode = "";
        EloquaContact contact = new EloquaContact();
        Boolean casl = false;

        for (Map.Entry<String, String[]> entry : this.fieldsSubmittedToEloqua.entrySet()) {
            log.debug("contact setAttr[{}] : {}", entry.getKey(), entry.getValue());
            String[] strArr=(String[])entry.getValue();

            if (strArr.length == 1) {
                contact.setAttr(entry.getKey(), strArr[0]);
            } else if (strArr.length > 1) {
                contact.setAttr(entry.getKey(), String.join(",", strArr));
            }
        }
        
        try {
        	String caslConsent = String.join("", this.fieldsSubmittedToEloqua.get("caslConsent"));
        	casl = caslConsent.equals("yes") ? true : false;
        } catch(Exception ex) {
        	log.error("APSG ERROR" + ex);
        }

        statusCode = this.eloquaService.updateEloquaContact(contact, casl, this.updateDocHistory, false, false, false);
        log.debug("updateEloquaContact ok");

        return statusCode;
    }

    public void addHiddenSubmittedFields(SlingHttpServletRequest request, String pagePath) {
        if (StringUtils.isBlank(pagePath)) {
            pagePath = request.getParameter("resourcePath");
        }
        Page usePage = request.getResourceResolver().resolve(pagePath).adaptTo(Page.class);
        EloquaData eloquaData = new EloquaData(request.getResourceResolver(), usePage);
        LanguageToggleUtil langUtil = LanguageToggleUtil.getInstance();
        String language =  request.getParameter("lang");
        String pageUrlFull = LinkResolver.addHtmlExtension(usePage.getPath());
        pageUrlFull = request.getScheme() + "://" + Constants.WHOS_ON_DOMAIN_PROD + pageUrlFull;

        String com=",[ ]*";

        addSubmittedField("emailAddress", this.inputEmail);
        addSubmittedField("gatedURL", pageUrlFull );
        addSubmittedField("lang", language );
        addSubmittedField("ownerID", eloquaData.getOwnerTags().split(com));
        addSubmittedField("exportJourney", eloquaData.getCategoryTags().split(com));
        addSubmittedField("exportPain", eloquaData.getSubCategoryTags().split(com));
        addSubmittedField("buyerjourney", eloquaData.getExportStatusTags().split(com));
        addSubmittedField("personaID", eloquaData.getPersonaTags().split(com));
        addSubmittedField("assetTier", "");

        if ( this.updateDocHistory ) {
            addSubmittedField("docID", this.docID);
        }
    }

    private EloquaService  getEloquaService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(EloquaService.class).getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(EloquaService.class.getName());
        EloquaService eloquaService = (EloquaService)bundleContext.getService(serviceRef);
        return eloquaService;
    }

    public EloquaContact getEloquaContact() {
        return this.eloquaContact;
    }

    public Map<String,String[]> getFieldsSubmittedToEloqua(){
        return new HashMap<>(fieldsSubmittedToEloqua);
    }

    public boolean getUpdateDocHistory() {
        return this.updateDocHistory;
    }

    public void setUpdateDocHistory(boolean value) {
        this.updateDocHistory = value;
    }

    public Boolean getNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public String getEmail(){
        return this.inputEmail;
    }

    public String getDocID(){
        return this.docID;
    }
}

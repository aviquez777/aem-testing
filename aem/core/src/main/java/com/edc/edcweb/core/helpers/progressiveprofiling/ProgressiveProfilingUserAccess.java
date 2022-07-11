package com.edc.edcweb.core.helpers.progressiveprofiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.models.EloquaData;
import com.edc.edcweb.core.service.EloquaService;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;
import com.edc.edcweb.core.serviceImpl.pojo.EloquaDocHistory;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * ProgressiveProfilingUserAccess is used to act as business layer for the process.
 * The user access gather the EloquaContact and the form data and is used to verify access.
 * fieldsRequiredFromEloqua and fieldsSubmittedToEloqua are used to communicate to the client and update Eloqua
 */

public class ProgressiveProfilingUserAccess
{

    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfilingUserAccess.class);

    private String docID = "";
    private String premiumUrl = "";
    private String inputEmail = "";
    private int targetAccessLevel = 1;
    private int currentUserLevel = 0;
    private Boolean newUser = true;

    private EloquaService eloquaService = null;
    private EloquaContact eloquaContact = new EloquaContact();
    private HashMap<String, Integer> fieldsRequiredFromEloqua = new HashMap<>();
    private HashMap<String, String []> fieldsSubmittedToEloqua = new HashMap<>();
    private Boolean accessAllowed = false;
    private String teaserUrl;

    private int afterAccessUserLevel=0;

    private boolean updateDocHistory = false;

    private String mode = Constants.Properties.PROGRESSIVEPROFILING_MODE_NORMAL;
    private String paragraphTextTAS;

    private String coreCustomer = "";
    private String knowledgeCustomerStage = "";
    
    private static final String DOC_ID = "docID";

    public ProgressiveProfilingUserAccess(String email, String docID, String premiumUrl, int targetAccessLevel) {

        log.debug("Contructor: {}, idocID {}", email, docID);

        this.inputEmail = email;
        this.docID = docID;
        this.premiumUrl = premiumUrl;
        this.targetAccessLevel = targetAccessLevel;
        this.accessAllowed = false;
        this.eloquaService = getEloquaService();
    }

    private void addSubmittedField(String fieldId, String value) {
         String arr[]= new String[1];
         arr[0] = value;
         this.addSubmittedField(fieldId, arr);
    }

    private EloquaService  getEloquaService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(EloquaService.class).getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(EloquaService.class.getName());
        EloquaService eloquaService = (EloquaService)bundleContext.getService(serviceRef);
        return eloquaService;
    }


    public String getEmail(){
        return this.inputEmail;
    }

    public String getDocID(){
        return this.docID;
    }

    public String getPremiumUrl(){
        return this.premiumUrl;
    }

    public int getTargetAccessLevel(){
        return this.targetAccessLevel;
    }

    public int getCurrentUserLevel(){
        return this.currentUserLevel;
    }

    public Boolean getAccessAllowed(){
        return this.accessAllowed;
    }

    public void setAccessAllowed(Boolean ival){
        this.accessAllowed = ival;
    }

    public void setCurrentUserLevel(int ival){
        this.currentUserLevel = ival;
    }

    public boolean isUserAccessedDocBefore() {
        log.debug("isUserAccessedDocBefore email [{}] docId [{}] ", this.inputEmail, this.docID);
        boolean accessedBefore = false;

        String checkDocID = this.docID;

        //for Help Request Form, docId is null
        if(checkDocID==null || checkDocID.isEmpty()) {
            return false;
        }

        if ( this.docID.startsWith(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_DOCID )){
            checkDocID = Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_DOCID + "*";
        }

        ArrayList<EloquaDocHistory> docHistory = this.eloquaService.getEloquaDocHistory(this.inputEmail,checkDocID );

        if(docHistory != null && docHistory.size()>0) {
            if ( this.docID.startsWith(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_DOCID )){
                //MEA: docID=MEA-* will return multiple results
                //We need to check if returned history contains: MEA-UNLOCKED, MEA-US or MEA-*(others)
                Iterator<EloquaDocHistory> itor = docHistory.iterator();
                while(itor.hasNext()) {
                    EloquaDocHistory aDocHistory = itor.next();
                    String historyDocId = aDocHistory.getDocID();
                    String timeAccessed = aDocHistory.getTimesAccessed();
                    if(historyDocId.equalsIgnoreCase(this.docID) ||
                            historyDocId.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_MEAUNLOCKED_DOCID) ||
                            (!historyDocId.equalsIgnoreCase("MEA-USA") && historyDocId.toUpperCase().startsWith(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA_DOCID))) {
                        //if docId are match, or if  returned doc history is 'MEA-UNLOCKED' or MEA-*(others but not MEA-USA) then user has access
                        if(timeAccessed != null && !timeAccessed.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
            else {
                //regular docID: only one result returned
                String timeAccessed = docHistory.get(0).getTimesAccessed();
                if(timeAccessed != null && !timeAccessed.isEmpty()) {
                    accessedBefore = true;
                }
            }
        }

        log.debug("isUserAccessedDocBefore docId [{}] accessedBefore:{} ", checkDocID, accessedBefore);
        return accessedBefore;
    }

    public String initializeEloquaContact(SlingHttpServletRequest request){

        this.eloquaContact = this.eloquaService.getEloquaContact(this.inputEmail);
        String response  = "";


        Map<String,Object> paramMap= new LinkedHashMap<>();
        paramMap.putAll( request.getParameterMap());

        if (this.eloquaContact.getEmailAddress().isEmpty() )
        {
            log.debug("New user [{}] which is not exist in Eloqua ", this.inputEmail);
            eloquaContact.setEmailAddress(this.inputEmail);

            //We need add more hidden fields to eloquaContact
    		for (Map.Entry<String, Object> entry : paramMap.entrySet())
   		 	{
    			String key = entry.getKey();
    			String[] values = (String [])entry.getValue();
    			String value = "";
    			if(values.length>0) {
    				value = values[0];
    			}

	   			if ( key.equalsIgnoreCase("fullFromPage")) {
	   				eloquaContact.setAttr("fullFromPage",value );
	   			}else if(key.equalsIgnoreCase("fromPage")) {
	   				eloquaContact.setAttr("fromPage",value );
	   			}else if(key.equalsIgnoreCase("refPage")) {
	   				eloquaContact.setAttr("refPage",value );
	   			}else if(key.equalsIgnoreCase("elqCustomerGUID")) {
	   				eloquaContact.setAttr("elqCustomerGUID",value );
	   			}else if(key.equalsIgnoreCase("elqCookieWrite")) {
	   				eloquaContact.setAttr("elqCookieWrite",value );
	   			}else if(key.equalsIgnoreCase("inquiryID")) {
	   				eloquaContact.setAttr("inquiryID",value );
	   			}else if(key.equalsIgnoreCase(DOC_ID)) {
	   				eloquaContact.setAttr(DOC_ID,value );
	   			}
   		 	}

            if (this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
                // Include the related question for Export Help Request for,
                eloquaContact.setParagraphTextTAS(this.paragraphTextTAS);
            }



            //Create the contact in Eloqua as soon as they submit the email
            boolean isKnowlegeMode = this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER);
            response = this.eloquaService.updateEloquaContact(eloquaContact, true, false, this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST), isKnowlegeMode,false);
        }
        else {
            //Send existing user's question if Export Help Request Form
        	if(this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
        		//Bug 76300
        		eloquaContact.setParagraphTextTAS(this.paragraphTextTAS);
        		response = this.eloquaService.updateEloquaContact(eloquaContact, true, false, true, false, false);
        	}
        	else {
        		response = "200";
        	}
            this.newUser = false;
        }
        log.debug("initializeEloquaContact: existing user [{}] retrieved.",this.inputEmail);
        return response;
    }

    public EloquaContact getEloquaContact(){
        return this.eloquaContact;
    }

    public void addRequiredField( String fieldId, Integer fieldLevel){

        fieldsRequiredFromEloqua.put(fieldId, fieldLevel);
    }

    public Map<String,Integer> getFieldsRequiredFromEloqua(){

        return new HashMap<>(fieldsRequiredFromEloqua);
    }

    public void addSubmittedField( String fieldId, String [] values){

        String joined2 = String.join("::", values);
        log.debug("addSubmittedField[{}] : {}", fieldId, joined2 );
        fieldsSubmittedToEloqua.put(fieldId, values);
    }

    public Map<String,String[]> getFieldsSubmittedToEloqua(){

        return new HashMap<>(fieldsSubmittedToEloqua);

    }

    public void  updateCurrentUserLevel( ProgressiveProfilingFormData form){
        int currLevel = 5;

        Iterator<ProgressiveProfilingFormField> it = form.getContent().iterator();

        while (it.hasNext()){
            ProgressiveProfilingFormField content = it.next();
            String attrValue= this.getEloquaContact().getAttr(content.getEloquaname());

            if ( attrValue != null ){
                log.debug("getEloquaContact Eloquaname[{}] value: {} ", content.getEloquaname(), attrValue );
                if (  attrValue.isEmpty() && content.getRequired() ){
                    if ( content.getLevel() <= currLevel ){
                        currLevel = content.getLevel() - 1;
                    }
                }
            }
            else
            {
                log.debug("getEloquaContact: Not found value for Eloquaname[{}] ", content.getEloquaname() );
            }
        }

        if ( currLevel == 0 ){
            currLevel = 1;
        }

        this.currentUserLevel = currLevel;
        log.debug("updateCurrentUserLevel email[{}] level: {} ", this.getEmail(), currLevel );
    }

    public boolean checkAllFieldsValued(ProgressiveProfilingFormData form) {

        boolean allFieldsOk = true;

        Iterator<ProgressiveProfilingFormField> it = form.getContent().iterator();

        while (it.hasNext()){

            ProgressiveProfilingFormField content = it.next();

            if (  this.getEloquaContact().getAttr(content.getEloquaname()).isEmpty() && content.getRequired() ){
                log.debug("checkAllFieldsValued: found unfilled field: {} ", content.getEloquaname() );
                allFieldsOk = false;
                return allFieldsOk;
            }
        }

        log.debug("checkAllFieldsValued: all fields valuated OK");
        return allFieldsOk;
    }

    public boolean checkAllFieldsValuedForLevelOrUnder(ProgressiveProfilingFormData form, int targetAccessLevel2,boolean onlyrequired) {

        boolean ret = true;
        Iterator<ProgressiveProfilingFormField> it = form.getContent().iterator();

        while (it.hasNext()){

            ProgressiveProfilingFormField content = it.next();

            if ( content.getLevel() <= targetAccessLevel2 && this.getEloquaContact().getAttr(content.getEloquaname()).isEmpty() ){
                if ( (onlyrequired && content.getRequired()) || !onlyrequired  ){

                    ret = false;
                }
            }
        }

        return ret;
    }

    /**
     * Update the list of required fields to be populated by the user
     * @param form form fields
     * @param level user level
     */
    public void updateRequiredFieldsForLevelOrUnder(ProgressiveProfilingFormData form, int level) {
        updateRequiredFieldsForLevelOrUnder(form, level, false);
    }

    /**
     * Update the list of required fields to be populated by the user
     * @param form form fields
     * @param level user level
     * @param requestAllFields yes if need to request all the fields to the user (for PAU form)
     */
    public void updateRequiredFieldsForLevelOrUnder(ProgressiveProfilingFormData form, int level, boolean requestAllFields) {
        log.debug("updateRequiredFieldsForLevelOrUnder level {}",level );
        Iterator<ProgressiveProfilingFormField> it = form.getContent().iterator();

        while (it.hasNext()){

            ProgressiveProfilingFormField content = it.next();

            if ((content.getLevel() <= level && this.getEloquaContact().getAttr(content.getEloquaname()).isEmpty()) || requestAllFields )
            {
                 this.fieldsRequiredFromEloqua.put(content.getId(), content.getLevel() );
                 log.debug("fieldsRequiredFromEloqua id[{}]  level: {}", content.getId() , content.getLevel() );
            }
        }

        //manage exceptions
        //if companyAddress2 is required and companyAddress1 is not, then remove companyAddress2
        if (!this.fieldsRequiredFromEloqua.containsKey(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR1)
            && this.fieldsRequiredFromEloqua.containsKey(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR2) )
        {
            this.fieldsRequiredFromEloqua.remove(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR2);
             log.debug("manage exceptions: remove companyAddress2" );
        }
    }

    public String updateEloquaContact(boolean needUpdatePAUDate){

        String statusCode="";
        EloquaContact contact = new EloquaContact();

        for (Map.Entry<String, String[]> entry : this.fieldsSubmittedToEloqua.entrySet()){

            log.debug("contact setAttr[{}] : {}", entry.getKey(), entry.getValue() );
            String[] strArr=(String[])entry.getValue();

            if(strArr.length==1)
            {
                contact.setAttr(entry.getKey(), strArr[0]);
            }
            else if ( strArr.length > 1 )
            {
                contact.setAttr(entry.getKey(), String.join(",", strArr));
            }

        }

        statusCode = this.eloquaService.updateEloquaContact(contact, true, this.updateDocHistory, this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST), this.mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_KNOWLEDGE_COSTUMER), needUpdatePAUDate);
        log.debug("updateEloquaContact ok");

        return statusCode;
    }

    public void addHiddenSubmittedFields(SlingHttpServletRequest request) {
        Page teaserPage = request.getResourceResolver().resolve(this.teaserUrl).adaptTo(Page.class);
        boolean isExportHelp = mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST);
        String exportHelpPath = isExportHelp ? request.getParameter("resourcePath") : "";
        Page exportHelpPage = isExportHelp ? request.getResourceResolver().resolve(exportHelpPath).adaptTo(Page.class) : null;

        if ( teaserPage == null && exportHelpPage == null){
            log.debug("addHiddenSubmittedFields : Error teaserPage or exportHelpPage are not resolved");
            log.debug("addHiddenSubmittedFields : teaserPage: {}", this.teaserUrl);
            log.debug("addHiddenSubmittedFields : exportHelpPage: {}", exportHelpPath);
            return;
        }

        Page usePage = teaserPage != null ? teaserPage : exportHelpPage;
        EloquaData eloquaData = new EloquaData(request.getResourceResolver(), usePage);
        LanguageToggleUtil langUtil = LanguageToggleUtil.getInstance();
        String language =  langUtil.getLanguageTextFromPath(usePage.getPath());
        String premiumUrlFull = teaserPage != null ? (request.getScheme() + "://" + Constants.WHOS_ON_DOMAIN_PROD + this.premiumUrl) : "";

        String com=",[ ]*";

        if (teaserPage != null) {
            // Progressive Profiling
            String assetTier = String.valueOf(ProgressiveProfilingPremiumPageHelper.getAssetTierFromPremiumUrl (request, this.premiumUrl));
            addSubmittedField("assetTier", assetTier);
        }

        addSubmittedField("gatedUrl", premiumUrlFull );
        addSubmittedField("lang", language );
        addSubmittedField("ownerID", eloquaData.getOwnerTags().split(com));
        addSubmittedField("exportJourney", eloquaData.getCategoryTags().split(com));
        addSubmittedField("exportPain", eloquaData.getSubCategoryTags().split(com));
        addSubmittedField("buyerjourney", eloquaData.getExportStatusTags().split(com));
        addSubmittedField("personaID", eloquaData.getPersonaTags().split(com));
        addSubmittedField("emailAddress", this.inputEmail);

        if ( this.updateDocHistory ){
            addSubmittedField(DOC_ID, this.docID);
        }
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public void setUserLevelAfterAccess( int value)
    {
        this.afterAccessUserLevel = value;
    }

    public int getUserLevelAfterAccess() {
        return this.afterAccessUserLevel;
    }

    public boolean getUpdateDocHistory() {
        return this.updateDocHistory;
    }

    public void setUpdateDocHistory( boolean value ) {
        this.updateDocHistory = value;
    }

    public void setMode(String value) {
        this.mode = value;
    }

    public String getMode() {
        return this.mode;
    }

    public String getParagraphTextTAS() {
        return paragraphTextTAS;
    }

    public void setParagraphTextTAS(String paragraphTextTAS) {
        this.paragraphTextTAS = paragraphTextTAS;
    }

    public Boolean getNewUser() {
        return newUser;
    }

    public void setNewUser(Boolean newUser) {
        this.newUser = newUser;
    }

    public String getCoreCustomer() {
        return coreCustomer;
    }

    public void setCoreCustomer(String coreCustomer) {
        this.coreCustomer = coreCustomer;
    }

    public String getKnowledgeCustomerStage() {
        return knowledgeCustomerStage;
    }

    public void setKnowledgeCustomerStage(String knowledgeCustomerStage) {
        this.knowledgeCustomerStage = knowledgeCustomerStage;
    }
}

package com.edc.edcportal.core.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.edc.edcportal.core.eloqua.pojo.EloquaUserProfileDO;
import com.edc.edcportal.core.eloqua.services.EloquaDAOService;
import com.edc.edcportal.core.exception.EDCEloquaSystemException;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.LanguageUtil;
import com.edc.edcportal.core.helpers.ReplicationStatusHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsDatalayer;
import com.edc.edcportal.core.services.FieldMappingConfigService;

/**
 * @author Monica Castillo
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * This class is the model to get page level data for the analytics datalayer javaScript object.
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Datalayer
{
    private static final Logger log = LoggerFactory.getLogger(Datalayer.class);

    @Inject
    @Source("sling-object")
    private ResourceResolver resolver;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private SlingSettingsService settings;

    @Inject
    private EloquaDAOService eloquaDAOService;

    @Inject
    private FieldMappingConfigService fieldMappingConfigService;

    @Inject
    @Optional
    private Page currentPage;

    private String templateType;
    private String issueDate;
    private String pageLanguage;
    private String pageHost;
    private String siteEnv;
    private String primaryCategory;
    private String subcategory1;
    private String subcategory2;
    private String pageName;
    private String articlePrimaryCategory;
    private Map<String, String> pageTags;
    private String authStatus;
    private String myEdcCustStatus;
    private String externalID;
    private String guid;
    private String productType;
    private String productDesc;

    Map<String, String> formFields = new LinkedHashMap<>();
    Map<String, String> headers = new LinkedHashMap<>();
    Map<String, FormFieldDefintion> formFieldDefinitions;

    @PostConstruct
    public void initModel() throws MalformedURLException {

        if(log.isDebugEnabled())
        {
            log.debug("Datalayer PostConstruct");
        }



        String pagePath = currentPage.getPath();

        // make sure the page is under /content/myedc/
        if(pagePath.startsWith(ConstantsDatalayer.Paths.CONTENT_MYEDC)){
            Template pageTemplate           = currentPage.getTemplate();
            ValueMap pageProps              = currentPage.getProperties();

            URL pageURL                     = new URL(this.request.getRequestURL().toString());
            String languageAbbreviation     = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsDatalayer.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            this.templateType               = pageTemplate.getTitle();
            this.pageLanguage               = ConstantsDatalayer.SupportedLanguages.getLanguageFromAbbreviation(languageAbbreviation).toString();
            this.pageHost                   = pageURL.getHost();
            this.pageTags                   = new HashMap<>();
            this.articlePrimaryCategory     = "";

            pageCategories(pagePath, languageAbbreviation);
            currentRunMode();
            lastPublishDate(pageProps, currentPage);
            setValuesFromTags(currentPage);
            getUserData(request);
            getProductInfo(request);
        } else {
            setAllEmpty();
        }
    }

    /**
     * Store the tags in the HashMap object
     *
     * @param  key  they name o the key to store the value in the HashMap object
     * @param  value  the value to be stored
     * @param  categoryPath  the path of the tag category
     *
     */
    private void fillTagsMap(String key, String value, String categoryPath) {
        value = value.replace(categoryPath, "").replaceAll("/", "|");

        if(this.pageTags.containsKey(key)) {
            if (!this.pageTags.get(key).contains(value)) {
                this.pageTags.put(key, this.pageTags.get(key) + ConstantsDatalayer.DLProperties.DATALAYER_TAG_SEPARATOR + value);
            }
        } else {
            this.pageTags.put(key, value);
        }
    }

    /**
     * Store the tags in the HashMap object
     *
     * @param  value  the value to be stored
     * @param  categoryPath  the path of the tag category
     * @param  list  the List to store the value
     * @param  index  position in the list to store the value
     *
     */
    private void fillTagsList(String value, String categoryPath, List<String> list, int index) {
        value = value.replace(categoryPath, "");

        if(list.size() > index) {
            if (!list.get(index).contains(value)) {
                list.set(index, list.get(index) + ConstantsDatalayer.DLProperties.DATALAYER_TAG_SEPARATOR + value);
            }
        } else {
            list.add(index, value);
        }
    }

    /**
     * Get page tags to set the corresponding datalayer values
     *
     * @param  page  the current page
     *
     */
    private void setValuesFromTags(Page page) {
        Tag[] pageTags = page.getTags();

        if (pageTags != null && pageTags.length > 0) {
            for(Tag tag : pageTags){
                String localTagId = tag.getLocalTagID();
                String key = "";
                String tagCategory = "";
                fillTagsMap(key, localTagId, tagCategory);
            }
        }
    }

    /**
     * Set all values empty for non content pages
     *
     */
    private void setAllEmpty() {
        this.templateType = "";
        this.pageLanguage = "";
        this.pageHost = "";
        this.issueDate = "";
        this.siteEnv = "";
        this.primaryCategory = "";
        this.subcategory1 = "";
        this.subcategory2 = "";
        this.pageName = "";
    }

    /**
     * Get the last published date
     * this.issueDate
     *
     */
    private void lastPublishDate(ValueMap properties, Page page) {
        ReplicationStatus repStatus = ReplicationStatusHelper.getReplicationStatus(page);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar publishDate;

        this.issueDate = "";

        // Get last published date, if it's not set get the first published date if available
        if((repStatus != null) && (repStatus.getLastPublished() != null)) {
            publishDate = repStatus.getLastPublished();
            this.issueDate = sdf.format(publishDate.getTime());
        } else if (properties.get(ConstantsDatalayer.DLProperties.FIRST_PUBLISHED, Calendar.class) != null) {
            publishDate = properties.get(ConstantsDatalayer.DLProperties.FIRST_PUBLISHED, Calendar.class);
            this.issueDate = sdf.format(publishDate.getTime());
        }
    }

    /**
     * Get the name of the current environment
     * this.siteEnv
     * possible values: dev, prod, qa, stage, test
     *
     */
    private void currentRunMode() {
        Set<String> currentRunModes = settings.getRunModes();

        this.siteEnv = "";

        if (currentRunModes.contains(ConstantsDatalayer.DLProperties.DATALAYER_ENV_DEV)) {
            this.siteEnv = ConstantsDatalayer.DLProperties.DATALAYER_ENV_DEV;
        } else if (currentRunModes.contains(ConstantsDatalayer.DLProperties.DATALAYER_ENV_PROD)) {
            this.siteEnv = ConstantsDatalayer.DLProperties.DATALAYER_ENV_PROD;
        } else if (currentRunModes.contains(ConstantsDatalayer.DLProperties.DATALAYER_ENV_QA)) {
            this.siteEnv = ConstantsDatalayer.DLProperties.DATALAYER_ENV_QA;
        } else if (currentRunModes.contains(ConstantsDatalayer.DLProperties.DATALAYER_ENV_STAGE)) {
            this.siteEnv = ConstantsDatalayer.DLProperties.DATALAYER_ENV_STAGE;
        } else if (currentRunModes.contains(ConstantsDatalayer.DLProperties.DATALAYER_ENV_TEST)) {
            this.siteEnv = ConstantsDatalayer.DLProperties.DATALAYER_ENV_TEST;
        }
    }

    /**
     * Parse the page path to get the values for:
     * this.primaryCategory
     * this.subcategory1
     * this.subcategory2
     * this.pageName
     *
     * @param  path  the path of the current page
     * @param  languageAbbreviation the language abbreviation of the current page
     *
     */
    private void pageCategories(String path, String languageAbbreviation) {
        //remove /contet/edc/{lang} from path
        String pathRegExp = "(" + ConstantsDatalayer.Paths.CONTENT + ")?(" + ConstantsDatalayer.Paths.EDCPORTAL + ")?(/" + languageAbbreviation + ")?/?";
        path = path.replaceFirst(pathRegExp, "");

        String[] categoryList = path.split("/");
        Integer pathLength = categoryList.length;

        this.primaryCategory = "";
        this.subcategory1 = "";
        this.subcategory2 = "";
        this.pageName = "";

        switch (pathLength) {
            case 0:
                break;
            case 1:
                this.pageName = currentPage.getName();
                break;
            case 2:
                this.primaryCategory = currentPage.getParent(1).getName();
                break;
            case 3:
                this.primaryCategory = currentPage.getParent(2).getName();
                this.subcategory1 = currentPage.getParent(1).getName();
                break;
            default:
                this.primaryCategory = currentPage.getParent(3).getName();
                this.subcategory1 = currentPage.getParent(2).getName();
                this.subcategory2 = currentPage.getParent(1).getName();
                break;
        }

        if (pathLength > 1) {
            this.pageName =  currentPage.getParent(pathLength - 1).getName();
            for (int i = pathLength - 2; i >= 0; i--) {
                if (i==2){continue;}
                this.pageName += ":" + currentPage.getParent(i).getName();
            }
        }
    }
    
    /**
     * Populates the user status
     *
     */
    private void getUserData(SlingHttpServletRequest request) {
        this.authStatus =  request.getHeader(Constants.Properties.HEADER_FIRST_NAME).isEmpty()? "not-authenticated":"authenticated";
        this.myEdcCustStatus = request.getHeader(Constants.Properties.HEADER_FIRST_NAME).isEmpty()? "non-myEDC user":"myEDC user";
        this.externalID = request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID);

        EloquaUserProfileDO eloquaUserProfileDO = null;
        try {
            eloquaUserProfileDO = this.eloquaDAOService.getUserProfileByExternalId(this.externalID);
        } catch (EDCEloquaSystemException e) {
            log.error(e.toString());
        }
        String guidFieldId = eloquaDAOService.getEloquaConfigService().getGuidFieldId();

        // Task 221435 squid:S2259 "eloquaUserProfileDO" is nullable here.
        if (eloquaUserProfileDO != null) {
            this.guid = eloquaUserProfileDO.getFormFieldsValues().get(guidFieldId).getEloquaValue();
            //BUG 69750 populate from Eloqua and override if on param
            String productTypeFieldId = eloquaDAOService.getEloquaConfigService().getProductFieldId();
            this.productType = eloquaUserProfileDO.getFormFieldsValues().get(productTypeFieldId).getEloquaValue();
            String productDescFieldId = eloquaDAOService.getEloquaConfigService().getProductDescriptionFieldId();
            this.productDesc = eloquaUserProfileDO.getFormFieldsValues().get(productDescFieldId).getEloquaValue();
        }
    }

    private void getProductInfo(SlingHttpServletRequest request) {
        String temp = request.getParameter("productIntent") == null ? "" : request.getParameter("productIntent");

        if (!temp.isEmpty()) {
            this.productType = temp.split("_!_")[0] == null ? "": temp.split("_!_")[0];
            this.productDesc = temp.split("_!_")[1] == null ? "": temp.split("_!_")[1];
        } 
    }

    /**
     * @return the templateType
     */
    public String getTemplateType() {
        return this.templateType;
    }

    /**
     * @return the issueDate
     */
    public String getIssueDate() {
        return this.issueDate;
    }

    /**
     * @return the pageLanguage
     */
    public String getPageLanguage() {
        return this.pageLanguage;
    }

    /**
     * @return the pageHost
     */
    public String getPageHost() {
        return this.pageHost;
    }

    /**
     * @return the primaryCategory
     */
    public String getPrimaryCategory() {
        return this.primaryCategory;
    }

    /**
     * @return the subcategory1
     */
    public String getSubcategory1() {
        return this.subcategory1;
    }

    /**
     * @return the subcategory2
     */
    public String getSubcategory2() {
        return this.subcategory2;
    }

    /**
     * @return the pageName
     */
    public String getPageName() {
        return this.pageName;
    }

    /**
     * @return the siteEnv
     */
    public String getSiteEnv() {
        return this.siteEnv;
    }

    /**
     * @return the pageTags
     */
    public Map<String, String> getPageTags() {
        return this.pageTags;
    }

    public String getAuthStatus() { return this.authStatus; }

    public String getMyEdcCustStatus() { return  this.myEdcCustStatus; }

    public String getExternalID() { return  this.externalID; }

    public String getGuid() { return  this.guid; }
    
    public String getProductType() { return  this.productType; }
    
    public String getProductDesc() { return  this.productDesc; }
}

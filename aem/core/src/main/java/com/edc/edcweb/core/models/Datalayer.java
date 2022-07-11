package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.tagging.Tag;
import com.day.cq.replication.ReplicationStatus;

import com.edc.edcweb.core.helpers.constants.ConstantsDatalayer;
import com.edc.edcweb.core.apim.pojo.SupplierProfileVO;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.ReplicationStatusHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;
import com.edc.edcweb.core.models.inlist.InListSupplierProfile;

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
    private Boolean isArticle = false;
    private String disableAddthis;
    private String articlePrimaryCategory;
    private Map<String, String> pageTags = new HashMap<>();
    private List<String> regionTags = new LinkedList<>();
    private List<String> solutionTags = new LinkedList<>();

    @PostConstruct
    public void initModel() throws Exception {

        if(log.isDebugEnabled())
        {
            log.debug("Datalayer PostConstruct");
        }

        String pagePath = currentPage.getPath();

        // make sure the page is under /content/edc/
        if(pagePath.startsWith(ConstantsDatalayer.Paths.CONTENT_EDC)){
            Template pageTemplate           = currentPage.getTemplate();
            ValueMap pageProps              = currentPage.getProperties();

            URL pageURL                     = new URL(this.request.getRequestURL().toString());
            String languageAbbreviation     = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, ConstantsDatalayer.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            if(pageTemplate != null) {
                this.templateType               = pageTemplate.getTitle();
                this.isArticle                  = checkPageType(pageTemplate.getPageTypePath());
            } else {
                log.debug("Datalayer : Empty template for {}", pagePath); 
            }
            

            this.pageLanguage               = ConstantsDatalayer.SupportedLanguages.getLanguageFromAbbreviation(languageAbbreviation).toString();
            this.pageHost                   = pageURL.getHost();
            this.pageTags                   = new HashMap<>();
            this.regionTags                 = new ArrayList<>();
            this.solutionTags               = new ArrayList<>();
            this.articlePrimaryCategory     = "";

            pageCategories(pagePath, languageAbbreviation);
            currentRunMode();
            lastPublishDate(pageProps, currentPage);
            setValuesFromTags(currentPage);
            disableAddthis(pageProps);

            if (this.isArticle) {
                try {
                    // get the primary tag from articlehero or videohero component
                    this.articlePrimaryCategory = findPrimaryTag(currentPage);
                }
                catch (RepositoryException repositoryException) {
                    this.articlePrimaryCategory = "";
                }
            }

        } else {
            setAllEmpty();
        }
    }

    /**
     * Get the primarytag property from articlehero or videohero component to set the primaryCategory value
     * Assuming that the component path is jcr:content/root/{responsivegrid_id}/articlecontainer/{videohero|articlehero}
     * where we don't know for sure the {responsivegrid_id}
     * @param page
     * @return the primarytag value
     * @throws RepositoryException
     */
    private String findPrimaryTag(Page page) throws RepositoryException {
        String primarytag = "";

        Resource childResource = resolver.getResource(page.getPath() + ConstantsDatalayer.DLProperties.DATALAYER_COMPONENTPATH);
        Node childNode = childResource != null ? childResource.adaptTo(Node.class) : null;

        NodeIterator childrenNodes = childNode != null ? childNode.getNodes() : null;

        if (childrenNodes != null) {
            while(childrenNodes.hasNext()){
                Node next = childrenNodes.nextNode();

                if(next.hasNodes()){
                    NodeIterator nextChildren = next.getNodes(ConstantsDatalayer.DLProperties.DATALAYER_ARTICLENODENAME);

                    while(nextChildren.hasNext()){
                        Node containerChildren = nextChildren.nextNode();

                        if(containerChildren.hasNodes()){
                            NodeIterator bannerChildren = containerChildren.getNodes("articlehero | videohero");

                            if(bannerChildren.hasNext()) {
                                Node bannerNode = bannerChildren.nextNode();

                                if (bannerNode.hasProperty(ConstantsDatalayer.DLProperties.DATALAYER_PRYMARYTAG)) {
                                    primarytag = bannerNode.getProperty(ConstantsDatalayer.DLProperties.DATALAYER_PRYMARYTAG).getValue().toString();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return primarytag.replaceAll("edc:\\w+\\/", "").replaceAll("/","|");
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
     * Prepare tags that have to be stored in different keys, like category1, category2, etc
     *
     * @param  key  they name o the key to store the value in the HashMap object
     * @param  value  the value to be stored
     * @param  categoryPath  the path of the tag category
     *
     */
    private void fillTagsMapByCategory(String key, String value, String categoryPath) {
        value = value.replace(categoryPath, "");
        String[] values = value.split("/");
        for (int i = 0; i< values.length; i++) {
            switch(key) {
                case ConstantsDatalayer.DLProperties.DATALAYER_REGION:
                    fillTagsList(values[i], categoryPath, this.regionTags, i);
                    break;
                case ConstantsDatalayer.DLProperties.DATALAYER_SOLUTION:
                    fillTagsList(values[i], categoryPath, this.solutionTags, i);
                    break;
                default:
                    fillTagsMap(key + (i + 1), values[i], categoryPath);
            }
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
                Boolean hasSubCategories = false;
                Boolean setTags = true;

                if (localTagId != null) {
                    if(localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_REGION)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_REGION;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_REGION;
                        hasSubCategories = true;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_INDUSTRY)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_INDUSTRY;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_INDUSTRY;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_FTAS)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_FTAS;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_FTAS;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_CONTRIBUTORS)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_CONTRIBUTORS;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_CONTRIBUTORS;

                    }  else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_CATEGORY)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_SUBCATEGORY;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_CATEGORY;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_ACCESSTYPE)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_ACCESSTYPE;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_ACCESSTYPE;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_FORMATYPE)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_FORMATYPE;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_FORMATYPE;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_SOLUTION) || localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_SOLUTIONS)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_SOLUTION;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_SOLUTION;
                        hasSubCategories = true;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_EXPORTSTATUS)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_EXPORTSTATUS;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_EXPORTSTATUS;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_PERSONA)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_PERSONA;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_PERSONA;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_BUYERSTAGE)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_BUYERSTAGE;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_BUYERSTAGE;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_OWNERID)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_OWNERID;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_OWNERID;

                    } else if (localTagId.startsWith(ConstantsDatalayer.DLProperties.DATALAYER_TAG_EVENTTYPE)) {
                        key = ConstantsDatalayer.DLProperties.DATALAYER_EVENTTYPE;
                        tagCategory = ConstantsDatalayer.DLProperties.DATALAYER_TAG_EVENTTYPE;

                    } else {
                        setTags = false;
                    }
                } else {
                    setTags = false;
                }

                if (setTags && hasSubCategories) {
                    fillTagsMapByCategory(key, localTagId, tagCategory);
                } else if (setTags && !hasSubCategories) {
                    fillTagsMap(key, localTagId, tagCategory);
                }
            }
        }
    }

    /**
     * Check if the page is tagged as video, blog or article
     *
     * @param  templateTypePath  the path of the template type
     *
     */
    private boolean checkPageType(String templateTypePath) {
        Boolean isArticePage = false;

        if (templateTypePath != null && templateTypePath.equals(ConstantsDatalayer.DLProperties.DATALAYER_TYPE_ARTICLE)) {
            isArticePage = true;
        }

        return isArticePage;
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
        this.isArticle = false;
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

            if (publishDate != null) {
                this.issueDate = sdf.format(publishDate.getTime());
            }
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
        String pathRegExp = "(" + ConstantsDatalayer.Paths.CONTENT + ")?(" + ConstantsDatalayer.Paths.EDC + ")?(/" + languageAbbreviation + ")?/?";
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
                this.pageName += ":" + currentPage.getParent(i).getName();
            }
        }

        String[] pageSelectors = request.getRequestPathInfo().getSelectors();
        if (pageSelectors.length > 1 && pageSelectors[0].equalsIgnoreCase(ConstantsInList.InListProperties.INLIST_SERVICE)) {
            InListSupplierProfile profile = request.adaptTo(InListSupplierProfile.class);
            SupplierProfileVO supplierProfile = profile != null ? profile.getSupplierProfile() : null;

            if (supplierProfile != null) {
                this.pageName += "." + pageSelectors[0] + "." + supplierProfile.getName().toLowerCase();
            }
        }
    }
    
    /**
     * Get value for addthis status via page properties
     * this.disableAddthis
     * @param  page properties value map
     *
     */
    
    private void disableAddthis(ValueMap properties) {
    	if (properties.get("disableaddthis",String.class) != null) {
            this.disableAddthis = properties.get("disableaddthis",String.class);
        } else {
        	this.disableAddthis = "off";
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

    /**
     * @return the regionTags
     */
    public String getRegionTags() {
        return this.regionTags.toString();
    }

    /**
     * @return the solutionTags
     */
    public String getSolutionTags() {
        return this.solutionTags.toString();
    }
    
    public String getDisableAddthis() {
    	return this.disableAddthis;
    }

    /**
     *
     * @return the primary category set on the articlehero or articlehero
     */
    public String getArticlePrimaryCategory() {
        return this.articlePrimaryCategory;
    }
}

package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageToggleUtil;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.search.PageList;
import com.edc.edcweb.core.search.PageSearch;
import com.edc.edcweb.core.search.filters.PageNotIsHideInNavFilter;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        This class is the model class for the Header component used by the EDC
 *        web site.
 *
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, resourceType = "edc/components/structure/header")
public class Header {

    @Inject
    @Source("sling-object")
    protected ResourceResolver resolver;

    @Inject
    protected SlingHttpServletRequest request;

    @Inject
    protected Page currentPage;

    @Inject
    private EDCSystemConfigurationService edcSystemConfiguration;

    protected List<PageList> pages = new ArrayList<>();
    protected List<TextUrlLinks> links;
    protected String logoUrl;
    protected String logoImage;
    protected String logoAltText;
    protected String logoTarget;
    protected String phoneNumber;
    protected String loggedInText;
    protected String loggedOutText;
    protected String loginUrl;
    protected String loginTarget;
    protected String leftTitle;
    protected String leftTitleIn;
    protected List<TextUrlLinks> leftLinks;
    protected String rightTitle;
    protected List<TextUrlLinks> rightLinks;
    protected String menuTarget;
    protected String skipText;
    protected String logoutButton;
    protected String logoutButtonText;
    protected String userStatusServiceUrl;

    protected String manageProfileUrl;
    protected String manageProfileLabel;
    protected String myDashboardUrl;
    protected String myDashboardLabel;
    protected String myAccountMenuTitle;
    protected List<TextUrlLinks> myAccountLinks;
    protected String logoutUrl;
    protected String mSearchLabel;
    protected Boolean showSearch;
    protected String showSearchAccessibility;
    protected String languageToggleAccessibility;
    protected String secondaryNavigationBarAccessibility;
    protected String closeMenuAccessibility;


    /**
     * Populates the Model with values from the applicable ContentPolicy (based on
     * current path language).
     *
     */
    @PostConstruct
    public void initModel() {
        this.userStatusServiceUrl = Constants.Paths.USER_STATUS_SERVICE_URL;
        String startingPage = "";
        Long maxLevels = -1L;
        LanguageToggleUtil langUtil = LanguageToggleUtil.getInstance();
        // ---------------------------------------------------------------------
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            startingPage = properties.get(Constants.Properties.STARTING_PAGE, "");
            maxLevels = properties.get(Constants.Properties.MAX_LEVELS, -1L);
            this.logoUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LOGO_URL, String.class));
            this.logoImage = properties.get(Constants.Properties.LOGO_IMAGE, String.class);
            this.logoAltText = properties.get(Constants.Properties.LOGO_ALT_TEXT, String.class);
            this.logoTarget = properties.get(Constants.Properties.LOGO_TARGET, String.class);
            this.phoneNumber = properties.get(Constants.Properties.PHONE_NUMBER, String.class);
            this.loggedInText = properties.get(Constants.Properties.LOGGEDIN_TEXT, String.class);
            this.loggedOutText = properties.get(Constants.Properties.LOGGEDOUT_TEXT, String.class);
            this.loginUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LOGIN_URL, String.class));
            this.loginTarget = properties.get(Constants.Properties.LOGIN_TARGET, String.class);
            this.leftTitle = properties.get(Constants.Properties.LEFT_TITLE, String.class);
            this.leftTitleIn = properties.get(Constants.Properties.LEFT_TITLE_IN, String.class);
            this.rightTitle = properties.get(Constants.Properties.RIGHT_TITLE, String.class);
            this.menuTarget = properties.get(Constants.Properties.MENU_TARGET, String.class);
            this.skipText = properties.get(Constants.Properties.MENU_SKIP, String.class);
            this.logoutButton = (langUtil.isEnglish(this.currentPage.getPath()) ? Constants.Properties.LOGOUT_BUTTON_EN
                    : Constants.Properties.LOGOUT_BUTTON_FR);
            this.logoutButtonText = (langUtil.isEnglish(this.currentPage.getPath())
                    ? Constants.Properties.LOGOUT_BUTTON_EN_NEW
                    : Constants.Properties.LOGOUT_BUTTON_FR_NEW);
            String[] urls = edcSystemConfiguration.getLogoutUrls();
            if (urls.length > 1) {
                this.logoutUrl = (langUtil.isEnglish(this.currentPage.getPath()) ? urls[0] : urls[1]);
            }
            this.myAccountMenuTitle = properties.get("myaccountmenutitle", String.class);
            this.manageProfileUrl = LinkResolver
                    .addHtmlExtension(langUtil.isEnglish(this.currentPage.getPath()) ? Constants.Myedc.MANAGEPROFILE_EN
                            : Constants.Myedc.MANAGEPROFILE_FR);
            this.manageProfileLabel = (langUtil.isEnglish(this.currentPage.getPath())
                    ? Constants.Myedc.MANAGEPROFILE_LABEL_EN
                    : Constants.Myedc.MANAGEPROFILE_LABEL_FR);
            this.myDashboardUrl = LinkResolver.addHtmlExtension(
                    langUtil.isEnglish(this.currentPage.getPath()) ? Constants.Myedc.MYDASHBOARD_LINK_EN
                            : Constants.Myedc.MYDASHBOARD_LINK_FR);
            this.myDashboardLabel = (langUtil.isEnglish(this.currentPage.getPath()) ? Constants.Myedc.MYACCOUNT_LABEL_EN
                    : Constants.Myedc.MYACCOUNT_LABEL_FR);
            this.mSearchLabel = properties.get(Constants.Properties.MOBILE_SEARCH_LABEL, String.class);
            this.showSearch = properties.get(Constants.Properties.SHOW_SEARCH, Boolean.class);
            this.showSearchAccessibility = properties.get(Constants.Properties.SHOW_SEARCH_ACCESSIBILITY, String.class);
            this.languageToggleAccessibility = properties.get(Constants.Properties.LANGUAGE_TOGGLE_ACCESSIBILITY, String.class);
            this.secondaryNavigationBarAccessibility = properties.get(Constants.Properties.SECONDARY_NAVIGATION_BAR_ACCESSIBILITY, String.class);
            this.closeMenuAccessibility = properties.get(Constants.Properties.CLOSE_MENU_ACCESSIBILITY, String.class);

        }
        // ---------------------------------------------------------------------
        PageSearch pageSearch = new PageSearch(maxLevels, new PageNotIsHideInNavFilter());
        // ---------------------------------------------------------------------
        Resource startingPageResource = this.resolver.getResource(startingPage);
        Page page = null;
        if (startingPageResource != null) {
            page = startingPageResource.adaptTo(Page.class);
        }
        this.pages = pageSearch.searchPages(page);
        // ---------------------------------------------------------------------
        // get the 'links' nodes from the content
        // ---------------------------------------------------------------------
        Resource policyResource = null;
        if (contentPolicy != null) {
            policyResource = contentPolicy.adaptTo(Resource.class);
        }
        // ---------------------------------------------------------------------
        Resource childResourceLinks = null;
        Resource childResourceLeftLinks = null;
        Resource childResourceRightLinks = null;
        if (policyResource != null) {
            childResourceLinks = policyResource.getChild(Constants.Properties.LINKS);
            childResourceLeftLinks = policyResource.getChild(Constants.Properties.LEFT_LINKS);
            childResourceRightLinks = policyResource.getChild(Constants.Properties.RIGHT_LINKS);
        }
        // ---------------------------------------------------------------------
        links = populateLinksModel(childResourceLinks);
        leftLinks = populateLinksModel(childResourceLeftLinks);
        rightLinks = populateLinksModel(childResourceRightLinks);
    }

    /**
     * Get the list of pages in the navigation menu.
     *
     */
    public List<PageList> getPages() {
        return this.pages;
    }

    /**
     * Get the dynamic links as defined in the Header component's author dialog.
     *
     */
    public List<TextUrlLinks> getLinks() {
        return this.links;
    }

    /**
     * Get the URL to be loaded when the logo is clicked.
     *
     */
    public String getLogoUrl() {
        return this.logoUrl;
    }

    /**
     * Get the file reference (asset in the DAM) for the logo image.
     *
     */
    public String getLogoImage() {
        return this.logoImage;
    }

    /**
     * Get the alternate text for the logo image.
     *
     */
    public String getLogoAltText() {
        return this.logoAltText;
    }

    /**
     * Get the logo image target (existing window, new window, etc.).
     *
     */
    public String getLogoTarget() {
        return this.logoTarget;
    }

    /**
     * Get the phone number to be displayed in the Header.
     *
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Get the text to display in the Login link.
     *
     */
    public String getLoggedInText() {
        return this.loggedInText;
    }

    public String getLoggedOutText() {
        return this.loggedOutText;
    }

    /**
     * Get the text of the login button.
     *
     */
    public String getLogoutButtonText() {
        return this.logoutButtonText;
    }

    /**
     * Get the URL of the login page.
     *
     */
    public String getLoginUrl() {
        return this.loginUrl;
    }

    /**
     * Get the login target (existing window, new window, etc.).
     *
     */
    public String getLoginTarget() {
        return this.loginTarget;
    }

    /**
     * Get the title for the left panel of the User menu.
     *
     */
    public String getLeftTitle() {
        return this.leftTitle;
    }

    /**
     * Get the title for the left panel of the User menu logged in.
     *
     */
    public String getLeftTitleIn() {
        return leftTitleIn;
    }

    /**
     * Get the left panel links as defined in the Header component's author dialog.
     *
     */
    public List<TextUrlLinks> getLeftLinks() {
        return this.leftLinks;
    }

    /**
     * Get the title for the right panel of the User menu.
     *
     */
    public String getRightTitle() {
        return this.rightTitle;
    }

    /**
     * Get the right panel links as defined in the Header component's author dialog.
     *
     */
    public List<TextUrlLinks> getRightLinks() {
        return this.rightLinks;
    }

    /**
     * Get the target for all links in the navigation menu (existing window, new
     * window, etc.).
     *
     */
    public String getMenuTarget() {
        return this.menuTarget;
    }

    /**
     * Get the Label for myedc "manage my profile" page.
     *
     */
    public String getManageProfileLabel() {
        return this.manageProfileLabel;
    }

    /**
     * Get the URL for myedc "manage my profile" page.
     *
     */
    public String getManageProfileUrl() {
        return this.manageProfileUrl;
    }

    /**
     * Get the Label for myedc "my dashboard" page.
     *
     */
    public String getMyDashboardLabel() {
        return this.myDashboardLabel;
    }

    /**
     * Get the URL for myedc "my dashboard" page.
     *
     */
    public String getMyDashboardUrl() {
        return this.myDashboardUrl;
    }

    /**
     * Get the Label for myedc "my dashboard" page.
     *
     */
    public String getMyAccountMenuTitle() {
        return this.myAccountMenuTitle;
    }

    /**
     * Get the dynamic links as defined in the Header component's author dialog.
     *
     */
    public List<TextUrlLinks> getMyAccountLinks() {
        return this.myAccountLinks;
    }

    /**
     * For accesibility | Get the text for the action to skip the navigation
     *
     */
    public String getSkipText() {
        return this.skipText;
    }

    /**
     * Get the text for the log out button
     *
     */
    public String getLogoutButton() {
        return this.logoutButton;
    }

    /**
     * Get user status service url for user menu
     *
     */
    public String getUserStatusServiceUrl() {
        return this.userStatusServiceUrl;
    }

    protected List<TextUrlLinks> populateLinksModel(Resource resource) {
        List<TextUrlLinks> listOfLinks = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                TextUrlLinks item = resources.next().adaptTo(TextUrlLinks.class);
                listOfLinks.add(item);
            }
        }
        return listOfLinks;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getMSearchLabel() {
        return mSearchLabel;
    }

    public Boolean getShowSearch() {
        return showSearch;
    }

    public String getShowSearchAccessibility() {
        return this.showSearchAccessibility;
    }

    public String getLanguageToggleAccessibility() {
        return this.languageToggleAccessibility;
    }

    public String getSecondaryNavigationBarAccessibility() {
        return this.secondaryNavigationBarAccessibility;
    }

    public String getCloseMenuAccessibility() {
        return this.closeMenuAccessibility;
    }

}

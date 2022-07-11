package com.edc.edcweb.core.helpers;

import java.util.Iterator;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 *        This class provides utility methods to simplify the use of content
 *        policies within the EDC web site.
 * 
 * 
 */
public class ContentPolicyUtil {

    private static final Logger log = LoggerFactory.getLogger(ContentPolicyUtil.class);

    private ContentPolicyUtil() {

    }

    /**
     * Returns a ContentPolicy object corresponding appropriate for the action being
     * performed.
     * <p>
     * When editing a content policy, this method will return the policy the
     * template author has currently selected for modification.
     * <p>
     * When satisfying page requests, this function will return the content policy
     * that corresponds to the language of the request.
     * <p>
     * If a policy can not be mapped to a requested language, the English content
     * policy is returned by default.
     *
     * @param request     Used to access AEM related objects associated with the
     *                    current request.
     * @param currentPage Used to access information about the page being accessed
     *                    as part of the request.
     * @return ContentPolicy
     */
    public static ContentPolicy resolveLocalizedContentPolicy(SlingHttpServletRequest request, Page currentPage) {

        return resolveLocalizedContentPolicy(request.getResourceResolver(), request.getResource(), currentPage);
    }

    /**
     * Returns a ContentPolicy object corresponding appropriate for the action being
     * performed.
     */
    public static ContentPolicy resolveLocalizedContentPolicy(ResourceResolver resolver, Resource resource,
            Page currentPage) {

        ContentPolicy resolvedPolicy = null;
        if (resolver == null || resource == null || currentPage == null) {
            log.debug("resolveLocalizedContentPolicy : input is null");
            return resolvedPolicy;
        }

        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        ContentPolicy contentPolicy = null;
        if (policyManager != null) {
            contentPolicy = policyManager.getPolicy(resource);
        }
        if (contentPolicy != null) {

            String policyTitle = contentPolicy.getTitle();
            String policyPath = contentPolicy.getPath();
            String pagePath = currentPage.getPath();

            // return the current policy for authors directly editing
            // the content policy

            if (pagePath.startsWith(Constants.Paths.CONFIG, 0)) {
                resolvedPolicy = contentPolicy;
            }

            // return the current policy if the policy language matches the
            // language of the requested page

            String policyLanguage = extractPolicyLanguage(policyTitle);
            String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(pagePath,
                    Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            if (policyLanguage.equalsIgnoreCase(pageLanguage)) {
                resolvedPolicy = contentPolicy;
            }

            if (resolvedPolicy == null) { // try to match the page language to a
                                          // policy, default to English if no
                                          // policy is found
                resolvedPolicy = getDefaultPolicy(resolver, contentPolicy, policyPath, pageLanguage);
            }

        }

        return resolvedPolicy;
    }

    /** getDefaultPolicy Gets the policy found on the template and finds another with the same name but with current language.
     * 
     * @param resolver
     * @param contentPolicy
     * @param policyPath
     * @param pageLanguage
     * @return Page Language policy or the policy found on the template if no match found
     */
    private static ContentPolicy getDefaultPolicy(ResourceResolver resolver, ContentPolicy contentPolicy,
            String policyPath, String pageLanguage) {
        // set the current policy to the one we have so it'll be returned if no other is
        // found
        ContentPolicy resolvedPolicy = contentPolicy;
        // Current Policy Name, set to blank for not match
        String currPolName = "";
        // Get the Name Parts from current policy
        String[] currentTitleParts = getTitleParts(contentPolicy.getTitle());
        if (currentTitleParts.length > 2) {
            currPolName = currentTitleParts[2];
        }
        // ---------------------------------------------------------------------
        String policyRoot = policyPath.substring(0, (contentPolicy.getPath().lastIndexOf('/') + 1));
        Resource policiesResource = resolver.getResource(policyRoot);
        // ---------------------------------------------------------------------
        if (policiesResource != null) {
            Iterator<Resource> policies = policiesResource.listChildren();
            while (policies.hasNext()) {
                ContentPolicy policy = policies.next().adaptTo(ContentPolicy.class);
                if (policy != null) {
                    // get this policy title parts
                    String[] thisPolTitleParts = getTitleParts(policy.getTitle());
                    String thisPolLang = null;
                    String thisPolName = null;
                    if (thisPolTitleParts.length > 2) {
                        thisPolLang = thisPolTitleParts[1];
                        thisPolName = thisPolTitleParts[2];
                    }
                    // if current page language and "policy name" matches (case aware), we shouold
                    // got the corresponding policy
                    if (pageLanguage.equalsIgnoreCase(thisPolLang) && currPolName.equals(thisPolName)) {
                        resolvedPolicy = policy;
                        break;
                    }
                }
            }
        }
        return resolvedPolicy;
    }

    /**
     * Returns a two character String representing the language of a content policy.
     * If no language code is found, an empty String is returned.
     * <p>
     * This method relies upon a set naming convention for policies. Policy names
     * are comprised of three parts, separated by hyphens. EDC - LANGUAGE CODE -
     * COMPONENT NAME
     *
     * @param policyTitle The title of the content policy under consideration.
     * @return policyLanguage A two character string representing the language of
     *         the content policy. The string will be empty if no language code is
     *         found.
     */
    public static String extractPolicyLanguage(String policyTitle) {
        String[] components = getTitleParts(policyTitle);
        String policyLanguage = "";
        // properly formed policy names have three parts separated by hyphens eg:
        // EDC-EN-FOOTER
        // properly formed language codes are two letters long
        if ((components.length == 3) && (components[1].length() == 2)) {
            policyLanguage = components[1].toUpperCase();
        }

        return policyLanguage;
    }

    /** getTitleParts Splits the title in Array and returns it
     * 
     * @param policyTitle
     * @return Array with title parts
     */
    private static String[] getTitleParts(String policyTitle) {

        String[] components = {};
        if (policyTitle != null) {
            components = policyTitle.split("-");
        }
        return components;
    }

    /**
     *  resolvePageLocalizedContentPolicy Gets the Resource Resolver
     * @param request
     * @param currentPage
     * @return
     */
    public static ContentPolicy resolvePageLocalizedContentPolicy(SlingHttpServletRequest request, Page currentPage) {

        ResourceResolver resolver = request.getResourceResolver();
        Resource resourcePage = currentPage.adaptTo(Resource.class);

        return resolveLocalizedContentPolicy(resolver, resourcePage, currentPage);
    }

    /** resolveLocalizedComponentPolicy Apparently not used
     * 
     * @param request
     * @param currentPage
     * @param componentName
     * @return
     * @throws RepositoryException
     */
    public static ContentPolicy resolveLocalizedComponentPolicy(SlingHttpServletRequest request, Page currentPage,
            String componentName) throws RepositoryException {

        ResourceResolver resolver = request.getResourceResolver();
        Resource resourcePage = currentPage.adaptTo(Resource.class);
        Template currentTemplate = currentPage.getTemplate();

        // countryposrating get this under the node of the policies
        Resource policyResource = request.getResourceResolver()
                .getResource(currentTemplate.getPath() + "/policies/jcr:content/root/" + componentName);
        if (policyResource != null) {
            ValueMap valueMapPolicy = policyResource.getValueMap();
            String policyID = valueMapPolicy.get("cq:policy", String.class);

            Resource policyContent = request.getResourceResolver()
                    .getResource("/conf/edc/settings/wcm/policies/" + policyID);
            if (policyContent != null) {
                ValueMap valueMapPolicyContent = policyContent.getValueMap();
                String policyTitle = valueMapPolicyContent.get("jcr:title", String.class); // get the name of the
                                                                                           // policy...
            }
        }

        return resolveLocalizedContentPolicy(resolver, resourcePage, currentPage);
    }

}
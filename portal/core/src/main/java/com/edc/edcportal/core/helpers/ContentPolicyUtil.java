package com.edc.edcportal.core.helpers;

import java.util.Iterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 * <p>
 * This class provides utility methods to simplify the use of content policies within the EDC Portal web site.
 */
public class ContentPolicyUtil {

    private static final Logger log = LoggerFactory.getLogger(ContentPolicyUtil.class);

    private ContentPolicyUtil() {
    }

    /**
     * Returns a ContentPolicy object corresponding appropriate for the action being performed.
     * <p>
     * When editing a content policy, this method will return the policy the template author has currently selected for modification.
     * <p>
     * When satisfying page requests, this function will return the content policy that corresponds to the language of the request.
     * <p>
     * If a policy can not be mapped to a requested language, the English content policy is returned by default.
     *
     * @param request     Used to access AEM related objects associated with the current request.
     * @param currentPage Used to access information about the page being accessed as part of the request.
     * @return ContentPolicy
     */
    public static ContentPolicy resolveLocalizedContentPolicy(SlingHttpServletRequest request, Page currentPage) {
        return resolveLocalizedContentPolicy(request.getResourceResolver(), request.getResource(), currentPage);
    }

    /**
     * Returns a ContentPolicy object corresponding appropriate for the action being performed.
     */
    public static ContentPolicy resolveLocalizedContentPolicy(ResourceResolver resolver, Resource resource, Page currentPage) {
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
            String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

            if (policyLanguage.equalsIgnoreCase(pageLanguage)) {
                resolvedPolicy = contentPolicy;
            }

            // try to match the page language to a policy,
            // default to English if no policy is found
            if (resolvedPolicy == null) {
                resolvedPolicy = getDefaultPolicy(resolver, contentPolicy, policyPath, pageLanguage);
            }
        }

        return resolvedPolicy;
    }

    private static ContentPolicy getDefaultPolicy(ResourceResolver resolver, ContentPolicy contentPolicy, String policyPath, String pageLanguage) {
        ContentPolicy resolvedPolicy = null;
        String policyLanguage = "";
        //---------------------------------------------------------------------
        String policyRoot = policyPath.substring(0, (contentPolicy.getPath().lastIndexOf('/') + 1));
        Resource policiesResource = resolver.getResource(policyRoot);
        //---------------------------------------------------------------------
        if (policiesResource != null) {
            Iterator<Resource> policies = policiesResource.listChildren();

            while (policies.hasNext()) {
                ContentPolicy policy = policies.next().adaptTo(ContentPolicy.class);

                if (policy != null) {
                    policyLanguage = extractPolicyLanguage(policy.getTitle());
                }

                if (policyLanguage.equalsIgnoreCase(pageLanguage)) {
                    resolvedPolicy = policy;
                } else {
                    if (resolvedPolicy == null && policyLanguage.equalsIgnoreCase(Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation())) {
                        resolvedPolicy = policy;
                    }
                }
            }
        }

        return resolvedPolicy;
    }

    /**
     * Returns a two character String representing the language of a content policy.  If no language code is found, an empty String is returned.
     * <p>
     * This method relies upon a set naming convention for policies.  Policy names are comprised of three parts, separated by hyphens.  EDCPORTAL - LANGUAGE CODE - COMPONENT NAME
     *
     * @param policyTitle The title of the content policy under consideration.
     * @return policyLanguage A two character string representing the language of the content policy.  The string will be empty if no language code is found.
     */
    public static String extractPolicyLanguage(String policyTitle) {
        String policyLanguage = "";
        String[] components = {};

        if (policyTitle != null) {
            components = policyTitle.split("-");
        }

        // properly formed policy names have three parts separated by hyphens eg: EDCPORTAL-EN-FOOTER
        // properly formed language codes are two letters long
        if ((components.length == 3) && (components[1].length() == 2)) {
            policyLanguage = components[1].toUpperCase();
        }

        return policyLanguage;
    }

    public static ContentPolicy resolvePageLocalizedContentPolicy(SlingHttpServletRequest request, Page currentPage) {
        ResourceResolver resolver = request.getResourceResolver();
        Resource resourcePage = currentPage.adaptTo(Resource.class);

        return resolveLocalizedContentPolicy(resolver, resourcePage, currentPage);
    }
}
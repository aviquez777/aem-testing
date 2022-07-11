/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.edc.edcweb.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.ServletResponseHelper;
import com.edc.edcweb.core.serviceImpl.EdcPageListItemImpl;
import com.edc.edcweb.core.serviceImpl.EdcSearchImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.selectors=" + EdcSearchResultServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=cq/Page",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET"
        }
)
public class EdcSearchResultServlet extends SlingSafeMethodsServlet {
    
    private static final long serialVersionUID = -3522134777775086197L;

    protected static final String DEFAULT_SELECTOR = "edcsearchresults";
    protected static final String PARAM_FULLTEXT = "fulltext";

    private static final String PARAM_RESULTS_OFFSET = "resultsOffset";
    private static final String PREDICATE_FULLTEXT = "fulltext";
    private static final String PREDICATE_TYPE = "type";
    private static final String PREDICATE_PATH = "path";
    private static final String NN_STRUCTURE = "structure";

    // Filtering predicates and query exceptions
    private static final String STRING_TRUE = "true";
    private static final String CONTENT_BASE = Constants.Paths.CONTENT_EDC + "/";
    private static final String QG = "_group.";
    private static final String[] EXCLUDED_PATHS = Constants.ArrayValues.EXCLUDED_PATHS.toArray();
    private static final String[] EXCLUDE_PROPS = Constants.ArrayValues.EXCLUDE_PROPS.toArray();

    private static final Logger logger = LoggerFactory.getLogger(EdcSearchResultServlet.class);

    @Reference
    @Inject
    private QueryBuilder queryBuilder;

    @Reference
    @Inject
    private LanguageManager languageManager;

    @Reference
    @Inject
    private LiveRelationshipManager relationshipManager;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        Page currentPage = getCurrentPage(request);
        if (currentPage != null) {
            Resource searchResource = getSearchContentResource(request, currentPage);
            List<ListItem> results = getResults(request, searchResource, currentPage);
            String jsonstring = writeJson(results);
            ServletResponseHelper.writeResponse(response, jsonstring);
        }
    }

    private Page getCurrentPage(SlingHttpServletRequest request) {
        Page currentPage = null;
        Resource currentResource = request.getResource();
        ResourceResolver resourceResolver = currentResource.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            currentPage = pageManager.getContainingPage(currentResource.getPath());
        }
        return currentPage;
    }

    private String  writeJson(List<ListItem> results) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonstring = null;
        try {
            jsonstring = mapper.writeValueAsString(results);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return jsonstring;
    }

    private Resource getSearchContentResource(SlingHttpServletRequest request, Page currentPage) {
        Resource searchContentResource = null;
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        Resource resource = request.getResource();
        String relativeContentResource = requestPathInfo.getSuffix();
        if (StringUtils.startsWith(relativeContentResource, "/")) {
            relativeContentResource = StringUtils.substring(relativeContentResource, 1);
        }
        if (StringUtils.isNotEmpty(relativeContentResource)) {
            searchContentResource = resource.getChild(relativeContentResource);
            if (searchContentResource == null) {
                PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
                if (pageManager != null) {
                    Template template = currentPage.getTemplate();
                    if (template != null) {
                        Resource templateResource = request.getResourceResolver().getResource(template.getPath());
                        if (templateResource != null) {
                            searchContentResource = templateResource.getChild(NN_STRUCTURE + "/" + relativeContentResource);
                        }
                    }
                }
            }
        }
        logger.debug("EdcSearchResultServlet searchContentResource {} ", searchContentResource);
        return searchContentResource;
    }


    private List<ListItem> getResults(SlingHttpServletRequest request, Resource searchResource, Page currentPage) {
        int searchTermMinimumLength = EdcSearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT;
        int resultsSize = EdcSearchImpl.PROP_RESULTS_SIZE_DEFAULT;
        String pageLang = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        String searchRootPagePath = null;
        Resource excludedPathsMultifield = null;
        if (searchResource != null) {
            ValueMap valueMap = searchResource.getValueMap();
            ValueMap contentPolicyMap = null;
            ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request.getResourceResolver(), searchResource, currentPage);
            if (contentPolicy != null) {
                // Remove this line if log grows too much
                logger.debug("EdcSearchResultServlet using policy title {} path {}:", contentPolicy.getTitle(), contentPolicy.getPath());
                contentPolicyMap = contentPolicy.getProperties();
                searchTermMinimumLength = valueMap.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, contentPolicyMap.get(Search
                        .PN_SEARCH_TERM_MINIMUM_LENGTH, EdcSearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT));
                resultsSize = valueMap.get(Search.PN_RESULTS_SIZE, contentPolicyMap.get(Search.PN_RESULTS_SIZE,
                        EdcSearchImpl.PROP_RESULTS_SIZE_DEFAULT));
                String searchRoot = valueMap.get(Search.PN_SEARCH_ROOT, contentPolicyMap.get(Search.PN_SEARCH_ROOT, EdcSearchImpl.PROP_SEARCH_ROOT_DEFAULT));
                searchRootPagePath = getSearchRootPagePath(searchRoot, currentPage); 
                Resource contentPolicyResource = contentPolicy.adaptTo(Resource.class);
                if (contentPolicyResource != null) {
                    excludedPathsMultifield = contentPolicyResource.getChild("excludedpaths");
                    // Remove this line if log grows too much
                    logger.debug("EdcSearchResultServlet excludedPathsMultifield: {}", excludedPathsMultifield);
                } else {
                    logger.debug("EdcSearchResultServlet excludedPathsMultifield not found");
                }
            } else {
                logger.debug("EdcSearchResultServlet no policy on template: {} resource : {}", currentPage.getTemplate(), searchResource.getName());
            }
        } else {
            String languageRoot = languageManager.getLanguageRoot(currentPage.getContentResource()).getPath();
            searchRootPagePath = getSearchRootPagePath(languageRoot, currentPage);
        }
        if (StringUtils.isEmpty(searchRootPagePath)) {
            searchRootPagePath = currentPage.getPath();
        }
        List<ListItem> results = new ArrayList<>();
        String fulltext = request.getParameter(PARAM_FULLTEXT);
        if (fulltext == null || fulltext.length() < searchTermMinimumLength) {
            return results;
        }
        long resultsOffset = 0;
        if (request.getParameter(PARAM_RESULTS_OFFSET) != null) {
            resultsOffset = Long.parseLong(request.getParameter(PARAM_RESULTS_OFFSET));
        }
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put(PREDICATE_FULLTEXT, fulltext + "~");
        predicatesMap.put(PREDICATE_PATH, searchRootPagePath);
        predicatesMap.put(PREDICATE_TYPE, NameConstants.NT_PAGE);

        // Exclude pages under paths
        for (int i = 0; i < EXCLUDED_PATHS.length; i++) {
            predicatesMap.put("0" + QG + i + QG + "p.not", STRING_TRUE);
            predicatesMap.put("0" + QG + i + QG + "path", CONTENT_BASE + pageLang + "/" + EXCLUDED_PATHS[i]);
            predicatesMap.put("0" + QG + i + QG + "path.self", STRING_TRUE);  
            // Exclude pages under authored excluded paths      
            if (i + 1 == EXCLUDED_PATHS.length && excludedPathsMultifield != null) {
                Iterator<Resource> excludedPaths = excludedPathsMultifield.listChildren();
                int y = i + 1;
                while (excludedPaths.hasNext()) {
                    Resource excludedPathRs = excludedPaths.next();
                    ValueMap excludedPathVm = excludedPathRs.getValueMap();
                    String excludedPath = excludedPathVm.get("excludepath", String.class);
                    predicatesMap.put("0" + QG + y + QG + "p.not", STRING_TRUE);
                    predicatesMap.put("0" + QG + y + QG + "path", excludedPath);
                    predicatesMap.put("0" + QG + y + QG + "path.self", STRING_TRUE);
                    y++;
                }
            }
        }

        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
        // Remove this line if log grows too much
        logger.debug("EdcSearchResultServlet predicates {}:", predicates);
        if (resultsSize != 0) {
            query.setHitsPerPage(resultsSize);
        }
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();

        List<Hit> hits = searchResult.getHits();
        if (hits != null) {
            for (Hit hit : hits) {
                try {
                    Resource hitRes = hit.getResource();
                    Page page = getPage(hitRes);
                    if (page != null) {
                        ValueMap pageProps = page.getProperties();
                        boolean showInSearch = pageProps.get("showinsearch", false);
                        boolean excludeFromSearch = excludeFromSearch(pageProps);
                        // Add to results if not excluded, or 'Show in search results' is checked
                        if (!excludeFromSearch || (excludeFromSearch && showInSearch)) {
                            results.add(new EdcPageListItemImpl(request, page));
                        }
                    }
                } catch (RepositoryException e) {
                    logger.error("Unable to retrieve search results for query.", e);
                }
            }
        }
        logger.debug("EdcSearchResultServlet getResults {}", results);
        return results;
    }

    private String getSearchRootPagePath(String searchRoot, Page currentPage) {
        String searchRootPagePath = null;
        PageManager pageManager = currentPage.getPageManager();
        if (StringUtils.isNotEmpty(searchRoot) && pageManager != null) {
            Page rootPage = pageManager.getPage(searchRoot);
            if (rootPage != null) {
                Page searchRootLanguageRoot = languageManager.getLanguageRoot(rootPage.getContentResource());
                Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
                RangeIterator liveCopiesIterator = null;
                try {
                    liveCopiesIterator = relationshipManager.getLiveRelationships(currentPage.adaptTo(Resource.class), null, null);
                } catch (WCMException e) {
                    // ignore it
                }
                if (searchRootLanguageRoot != null && currentPageLanguageRoot != null && !searchRootLanguageRoot.equals
                        (currentPageLanguageRoot)) {
                    // check if there's a language copy of the search root
                    Page languageCopySearchRoot = pageManager.getPage(ResourceUtil.normalize(currentPageLanguageRoot.getPath() + "/" +
                            getRelativePath(searchRootLanguageRoot, rootPage)));
                    if (languageCopySearchRoot != null) {
                        rootPage = languageCopySearchRoot;
                    }
                } else if (liveCopiesIterator != null) {
                    while (liveCopiesIterator.hasNext()) {
                        LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                        if (currentPage.getPath().startsWith(relationship.getTargetPath() + "/")) {
                            Page liveCopySearchRoot = pageManager.getPage(relationship.getTargetPath());
                            if (liveCopySearchRoot != null) {
                                rootPage = liveCopySearchRoot;
                                break;
                            }
                        }
                    }
                }
                searchRootPagePath = rootPage.getPath();
            }
        }
        return searchRootPagePath;
    }

    private String getRelativePath(Page root, Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

    private Page getPage(Resource resource) {
        if (resource != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                return pageManager.getContainingPage(resource);
            }
        }
        return null;
    }

    private boolean excludeFromSearch(ValueMap pageProps) {
        String excludeString;
        boolean exclude = false;
        for (int j = 0; j < EXCLUDE_PROPS.length; j++) {
            if (exclude) {
                return exclude;
            }
            if (EXCLUDE_PROPS[j].equals("substitutepath")) {
                excludeString = pageProps.get(EXCLUDE_PROPS[j], "");
                if (StringUtils.isNotBlank(excludeString)) {
                    exclude = true;
                }
            } else {
                exclude = pageProps.get(EXCLUDE_PROPS[j], false);
            }
        }
        return exclude;
    }
}

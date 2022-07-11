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
package com.edc.edcweb.core.serviceImpl;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Default;
 
 public class EdcPageListItemImpl implements ListItem {
 
     private static final Logger LOGGER = LoggerFactory.getLogger(EdcPageListItemImpl.class);
 
     protected SlingHttpServletRequest request;
     protected Page page;
 
     public EdcPageListItemImpl(SlingHttpServletRequest request, Page page) {
         this.request = request;
         this.page = page;
         Page redirectTarget = getRedirectTarget(page);
         if (redirectTarget != null && !redirectTarget.equals(page)) {
             this.page = redirectTarget;
         }
     }
 
     @Override
     public String getURL() {
         return getURL(request, page);
     }
 
     @Override
     public String getTitle() {
        String title = this.getSearchResults();
        if (StringUtils.isBlank(title)) {
            title = page.getNavigationTitle();
        }
        if (StringUtils.isBlank(title)) {
             title = page.getPageTitle();
        }
        if (StringUtils.isBlank(title)) {
             title = page.getTitle();
        }
        if (StringUtils.isBlank(title)) {
             title = page.getName();
        }
         return title;
     }
 
     @Override
     public String getDescription() {
         return page.getDescription();
     }
 
     @Override
     public Calendar getLastModified() {
         return page.getLastModified();
     }
 
     @Override
     public String getPath() {
         return page.getPath();
     }
 
     @Override
     @JsonIgnore
     public String getName() {
         return page.getName();
     }

     public String getSearchResults(){
        ValueMap pageProps = page.getProperties();
        LOGGER.debug("Search Result Property Cached is: " + pageProps.get("searchresultsfield",""));
        return  pageProps.get("searchresultsfield","");
     }

     private String getURL(SlingHttpServletRequest request, Page page) {
        String vanityURL = page.getVanityUrl();
        String url = StringUtils.isEmpty(vanityURL) ? request.getContextPath() + page.getPath() + ".html" : request.getContextPath() + vanityURL;

        // bug 183643 use alias if French
        String pageLang = LanguageUtil.getLanguageAbbreviationFromPath(page.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        if(Constants.SupportedLanguages.FRENCH.getLanguageAbbreviation().equalsIgnoreCase(pageLang)) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            url = resourceResolver.map(request, url);
        }
         return url;
     }
 
     private Page getRedirectTarget(Page page) {
         Page result = page;
         String redirectTarget;
         PageManager pageManager = page.getPageManager();
         Set<String> redirectCandidates = new LinkedHashSet<>();
         redirectCandidates.add(page.getPath());
         while (result != null && StringUtils.isNotEmpty((redirectTarget = result.getProperties().get(NameConstants.PN_REDIRECT_TARGET, String.class)))) {
             result = pageManager.getPage(redirectTarget);
             if (result != null) {
                 if (!redirectCandidates.add(result.getPath())) {
                     LOGGER.warn("Detected redirect loop for the following pages: {}.", redirectCandidates.toString());
                     break;
                 }
             }
         }
         return result;
     }
 
 }
 
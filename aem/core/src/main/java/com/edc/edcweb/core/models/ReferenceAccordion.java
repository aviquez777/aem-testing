package com.edc.edcweb.core.models;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;

import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;

import org.apache.commons.lang3.StringUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

/**
 * @author ACN
 * @version 1.1.0
 * @since 1.0.0
 * <p>
 * <p>
 * This class is the model class for the ReferenceAccordion component used by the EDC web site.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ReferenceAccordion {

    @Self
    private SlingHttpServletRequest request;

	private String titleText;

    private List<String> pathList = new LinkedList<>();

    @PostConstruct
    public void initModel() {
        // Get current resource
        Resource resource = request.getResource();
        ValueMap compProperties = resource.adaptTo(ValueMap.class);

        // Get referenced page for populating content in the accordion
        String url = compProperties.get(Constants.Properties.REF_ACCORDION_REF_URL, String.class);
        if (StringUtils.isNotBlank(url)) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page refPage = pageManager.getPage(url);

            // Check for PCI template on author environment
            WCMMode mode = WCMMode.fromRequest(request);
            if (!mode.equals(WCMMode.DISABLED)) {         
                String templateTitle = refPage.getTemplate().getTitle();
                if (templateTitle.equalsIgnoreCase(Constants.Properties.PCI_TEMPLATE_TITLE)) {
    
                    // Set accordion content
                    setAccordionContent(refPage);
                }
            } else {                
                setAccordionContent(refPage);
            }
    
        }
    }

    /**
     * Set accordion content from centercolumn Layout Container in referenced page
     */
    private void setAccordionContent(Page refPage) {
        Resource containerRes = ResourceResolverHelper.getResourceByTypeAndNode(refPage, Constants.Paths.RESPONSIVEGRID_RESOURCE_TYPE, Constants.Properties.PCI_TEMPLATE_MAIN_CONTAINER); 
        if (containerRes != null) {
            Iterator<Resource> compResources = containerRes.listChildren(); 
            while (compResources.hasNext())
            {
                Resource component = compResources.next();
                if (component != null) {
                    String resPath = component.getPath();
                    String resType = component.getResourceType(); 
                    switch (resType) {
                        case Constants.Paths.SECTIONTITLE_RESOURCE_TYPE:
                            titleText = component.getValueMap().get(Constants.Properties.PCI_PAGE_TITLE, String.class);
                            break;               
                        case Constants.Paths.REF_ACCORDION_RESOURCE_TYPE:              
                        case Constants.Paths.ACCORDION_RESOURCE_TYPE:
                            return;
                        default:
                            pathList.add(resPath);
                    }
                }
            }   
        }       
    }

    public String getTitle() {
        return titleText;
    }

    public List<String> getPathList() {
        return pathList;
    }
}

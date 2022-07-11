package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Constants.ArticleContentType;
import com.edc.edcweb.core.helpers.LinkResolver;
/**
 * @author Peter Crummey
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * This class is the model to determine if a page is premium content or not.
 * 
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ArticlePage
{
    private static final Logger log = LoggerFactory.getLogger(ArticlePage.class);
    
    @Inject
    private Page page;
    
    @Self
    private SlingHttpServletRequest request;
    
    private ArticlePageHelper articleHelper=new ArticlePageHelper();
    
    private String breadCrumbEWLabel;
    private String breadCrumbEWLink;
    private String breadCrumbTILink;
    private String breadCrumbTILabel;
    
    
    @PostConstruct
    public void initModel()
    {
        this.articleHelper.getPropertiesFromPage(this.request, this.page);
        this.populateBreadCrumbFromPolicy();
        
    }
    
    public void populateBreadCrumbFromPolicy()
    {
        log.debug("populateBreadCrumbFromPolicy ");
        ContentPolicy contentPolicy = ContentPolicyUtil.resolvePageLocalizedContentPolicy(this.request, this.page);
        if (contentPolicy != null) {
            
            ValueMap properties = contentPolicy.getProperties();
            this.breadCrumbTILabel = properties.get(Constants.Properties.PAGEPOLICY_TILABEL, String.class);
            this.breadCrumbTILink = properties.get(Constants.Properties.PAGEPOLICY_TILINK, String.class);
            this.breadCrumbEWLabel = properties.get(Constants.Properties.PAGEPOLICY_EWLABEL, String.class);
            this.breadCrumbEWLink = properties.get(Constants.Properties.PAGEPOLICY_EWLINK, String.class);
            
            this.breadCrumbTILink = LinkResolver.addHtmlExtension(breadCrumbTILink);
            this.breadCrumbEWLink = LinkResolver.addHtmlExtension(breadCrumbEWLink); 
            
            log.debug(" BreadCrumbTILabel {}", this.breadCrumbTILabel );
            log.debug(" BreadCrumbTILink {}", this.breadCrumbTILink  );
            log.debug(" BreadCrumbEWLabel {}", this.breadCrumbEWLabel  );
            log.debug(" BreadCrumbEWLink {}", this.breadCrumbEWLink  );
                 
        }
        
    }
    

    public boolean isPremium()
    {
        return this.articleHelper.isPremium();
    }

    public String getContentTypeClassName()
    {
        return this.articleHelper.getContentTypeClassName();
    }

    public String getContentTypeTitle()
    {
        return this.articleHelper.getContentTypeTitle();
    }
    
    public String getBreadCrumbLabel()
    {
        String label="";
        List<ArticleContentType> arrayList = new ArrayList<>();
        arrayList.add(Constants.ArticleContentType.BLOG);
        arrayList.add(Constants.ArticleContentType.WEEKLYCOMMENTARY);
                
        
        if ( TagHelper.isPageTagged(this.page, arrayList) )
        {
            label = this.breadCrumbEWLabel;
        }
        else
        {
            label = this.breadCrumbTILabel;
        }
        
        log.debug("getBreadCrumbLabel {}", label );
        return label;
    }
    
    public String getBreadCrumbLink()
    {
        String link="";
        List<ArticleContentType> arrayList = new ArrayList<>();
        arrayList.add(Constants.ArticleContentType.BLOG);
        
     
        if ( TagHelper.isPageTagged(this.page, arrayList) )
        {
            link = this.breadCrumbEWLink;
        }
        else
        {
            link = this.breadCrumbTILink;    
        }
        
        
        log.debug("getBreadCrumbLink {} ",link);
        return link;
    }
}
package com.edc.edcweb.core.components;

import com.day.cq.wcm.api.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.models.AuthorBiov2;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleAuthorsBody {

    @ScriptVariable
    private ResourceResolver resolver;

    @ScriptVariable
    private Page currentPage;

    @Inject
    @ChildResource
    @Default(values="Written by")
    private String compTitle;

    private List<AuthorBiov2> biographies;

    @PostConstruct
    protected void initModel() {
        this.biographies = new ArrayList<>();
        Resource authorDetailsComponent = ResourceResolverHelper.getResourceByType(this.currentPage, Constants.Properties.AUTHORS_DETAILS_RESOURCE_TYPE);
        if (authorDetailsComponent != null) {
            Iterator<Resource> itr = authorDetailsComponent.listChildren();
            while(itr.hasNext()) {
                Resource authorsRsrc = itr.next();
                Iterator<Resource> authorItr = authorsRsrc.listChildren();
                while(authorItr.hasNext()) {
                    ValueMap vm = authorItr.next().getValueMap();
                    String authorPath = vm.get(Constants.Properties.AUTHOR_BIO_PATH, String.class);
                    Resource authorResource = resolver.getResource(authorPath + Constants.Properties.AUTHOR_PATH_SUFFIX);
                    if(authorResource != null) {
                        AuthorBiov2 author = authorResource.adaptTo(AuthorBiov2.class);
                        author.setAuthorPagePath(authorPath);
                        biographies.add(author);
                    }
                }
            }
        }
    }

    public List<AuthorBiov2> getBiographies() {
        return this.biographies;
    }

    public void setCompTitle(String compTitle) {
        this.compTitle = compTitle;
    }

    public String getCompTitle() {
        return this.compTitle;
    }
}

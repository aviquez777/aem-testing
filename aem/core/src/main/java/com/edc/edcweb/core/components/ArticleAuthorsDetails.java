package com.edc.edcweb.core.components;

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

import com.edc.edcweb.core.models.AuthorBiov2;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleAuthorsDetails {

    @ScriptVariable
    private ResourceResolver resolver;

    @Inject
    @ChildResource
    private Resource authors;

    @Inject
    @ChildResource
    @Default(values="Author details")
    private String compTitle;

    @Inject 
    @ChildResource
    @Default(values="View all authors")
    private String ctaViewAllAuthorsText;

    @Inject
    @ChildResource
    private String ctaViewAllAuthorsLink;

    @Inject
    @ChildResource
    @Default(values = "3")
    private String numAuthorsRendered;

    private List<AuthorBiov2> biographies;

    @PostConstruct
    protected void initModel() {
        this.biographies = new ArrayList<>();

        if (this.authors != null) {
            Iterator<Resource> itr = this.authors.listChildren();
            while(itr.hasNext()) {
                ValueMap vm = itr.next().getValueMap();
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

    public Integer getBiographiesListSize() {
        return this.biographies.size();
    }

    public List<AuthorBiov2> getBiographies() {
        return this.biographies;
    }

    public void setCompTitle(String compTitle) {
        this.compTitle = compTitle;
    }

    public void setCTAViewAllAuthors (String ctaViewAllAuthorsText) {
        this.ctaViewAllAuthorsText = ctaViewAllAuthorsText;
    }

    public void setCTAViewAllAuthorsLink(String ctaViewAllAuthorsLink) {
        this.ctaViewAllAuthorsLink = ctaViewAllAuthorsLink;
    }

    public void setNumAuthorsRendered(String numAuthorsRendered) {
        this.numAuthorsRendered = numAuthorsRendered;
    }

    public String getCompTitle() {
        return this.compTitle;
    }

    public String getCTAViewAllAuthorsText() {
        return this.ctaViewAllAuthorsText;
    }

    public String getCTAViewAllAuthorsLink() {
        return this.ctaViewAllAuthorsLink;
    }

    public String getNumAuthorsRendered() {
        return this.numAuthorsRendered;
    }

    // Evaluates if the quantity of authors is more
    // than the max stablished.
    public Boolean isAuthorMoreThankMax(){
        return (this.biographies.size() > Integer.valueOf(this.numAuthorsRendered));
    }
}

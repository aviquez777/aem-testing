package com.edc.edcweb.core.models.productCardsBanker;

import javax.inject.Inject;
import javax.inject.Named;
import com.edc.edcweb.core.helpers.LinkResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import java.util.List;

@Model(adaptables = Resource.class)
public class ProductCardsBankerCards {
    @Inject
    @Named("name")
    @Optional
    private String name;

    @Inject
    @Named("headline")
    @Optional
    private String headline;

    @Inject
    @Named("description")
    @Optional
    private String description;

    @Inject
    @Named("leadintext")
    @Optional
    private String leadintext;

    @Inject
    @Named("ctatext")
    @Optional
    private String ctatext;

    @Inject
    @Named("ctalink")
    @Optional
    private String ctalink;

    @Inject
    @Named("ctatarget")
    @Optional
    private String ctatarget;

    private List<String> bulletList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeadintext() {
        return leadintext;
    }

    public void setLeadintext(String leadintext) {
        this.leadintext = leadintext;
    }

    public String getCtatext() {
        return ctatext;
    }

    public void setCtatext(String ctatext) {
        this.ctatext = ctatext;
    }

    public String getCtalink() {
        return LinkResolver.addHtmlExtension(ctalink);
    }

    public void setCtalink(String ctalink) {
        this.ctalink = ctalink;
    }

    public String getCtatarget() {
        return ctatarget;
    }

    public void setCtatarget(String ctatarget) {
        this.ctatarget = ctatarget;
    }

    public List<String> getBullets() {
        return bulletList;
    }

    public void setBullets(List<String> bulletList) {
        this.bulletList = bulletList;
    }
}
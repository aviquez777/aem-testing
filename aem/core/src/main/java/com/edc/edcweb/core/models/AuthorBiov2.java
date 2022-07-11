package com.edc.edcweb.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class AuthorBiov2 extends AuthorBio {

    @Inject
    @Optional
    private String authorpagepath;

    public void setAuthorPagePath(String authorpagepath) {
        this.authorpagepath = authorpagepath;
    }

    public String getAuthorPagePath() {
        return this.authorpagepath;
    }
}

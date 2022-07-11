package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class AuthorBio
{
    @Inject
    @Named("authorname")
    private String authorname;

    @Inject
    @Named("jobtitle")
    @Optional
    private String jobtitle;

    @Inject
    @Named("linkedin")
    @Optional
    private String linkedin;

    @Inject
    @Named("linktarget")
    @Optional
    private String linktarget;

    @Inject
    @Named("fileReference")
    @Optional
    private String fileReference;

    @Inject
    @Named("imgalt")
    @Optional
    private String imgalt;

    // biographical text
    @Inject
    @Named("text")
    @Optional
    private String text;

    @Inject
    @Named("company")
    @Optional
    private String company;

    @Inject
    @Named("phone")
    @Optional
    private String phone;

    @Inject
    @Named("email")
    @Optional
    private String email;

    @Inject
    @Named("biography")
    @Optional
    private String biography;

    @PostConstruct
    public void initModel() {
        //default implementation for model initialization not needed as all members are injected.

    }

    // getters

    public String getAuthorname() {
        return authorname;
    }

    public String getFileReference() {
        return fileReference;
    }

    public String getImgalt() {
        return imgalt;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public String getLinkTarget() {
        return linktarget;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getText() {
        return text;
    }

    public String getCompany() {
        return company;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getBiography() {
        return biography;
    }
}

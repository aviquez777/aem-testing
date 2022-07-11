package com.edc.edcportal.core.appauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * AppAuthUser POJO to use with Jackson databind
 * 
 **/

public class AppAuthUser {
    @JsonProperty("ExternalUserIdentifier")
    private String externalUserIdentifier;

    @JsonProperty("EmailAddress")
    private String emailAddress;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    public String getExternalUserIdentifier() {
        return externalUserIdentifier;
    }

    public void setExternalUserIdentifier(String externalUserIdentifier) {
        this.externalUserIdentifier = externalUserIdentifier;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

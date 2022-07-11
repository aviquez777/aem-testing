package com.edc.edcportal.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcportal.core.helpers.Constants;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *        Model to serve user identity attributes taken out from SAML assertion
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class LoginRequestHeaders {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHeaders.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    private String firstName;

    private String lastName;

    private String emailId;

    private String mobileNumber;

    private String externalId;

    private String createdDateTime;

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {
        this.firstName = request.getHeader(Constants.Properties.HEADER_FIRST_NAME);
        this.lastName = request.getHeader(Constants.Properties.HEADER_LAST_NAME);
        this.emailId = request.getHeader(Constants.Properties.HEADER_EMAIL_ID);
        this.mobileNumber = request.getHeader(Constants.Properties.HEADER_MOBILE_NUMBER);
        this.externalId = request.getHeader(Constants.Properties.HEADER_EXTERNAL_ID);
        this.createdDateTime = request.getHeader(Constants.Properties.HEADER_CREATED_DATE_TIME);

    }

    /**
     * @return the log
     */
    public static Logger getLog() {
        return log;
    }

    /**
     * @return the request
     */
    public SlingHttpServletRequest getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    public SlingHttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @return the mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the createdDateTime
     */
    public String getCreatedDateTime() {
        return createdDateTime;
    }
}
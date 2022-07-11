package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Roberto Ramos
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ContentPolicyUtil
 *
 * This class provides model support for the AEM Contact Info component.  The model is populated from dialog or from English or French content policies.
 *
 * The ContentPolicyUtil class is used to load the correct policy based upon the language of the current request.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class ContactInfo {

    @ScriptVariable
    private ValueMap properties;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private String title;
    private String schedule;
    private String ctatext1;
    private String phonenumber;
    private String submittext;
    private String linktarget;
    private String linktext;
    private String linkurl;
    private String fileReference;
    private String employeename;
    private String employeetitle;

    @PostConstruct
    public void initModel() {
        // For components already integrated, they take the data from properties
        // When a policy is defined and the phone number is not defined those values are overwritten with the value from the policy.
        this.title = properties.get(Constants.Properties.TITLE, String.class);
        this.schedule = properties.get(Constants.Properties.SCHEDULE, String.class);
        this.ctatext1 = properties.get(Constants.Properties.CTATEXT1, String.class);
        this.phonenumber = properties.get(Constants.Properties.PHONE_NUMBER, String.class);
        this.submittext = properties.get(Constants.Properties.SUBMITTEXT, String.class);
        this.linktarget = properties.get(Constants.Properties.LINK_TARGET, String.class);
        this.linktext = properties.get(Constants.Properties.LINK_TEXT, String.class);
        this.linkurl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.LINK_URL, String.class));
        this.fileReference = properties.get(Constants.Properties.FILEREFERENCE, String.class);
        this.employeename = properties.get(Constants.Properties.EMPLOYEENAME, String.class);
        this.employeetitle = properties.get(Constants.Properties.EMPLOYEETITLE, String.class);

        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null && this.phonenumber == null) {
            ValueMap policyProperties = contentPolicy.getProperties();

            this.title = policyProperties.get(Constants.Properties.TITLE, String.class);
            this.schedule = policyProperties.get(Constants.Properties.SCHEDULE, String.class);
            this.ctatext1 = policyProperties.get(Constants.Properties.CTATEXT1, String.class);
            this.phonenumber = policyProperties.get(Constants.Properties.PHONE_NUMBER, String.class);
            this.submittext = policyProperties.get(Constants.Properties.SUBMITTEXT, String.class);
            this.linktarget = policyProperties.get(Constants.Properties.LINK_TARGET, String.class);
            this.linktext = policyProperties.get(Constants.Properties.LINK_TEXT, String.class);
            this.linkurl = LinkResolver.addHtmlExtension(policyProperties.get(Constants.Properties.LINK_URL, String.class));
            this.fileReference = policyProperties.get(Constants.Properties.FILEREFERENCE, String.class);
            this.employeename = policyProperties.get(Constants.Properties.EMPLOYEENAME, String.class);
            this.employeetitle = policyProperties.get(Constants.Properties.EMPLOYEETITLE, String.class);
        }
    }

    public String getTitle() {
        return this.title;
    }

    public String getSchedule() {
        return this.schedule;
    }

    public String getCtatext1() {
        return this.ctatext1;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }

    public String getSubmittext() {
        return this.submittext;
    }

    public String getLinktarget() {
        return this.linktarget;
    }

    public String getLinktext() {
        return this.linktext;
    }

    public String getLinkurl() {
        return this.linkurl;
    }

    public String getFileReference() {
        return this.fileReference;
    }

    public String getEmployeename() {
        return this.employeename;
    }

    public String getEmployeetitle() {
        return this.employeetitle;
    }
}

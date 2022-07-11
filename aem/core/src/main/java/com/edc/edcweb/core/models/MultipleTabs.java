package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.edc.edcweb.core.helpers.constants.ConstantsAccessibility;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.CountryInfoHelper;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 *        Backing Java code for Multiple Tab. Specific position is defined in
 *        property position in country resource under /data/countryinfo. *
 *        Values for position are:
 *        open,closed,restricted,highlyrestricted,limitedinformation Values for
 *        rating are: lowrisk, lowmediumrisk, mediumrisk, mediumhighrisk,
 *        highrisk, noinforisk
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class MultipleTabs {
    private static final Logger log = LoggerFactory.getLogger(CountryInfo.class);

    @ScriptVariable
    private ValueMap properties;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private String countrytag;
    private String tabTitle;
    private String businessEnvironment;
    private String scrollleft;
    private String scrollright;

    @PostConstruct
    public void initModel() {
        log.debug(" Multiple Tabs ");

        try {
            this.countrytag = CountryInfoHelper.getPageRegionTag(currentPage, this.request);
            populateFromPolicy();
            populateFromCountryInfo();
        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }
    }

    private void populateFromPolicy() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            ValueMap myproperties = contentPolicy.getProperties();
            this.tabTitle = myproperties.get(Constants.Properties.MULTIPLETABS_TITLE, String.class);
            this.scrollleft = myproperties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_SCROLLLEFT, String.class);
            this.scrollright = myproperties.get(ConstantsAccessibility.CAProperties.ACCESSIBILITY_SCROLLRIGHT, String.class);
        }
    }

    private void populateFromCountryInfo() {
        String countryID = CountryInfoHelper.fetchCountryID(this.countrytag, this.request);
        this.businessEnvironment = CountryInfoHelper.getBusinessEnvironment(countryID, this.request);

    }

    public String getTabTitle() {
        return this.tabTitle;
    }

    public String getScrollleft() {
        return this.scrollleft;
    }

    public String getScrollright() {
        return this.scrollright;
    }

    public String getBusinessEnvironment() {
        return this.businessEnvironment;
    }
}

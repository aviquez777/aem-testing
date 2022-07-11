package com.edc.edcweb.core.models;

import com.edc.edcweb.core.helpers.Constants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.SignInHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingHelper;
import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class PremiumAccessButtons {
    private static final Logger log = LoggerFactory.getLogger(PremiumAccessButtons.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private ValueMap properties;

    // Button url values
    private String registerLinkUrl;
    private String loginLinkUrl;

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {
        log.debug(" initModel  " );

        String productPath = properties.get("productpath", String.class);

        /**
         * Generate myEDC links for login and register
         */
        if (productPath != null) {
            String docID = ProgressiveProfilingPremiumPageHelper.getDocIDFromPremiumUrl(request, productPath);
            Resource res = request.getResourceResolver().resolve(productPath.replace(Constants.Paths.PREMIUM, ""));
            // Get the product type and description
            String[] prodTypeDesc = ProgressiveProfilingHelper.getProductTypeAndDesc(res, docID);
            String productTypeValue  = prodTypeDesc[0];
            String productDescValue = prodTypeDesc[1];
            // generate links
            this.registerLinkUrl = SignInHelper.buildSignInQueryString(request, productPath, productTypeValue, productDescValue, true, true, 5, false);
            this.loginLinkUrl = SignInHelper.buildSignInQueryString(request, productPath, productTypeValue, productDescValue, false, true, 5, false);
        }
    }

    public String getRegisterLinkUrl() {
        return registerLinkUrl;
    }

    public String getLoginLinkUrl() {
        return loginLinkUrl;
    }
}

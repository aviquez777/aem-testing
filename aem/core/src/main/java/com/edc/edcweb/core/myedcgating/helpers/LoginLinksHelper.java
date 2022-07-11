package com.edc.edcweb.core.myedcgating.helpers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.SignInHelper;

@Model(adaptables = SlingHttpServletRequest.class)
public class LoginLinksHelper {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    @Optional
    private String premiumUrl;

    @Inject
    @Optional
    private String productTypeValue;

    @Inject
    @Optional
    private String productDescValue;

    @Inject
    @Optional
    private String extraQs;

    @Inject
    @Optional
    @Default(values = "false")
    // Parameters are string
    private String noRedirectBack;
    
    
    @Inject
    @Optional
    @Default(values = "false")
    private String productDescFromPremium;

    private String loginUrl;
    private String registerUrl;
    private String delim = Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER;

    @PostConstruct
    public void initModel() {
        // Premium URL might be sent as a property from the htl, if not, figure it out
        // using the logic
        if (StringUtils.isBlank(premiumUrl)) {
            premiumUrl = currentPage.getProperties().get(Constants.Properties.PROGRESSIVEPROFILING_PREMIUMURL,
                    currentPage.getPath());
        }
        // Product Type might be sent as a property from the htl, if not, figure it out
        // using the logic
        if (StringUtils.isBlank(productTypeValue)) {
            productTypeValue = ProductNameAndDescriptionResolver.getProductType(currentPage);
        }
        // Product Description might be sent as a property from the htl, if not, figure
        // it out using the logic
        if (StringUtils.isBlank(productDescValue)) {
            // Get the values from teaser url
            String getDescUrl = currentPage.getPath();
            // Check the productDescFromPremium to use the premium path
            if (Boolean.parseBoolean(productDescFromPremium)) {
                getDescUrl = premiumUrl;
            }
            productDescValue = ProductNameAndDescriptionResolver.getProductDescription(getDescUrl);
        }
        // Parse the String parameter as boolean
        boolean noRedirectBackBol = Boolean.parseBoolean(noRedirectBack);
        // Set the "baked in" variables
        boolean isLogin = false;
        boolean isRegister = true;
        boolean emailHasAccount = true;
        Integer accessLevel = 5;
        // Do login URL
        loginUrl = SignInHelper.buildSignInQueryString(request, premiumUrl, productTypeValue, productDescValue, isLogin,
                emailHasAccount, accessLevel, noRedirectBackBol);
        // Do Register URL
        registerUrl = SignInHelper.buildSignInQueryString(request, premiumUrl, productTypeValue, productDescValue,
                isRegister, emailHasAccount, accessLevel, noRedirectBackBol);
        // clean Extra Query Strings
        cleanExtraQ();
        // Append Action id if Any
        String eventIdProp = Constants.Properties.BC_ACTION_ID;
        String eventIdValue = currentPage.getProperties().get(eventIdProp, String.class);
        if (StringUtils.isNotBlank(eventIdValue)) {
            // Create the Name Pair Value
            String eventQS = Constants.Properties.BC_AID_QS_PARAM.concat(Constants.EQUAL_SIGN).concat(eventIdValue);
            // add event id to Query String
            if (StringUtils.isBlank(extraQs)) {
                extraQs = eventQS;
            } else {
                extraQs = eventQS.concat(delim).concat(extraQs);
            }
        }
        // Set extra Query String if any
        setExtraQ(premiumUrl);
    }

    private void cleanExtraQ() {
        // Make sure we have no "?" on extra queries
        extraQs = StringUtils.remove(extraQs, Constants.QUESTION_MARK);
        // remove any extra &
        extraQs = StringUtils.replace(extraQs, Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_AMPERSAND,
                Constants.Properties.SUB_QUERY_PARAM_ACTION_TYPE_DELIMITER);
    }

    private void setExtraQ(String premiumUrl) {
        String premiumQs = premiumUrl.concat(Constants.HTML_EXTENTION).concat(Constants.QUESTION_MARK);
        // Concatenate any extra QS if needed
        if (StringUtils.isNotBlank(extraQs)) {
            // Add ? in case delim
            if (StringUtils.contains(loginUrl, premiumQs)) {
                extraQs = delim.concat(extraQs);
            } else {
                extraQs = Constants.QUESTION_MARK.concat(extraQs);
            }

            loginUrl = loginUrl.concat(extraQs);
            registerUrl = registerUrl.concat(extraQs);
        }

    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

}

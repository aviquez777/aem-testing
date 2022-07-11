package com.edc.edcweb.core.myedcgating.helpers;

import org.apache.commons.lang3.StringUtils;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.Constants;

public class ProductNameAndDescriptionResolver {

    private ProductNameAndDescriptionResolver() {
        // Sonar Lint
    }

    /**
     * getProductType utility to get the product type. To be enhanced as necessary
     * 
     * @param currentPage Current Page Object
     * @return "Knowledge Product" if under tool folder, "Subscription".
     */
    public static String getProductType(Page currentPage) {
        String productType = Constants.Properties.SUBSCRIPTION;
        String pagePath = currentPage.getPath();
        // If under "tool" or "outil" then is knowledge product
        if (pagePath.contains(Constants.Paths.TOOL) || pagePath.contains(Constants.Paths.TOOL_ALIAS)) {
            productType = Constants.Properties.KNOWLEDGE_PRODUCT;
        }
        return productType;
    }

    /**
     * getProductDescription get the current path,
     * 
     * @param currentPage Current Page Object
     * @return the resulting string, Account Creation if no path
     */
    public static String getProductDescription(String url) {
        String productDesc = Constants.Properties.ACCOUNT_CREATION;
        if (StringUtils.isNotBlank(url)) {
            String pagePath = PagePathsHelper.removeContentEDCLang(url);
            // change all the "/" from path to "_"
            // MyEDC front controller will change those "_" back to colons as required
            // because colons cannot be on a query string
            productDesc = pagePath.replace(Constants.Paths.FORWARD_SLASH, Constants.UNDERSCORE);
        }
        return productDesc;
    }

}

package com.edc.edcweb.core.helpers;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Reference;

import com.edc.edcweb.core.helpers.progressiveprofiling.ProgressiveProfilingPremiumPageHelper;
import com.edc.edcweb.core.service.EloquaService;

/**
 * <h1>Email has Eloqua Account</h1> 
 * hasAccount():
 * Returns true if email is associated with existing account.
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class emailHasEloquaAccount {

    @Self
    private static SlingHttpServletRequest request;

    // Check if email is associated with existing account on MYEDC
    public static Boolean hasAccount(String userEmail) {
        
        BundleContext bundleContext = FrameworkUtil.getBundle(EloquaService.class).getBundleContext();
        ServiceReference<?> serviceRef = bundleContext.getServiceReference(EloquaService.class.getName());
        EloquaService eloquaService = (EloquaService)bundleContext.getService(serviceRef);

        return eloquaService.checkMyEDCAccountExistsByEMailId(userEmail);
    }

    public static Integer getAccessLevel(SlingHttpServletRequest request, String mode, String sneakPeek, String premiumUrl, String assetTier) {
        boolean isSneakPeek = false;
        if(sneakPeek!=null && sneakPeek.equalsIgnoreCase("yes")) {
            isSneakPeek = true;
        }

        int accessLevel = 0;

        if ( mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_MEA)){
            accessLevel = 4;
        }
        else if( mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_REGULAR) ){

            String level = assetTier;
            if(isSneakPeek) {
                //if sneakPeek, we take the user to chapter 1
                accessLevel = 1;
            }
            else if( level!=null ) {
                accessLevel = Integer.parseInt(level);
            }
        } else if(mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EXPORT_HELP_REQUEST)) {
            accessLevel = 5;
        } else if(mode.equalsIgnoreCase(Constants.Properties.PROGRESSIVEPROFILING_MODE_EBOOK_PERSONA)) {
            accessLevel = Integer.parseInt(assetTier);
        } else{
            //This is for regular progressive profiling usage and Export Help Hub mode.
            //get premium page url, accesslevel and docid.
            accessLevel = ProgressiveProfilingPremiumPageHelper.getAssetTierFromPremiumUrl(request,premiumUrl);
        }
        return accessLevel;
    }
}
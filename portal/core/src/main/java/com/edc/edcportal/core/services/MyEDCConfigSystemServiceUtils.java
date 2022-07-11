package com.edc.edcportal.core.services;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class MyEDCConfigSystemServiceUtils {
    private MyEDCConfigSystemServiceUtils() {
        // Sonar lint
    }

    /**
     * Get the MyEDCConfigSystemService service
     * 
     * @return the MyEDCConfigSystemService service
     */
    public static MyEDCConfigSystemService getMyEDCConfigSystemService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(MyEDCConfigSystemService.class).getBundleContext();
        ServiceReference<?> serviceRef = bundleContext.getServiceReference(MyEDCConfigSystemService.class.getName());
        return (MyEDCConfigSystemService) bundleContext.getService(serviceRef);
    }
}

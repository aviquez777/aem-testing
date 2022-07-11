package com.edc.edcportal.core.models;

import java.util.Iterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.edc.edcportal.core.eloqua.services.EloquaDAOUtils;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.CookieHelper;

@Model(adaptables = SlingHttpServletRequest.class)

public class RenewalUtmFields {

    @Self
    private SlingHttpServletRequest request;

    public Iterator<Resource> getUtmFields() {
        return EloquaDAOUtils.getUtmResourceIterator(request);
    }

    public String getTrafficSourceCookie() {
        return CookieHelper.getCookieValue(request, Constants.Properties.TRAFFIC_SOURCE_COOKIE_NAME);
    }
}

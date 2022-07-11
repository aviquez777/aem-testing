package com.edc.edcweb.core.telp.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONObject;

public interface TelpDAOService {
    JSONObject submitForm(SlingHttpServletRequest request);

    JSONObject getFiLov();
}

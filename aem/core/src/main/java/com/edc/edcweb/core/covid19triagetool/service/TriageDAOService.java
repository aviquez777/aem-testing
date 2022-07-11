package com.edc.edcweb.core.covid19triagetool.service;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;


public interface TriageDAOService {
    
    String getFormJson (SlingHttpServletRequest request) throws IOException;

}

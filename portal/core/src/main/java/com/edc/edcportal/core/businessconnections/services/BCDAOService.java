package com.edc.edcportal.core.businessconnections.services;

import org.apache.sling.api.SlingHttpServletRequest;

import com.edc.edcportal.core.businessconnections.pojo.BCTokenResponse;

public interface BCDAOService {
    BCTokenResponse getBCToken(SlingHttpServletRequest request);
}

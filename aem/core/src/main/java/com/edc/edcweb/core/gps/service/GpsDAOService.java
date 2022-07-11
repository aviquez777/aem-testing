package com.edc.edcweb.core.gps.service;

import java.util.List;

import org.apache.sling.api.request.RequestParameter;

import com.edc.edcweb.core.gps.pojo.GpsResponse;

/**
 * Generic GPS Submission. Expects formType and formJson to be valid
 * 
 * @param formType
 * @param formJson
 * @param attchmentList (optional)
 * 
 */
public interface GpsDAOService {
    GpsResponse submitForm(String formType, String formJson, List<RequestParameter> attchmentList, boolean returnConfNum);
}

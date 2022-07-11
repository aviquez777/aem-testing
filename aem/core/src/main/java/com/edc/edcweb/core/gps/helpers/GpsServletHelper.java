package com.edc.edcweb.core.gps.helpers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.gps.dto.SrfDataTransofrmObject;
import com.edc.edcweb.core.gps.dto.TelpV2DataTransofrmObject;
import com.edc.edcweb.core.gps.pojo.GpsFormFields;
import com.edc.edcweb.core.gps.pojo.GpsFormObjects;
import com.edc.edcweb.core.helpers.Request;
import com.edc.edcweb.core.helpers.ResourceResolverHelper;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;
import com.edc.edcweb.core.telpv2.helpers.TelpV2Constants;

/**
 * Helper Class to get the proper GPS File and Multipart FormFields
 *
 */

public class GpsServletHelper {

    private GpsServletHelper() {
        // SonarQube
    }

    /**
     * getFormObjects checks the resource type to figure out the form (more secure
     * than using a form field)
     * @param SlingHttpServletRequest request
     * @param lovApiDAOService to be used for file extension validation
     * @return GpsFormObjects Data to submit to the API: FormType, FormJson,
     *         FormJsonAttachments
     */
    public static GpsFormObjects getFormObjects(SlingHttpServletRequest request, LovApiDAOService lovApiDAOService) {
        GpsFormObjects gpsFormObjects = new GpsFormObjects();
        Resource pageRes = Request.getCurrentPageResource(request, null);
        Page currentPage = pageRes.adaptTo(Page.class);
        // Get the file and form fields
        GpsFormFields gpsFormFields = prepareMultipartFormFields(request);
        /** Find the form resource type, repeat this for other forms **/
        Resource formRes = ResourceResolverHelper.getResourceByType(currentPage, SrfConstants.SRF_FORM_RESOURCE_TYPE);
        if (formRes != null) {
            gpsFormObjects = SrfDataTransofrmObject.prepareFormObjects(request, gpsFormFields, lovApiDAOService);
        }
        formRes = ResourceResolverHelper.getResourceByType(currentPage, TelpV2Constants.TELPV2_FORM_RESOURCE_TYPE);
        if (formRes != null) {
            gpsFormObjects = TelpV2DataTransofrmObject.prepareFormObjects(request);
        }
        return gpsFormObjects;
    }

    /**
     * prepareMultipartFormFields populate GpsFormFields When form is a Multipart.
     *
     * Retrieve the file contents from the Sling API,
     * SlingHttpServletRequest.getRequestParameterMap method, which returns a
     * RequestParamterMap. Iterate over the parameters to retrieve the file and any
     * parameters passed into the request.
     * 
     * @param SlingHttpServletRequest request
     * @return GpsFormFields collection of the formFields and fileFields
     */
    private static GpsFormFields prepareMultipartFormFields(SlingHttpServletRequest request) {
        GpsFormFields gpsFormFields = new GpsFormFields();
        Map<String, String[]> formFields = new HashMap<>();
        List<RequestParameter> fileFields = new LinkedList<>();
        Map<String, RequestParameter[]> params = request.getRequestParameterMap();
        for (Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
            RequestParameter[] pArr = pairs.getValue();
            RequestParameter param = pArr[0];
            if (param.isFormField()) {
                String key = param.getName();
                if (!formFields.containsKey(key)) {
                    String[] value = request.getParameterValues(key);
                    formFields.put(key, value);
                }
            } else if (StringUtils.isNotBlank(param.getFileName())) {
                fileFields.add(param);
            }
        }
        gpsFormFields.setFormFields(formFields);
        gpsFormFields.setFileFields(fileFields);
        return gpsFormFields;
    }

}

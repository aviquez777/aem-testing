package com.edc.edcweb.core.apim.services.impl;


import java.util.HashMap;
import com.edc.edcweb.core.apim.pojo.APIMTokenDO;
import com.edc.edcweb.core.apim.pojo.inlist.filter.InListSupplierCardsAndFiltersJSONBean;
import com.edc.edcweb.core.apim.pojo.inlist.filter.SupplierCardsAndFiltersDO;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.SupplierProfileDO;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.SupplierProfileJSONBean;
import com.edc.edcweb.core.apim.services.APIMDAOService;
import com.edc.edcweb.core.apim.services.InListDAOService;
import com.edc.edcweb.core.helpers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        service = InListDAOService.class
)
public class InListDAOServiceImpl implements InListDAOService{

	private APIMDAOService apimService;

    private static final Logger log= LoggerFactory.getLogger(InListDAOServiceImpl.class);
    private static final String HTTP_REQUEST_GET = "GET";
    private APIMTokenDO apimToken = null;
    private String apimSubscriptonKey = "";
    private String apimProxyURL = "";
    private String servicePointInListSupplierFilter;
    private String servicePointInListSupplierProfile;


    @Activate
    @Modified
    protected void activate() {
    	apimService = getAPIMService();
        apimProxyURL = apimService.getConfigProxyURL();
        apimSubscriptonKey = apimService.getConfigSubscriptionKey();
        apimToken = apimService.getAccessToken(false);
        servicePointInListSupplierFilter = apimService.getServicePointInListSupplierFilter();
        servicePointInListSupplierProfile = apimService.getServicePointInListSupplierProfile();
    }

    @Override
    public SupplierCardsAndFiltersDO getSupplierFilter(String langAbbr) {

        SupplierCardsAndFiltersDO supplierCardsAndFiltersDO = new SupplierCardsAndFiltersDO();
        String queryResult = callAPIMService(langAbbr,null);
        log.info("Get supplier Filter response = {} ", queryResult);
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            //JSON mapping to Java Bean:
            InListSupplierCardsAndFiltersJSONBean  jsonBean =  mapper.readValue(queryResult, InListSupplierCardsAndFiltersJSONBean.class);
            if(jsonBean.getResult() != null)
            {
            	supplierCardsAndFiltersDO = jsonBean.getResult();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }    

        return supplierCardsAndFiltersDO;
    }

    @Override
    public SupplierProfileDO getSupplierProfile(String langAbbr, String supplierId) {

    	SupplierProfileDO supplierProfileDO = new SupplierProfileDO();
        String queryResult = callAPIMService(langAbbr, supplierId);
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            //JSON mapping to Java Bean:
            SupplierProfileJSONBean  jsonBean =  mapper.readValue(queryResult, SupplierProfileJSONBean.class);
            if(jsonBean.getResult() != null)
            {
            	 supplierProfileDO = jsonBean.getResult();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }   
        
        return supplierProfileDO;
    }

    private String callAPIMService(String langAbbr, String supplierId) {

        String resp = "";

        HTTPRequestParams reqParameters = new HTTPRequestParams();

        //url
        String url = "";
        if(supplierId ==null || supplierId.isEmpty()) {
        	// this is a url for get suppliers filter, like: https://proxyqac.api.edc.ca/api/inlist/v1/suppliersOfflineClient/en
        	url = apimProxyURL + servicePointInListSupplierFilter + langAbbr;
        }else {
        	// this is a url for get suppliers profile, like: https://proxyqac.api.edc.ca/api/inlist/v1/suppliers/100/en
        	url = apimProxyURL + servicePointInListSupplierProfile + supplierId+ "/" + langAbbr;
        }

        reqParameters.setUrl(url);

        //Method
        reqParameters.setRequestType(HTTP_REQUEST_GET);
        // BUG 101248 Get the token again just in case
        apimToken = apimService.getAccessToken(false);

        //Header properties
        HashMap<String, String> reqProps =  new HashMap<>();
        String auth = apimToken.getTokenType() +" "+ apimToken.getAccessToken();
        reqProps.put("Authorization", auth);
        reqProps.put("Content-Type", "application/json");
        reqProps.put("Ocp-Apim-Subscription-Key", apimSubscriptonKey);
        reqParameters.setRequestProperties(reqProps);

        HTTPResponseMessage respMsg =  HTTPRequestUtil.doRESTfulRequest(reqParameters);
        if (respMsg.getResponseCode() == 200) {
            //success
            resp = respMsg.getResponseBody();
        }
        else if(respMsg.getResponseCode() == 401) {
            //unauthorized, refresh the Token and try again
            apimToken = apimService.getAccessToken(true);
            auth = apimToken.getTokenType() +" "+ apimToken.getAccessToken();
            reqProps.put("Authorization", auth);
            reqParameters.setRequestProperties(reqProps);
            respMsg =  HTTPRequestUtil.doRESTfulRequest(reqParameters);
            if(respMsg.getResponseCode() == 200) {
                resp = respMsg.getResponseBody();
            }
            else {
                log.error("Fail to get InList Supplier Filter after refresh token: {}", respMsg.getResponseMessage());
            }

        }
        else {
            //other errors:
            log.error("Fail to get InList Supplier Filter: {}", respMsg.getResponseMessage());
        }

        return resp;
    }

    private APIMDAOService getAPIMService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(APIMDAOService.class).getBundleContext();
        ServiceReference<?> serviceRef = bundleContext.getServiceReference(APIMDAOService.class.getName());
        return (APIMDAOService) bundleContext.getService(serviceRef);
    }

}

package com.edc.edcweb.core.models.inlist;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.apim.ConstantsAPIM;
import com.edc.edcweb.core.apim.pojo.SupplierProfileVO;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.Certification;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.IndustriesServed;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.Language;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.MainAddress;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.MarketsServed;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.ModesOfTransportation;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.Service;
import com.edc.edcweb.core.apim.pojo.inlist.supplier.SupplierProfileDO;
import com.edc.edcweb.core.apim.services.InListDAOService;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsInList;
import com.edc.edcweb.core.servlets.ProgressiveProfilingFormServlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model to get an individual EHH Question related information
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class InListSupplierProfile {
    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    private static final Logger log = LoggerFactory.getLogger(ProgressiveProfilingFormServlet.class);
    SupplierProfileVO supplierProfile;

    @PostConstruct
    public void initModel() {
        String[] pageSelectors = request.getRequestPathInfo().getSelectors();
        String pagePath = currentPage.getPath();
        String languageAbbreviation = LanguageUtil.getLanguageAbbreviationFromPath(pagePath, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        // Get the question, with ID from page selector
        if (pageSelectors.length > 1 && pageSelectors[0].equalsIgnoreCase(ConstantsInList.InListProperties.INLIST_SERVICE)) {

            InListDAOService inListService  = getInListService();
            String supplierId = pageSelectors[1];
            if(supplierId!=null && !supplierId.isEmpty()) {
            	SupplierProfileDO supplierProfileDO = inListService.getSupplierProfile(languageAbbreviation.toLowerCase(), supplierId);
            	supplierProfile = transformProfileDOtoVO(supplierProfileDO);
            }
        }
    }

    

    //Parse the JSON response for supplier profile
    private SupplierProfileVO transformProfileDOtoVO(SupplierProfileDO supplierProfileDO) {

        SupplierProfileVO profiler = new SupplierProfileVO();
        List<SupplierProfileVO.Address> addresses = new ArrayList<SupplierProfileVO.Address>();
        
        try {


            if (supplierProfileDO != null) {
            	
                // Get Supplier Id
            	if(supplierProfileDO.getSupplierId()!=null) {
            		profiler.setId(supplierProfileDO.getSupplierId().toString());
            	}
            	
                // Get Supplier Name
            	if(supplierProfileDO.getSupplierName() !=null) {
            		profiler.setName(supplierProfileDO.getSupplierName());
            	}
            	
                // Get Supplier English Name
            	if(supplierProfileDO.getSupplierNameEnglish() !=null) {
            		profiler.setEnglishName(supplierProfileDO.getSupplierNameEnglish());
            	}
            	
                // Get Supplier Description
                if (supplierProfileDO.getSupplierDescription() !=null) {
                    profiler.setDescription(supplierProfileDO.getSupplierDescription());
                }

                // Get the response time
                if (supplierProfileDO.getReferalResponseTime() !=null) {
                    profiler.setReferralResponseTime(supplierProfileDO.getReferalResponseTime());
                }                
                

                // Get contact methods
                if(supplierProfileDO.getPreferredContactChannel()!=null) {
                	Map<String, String> contact = new LinkedHashMap<>();
                    String preferredChannel = supplierProfileDO.getPreferredContactChannel().toLowerCase();
                    String email = supplierProfileDO.getEmail();
                    String website = supplierProfileDO.getWebsite();
                    website = transformWebsite(website);
                    String phone = supplierProfileDO.getPhoneNumber();
                    
                    switch (preferredChannel) {
                    case "website":
                    case "site web":	
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_WEBSITE_LOWERCASE, website);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_EMAIL_LOWERCASE, email);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_PHONE, phone);
                        break;
                    case "phone":
                    case "téléphone":
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_PHONE, phone);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_EMAIL_LOWERCASE, email);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_WEBSITE_LOWERCASE, website);
                        break;
                    case "email":
                    case "courriel":	
                    default:
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_EMAIL_LOWERCASE, email);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_WEBSITE_LOWERCASE, website);
                        contact.put(ConstantsAPIM.InListJSONProperties.JSON_PROP_PHONE, phone);
                        break;
                    }

                    profiler.setContact(contact);
                }

                
                // Get the request a quote URL
                if (supplierProfileDO.getRequestQuoteURL() !=null) {
                    profiler.setRequestQuoteURL(transformWebsite(supplierProfileDO.getRequestQuoteURL()));
                }    
                
                // Get the main address (Headquarters)
                MainAddress addressValue = supplierProfileDO.getMainAddress();
                if(addressValue != null) {
                	SupplierProfileVO.Address mainAddress = profiler.new Address();
                    mainAddress.setHeadQuater(true);

                    // Get the City
                    if (addressValue.getCity() != null) {
                        mainAddress.setCity(addressValue.getCity());
                    }

                    // Get the Country
                    if (addressValue.getCountry() != null) {
                        mainAddress.setCountry(addressValue.getCountry());
                    }

                    // Get the Postal Code
                    if (addressValue.getPostalCode() != null) {
                        mainAddress.setPostCode(addressValue.getPostalCode());
                    }
                    
                    // Get the Province
                    if (addressValue.getProvince() != null) {
                        mainAddress.setProvince(addressValue.getProvince());
                    }
                   
                    // Get the Street
                    if (addressValue.getStreetAddress() != null) {
                        mainAddress.setStreet(addressValue.getStreetAddress());
                    }

                    addresses.add(mainAddress);
                }
                
                // Get the other addresses
                List<String> otherAddresses = supplierProfileDO.getOtherAddresses();
                if (otherAddresses !=null) {
                    for(String aAddress: otherAddresses) {
                        SupplierProfileVO.Address address = profiler.new Address();
                        address.setHeadQuater(false);
                        address.setCountry(aAddress);
                        addresses.add(address);
                    }
                }   
                
                profiler.setAddresses(addresses);

                // Get list of Services
                List<Service> services = supplierProfileDO.getServices();
                if(services != null) {
                	List<String> names = new ArrayList<>();
                	for(Service aService: services) {
                		names.add(aService.getName());
                	}
                    profiler.setServices(names.toArray(new String[0]));
                }

                // Get list of Modes of transportation
                List<ModesOfTransportation> modesOfTrans = supplierProfileDO.getModesOfTransportation();
                if(modesOfTrans != null) {
                	List<String> names = new ArrayList<>();
                	for(ModesOfTransportation aMode: modesOfTrans) {
                		names.add(aMode.getName());
                	}
                    profiler.setTransportationModes(names.toArray(new String[0]));
                }

                // Get list of Industries Served
                List<IndustriesServed> industries = supplierProfileDO.getIndustriesServed();
                if(industries != null) {
                	List<String> names = new ArrayList<>();
                	for(IndustriesServed aIndustry: industries) {
                		names.add(aIndustry.getName());
                	}
                    profiler.setIndustries(names.toArray(new String[0]));
                }
                
                // Get list of Markets Served
                List<MarketsServed> markets = supplierProfileDO.getMarketsServed();
                if(markets != null) {
                	List<String> names = new ArrayList<>();
                	for(MarketsServed aMarket: markets) {
                		names.add(aMarket.getName());
                	}
                    profiler.setMarketsServed(names.toArray(new String[0]));
                }

                // Get list of Certifications Served
                List<Certification> certifications = supplierProfileDO.getCertifications();
                if(certifications != null) {
                	List<String> names = new ArrayList<>();
                	for(Certification aCert: certifications) {
                		names.add(aCert.getCertificationName());
                	}
                    profiler.setCertifications(names.toArray(new String[0]));
                }
                
                // Get list of Languages Served
                
                List<Language> languages = supplierProfileDO.getLanguages();
                if(languages != null) {
                	List<String> names = new ArrayList<>();
                	for(Language aLang: languages) {
                		names.add(aLang.getName());
                	}
                    profiler.setLanguagesServed(names.toArray(new String[0]));
                }
                
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return profiler;
    }
    
    /**
     * Return Supplier Profile
     * @return SupplierProfile
     */
    public SupplierProfileVO getSupplierProfile() {
        return supplierProfile;
    }
        
    //Add www or http if url missing them. Avoid AEM link validation
    private String transformWebsite(String website) {

    	String result = website;
    	if(result!=null && !result.isEmpty() && !website.equalsIgnoreCase("null")) {
    		result = result.toLowerCase();
    		if(!result.startsWith("www.")&& !result.startsWith("http://") && !result.startsWith("https://")){
    			 result = "www."+result;
    		    }
		    if(!result.startsWith("http://") && !result.startsWith("https://")){
		    	result = "http://"+result;
		    }
    	}

    	return result;
    }
    
    private InListDAOService getInListService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(InListDAOService.class).getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(InListDAOService.class.getName());
        InListDAOService inListService = (InListDAOService)bundleContext.getService(serviceRef);
        return inListService;
    }
}
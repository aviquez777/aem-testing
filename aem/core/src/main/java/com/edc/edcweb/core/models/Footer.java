package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.LinkResolver;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 * 
 * @see ContentPolicyUtil
 * @see TextUrlLinks
 * 
 * 
 * This class provides model support for the AEM Footer component.  The model is populated from English or French content policies.
 * 
 * The ContentPolicyUtil class is used to load the correct policy based upon the language of the current request.
 * 
 * This model contains numerous lists of links, which uses the TextUrlLinks model.
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Footer {
	@Inject
	private SlingHttpServletRequest request;

	@Inject
	private Page currentPage;
		
// model title fields	
	
	private String leftTitle;
	private String centerTitle;
	private String rightTitle;
	private String contactTitle;
	private String socialTitle;

// model ids fields

	private String leftID= Constants.Properties.LEFT_ID;
	private String centerID = Constants.Properties.CENTER_ID;
	private String rightID = Constants.Properties.RIGHT_ID;
	private String contactID = Constants.Properties.CONTACT_ID;

// model aria controls fields

	private String leftAriaControls = Constants.Properties.LEFT_ARIA_CONTROLS;
	private String centerAriaControls = Constants.Properties.CENTER_ARIA_CONTROLS;
	private String rightAriaControls = Constants.Properties.RIGHT_ARIA_CONTROLS;
	private String contactAriaControls = Constants.Properties.CONTACT_ARIA_CONTROLS;

// footer classes
	private String classes = Constants.Properties.CLASS_HEADER_TITLE;

// footer nav aria-label
	private String ariaLabel;

// contact detail fields	
	
	private String phoneNumber;
	private String phoneLabel;
	
// logos	
	
	private String edcLogoPath;
	private String edcLogoPath2x;
	private String edcLogoPathBlack;
	private String edcLogoPath2xBlack;
	private String edcLogoUrl;
	private String edcLogoTarget;
	private String edcImgAlt;
	
	private String canadaLogoPath;
	private String canadaLogoPath2x;
	private String canadaLogoPathBlack;
	private String canadaLogoPath2xBlack;
	private String canadaLogoUrl;
	private String canadaLogoTarget;
	private String canadaImgAlt;

// address

    private String companyName;
    private String edcAddress;
	
// links
	
	private List<TextUrlLinks> leftLinks = new ArrayList<>();
	private List<TextUrlLinks> centerLinks = new ArrayList<>();
	private List<TextUrlLinks> rightLinks = new ArrayList<>();
	private List<TextUrlLinks> contactLinks = new ArrayList<>();
	private List<TextUrlLinks> socialLinks = new ArrayList<>();
	private List<TextUrlLinks> bottomLinks = new ArrayList<>();		

	
	/**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	@PostConstruct
	public void initModel() {
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
		if (contentPolicy != null) {
			
			ValueMap properties = contentPolicy.getProperties();
			leftTitle = properties.get(Constants.Properties.LEFT_TITLE, String.class);
			centerTitle = properties.get(Constants.Properties.CENTER_TITLE, String.class);
			rightTitle = properties.get(Constants.Properties.RIGHT_TITLE, String.class);
			contactTitle = properties.get(Constants.Properties.CONTACT_TITLE, String.class);
			socialTitle = properties.get(Constants.Properties.SOCIAL_TITLE, String.class);
			ariaLabel = properties.get(Constants.Properties.ARIA_LABEL, String.class);
			phoneNumber = properties.get(Constants.Properties.PHONE_NUMBER, String.class);
			phoneLabel = properties.get(Constants.Properties.PHONE_LABEL, String.class);
	
			edcLogoPath = properties.get(Constants.Properties.EDC_LOGO, String.class);
			edcLogoPath2x = properties.get(Constants.Properties.EDC_LOGO_2X, String.class);
			edcLogoPathBlack = properties.get(Constants.Properties.EDC_LOGO_BLACK, String.class);
			edcLogoPath2xBlack = properties.get(Constants.Properties.EDC_LOGO_2X_BLACK, String.class);
			edcLogoUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.EDC_LOGO_URL, String.class));	
			edcLogoTarget = properties.get(Constants.Properties.EDC_LOGO_TARGET, String.class);
			edcImgAlt = properties.get(Constants.Properties.EDC_ALT_TEXT, String.class);
			
			canadaLogoPath = properties.get(Constants.Properties.CANADA_LOGO, String.class);
			canadaLogoPath2x = properties.get(Constants.Properties.CANADA_LOGO_2X, String.class);
			canadaLogoPathBlack = properties.get(Constants.Properties.CANADA_LOGO_BLACK, String.class);
			canadaLogoPath2xBlack = properties.get(Constants.Properties.CANADA_LOGO_2X_BLACK, String.class);
			canadaLogoUrl = LinkResolver.addHtmlExtension(properties.get(Constants.Properties.CANADA_LOGO_URL, String.class));	
			canadaLogoTarget = properties.get(Constants.Properties.CANADA_LOGO_TARGET, String.class);
			canadaImgAlt = properties.get(Constants.Properties.CANADA_ALT_TEXT, String.class);
			companyName = properties.get(Constants.Properties.COMPANY_NAME, String.class);
			edcAddress = properties.get(Constants.Properties.EDC_ADDRESS, String.class);
			
			Resource resource = contentPolicy.adaptTo(Resource.class);
			if(resource != null)
			{
				leftLinks = populateLinksModel ( resource.getChild(Constants.Properties.LEFT_LINKS) );
				centerLinks = populateLinksModel ( resource.getChild(Constants.Properties.CENTER_LINKS) );
				rightLinks = populateLinksModel ( resource.getChild(Constants.Properties.RIGHT_LINKS) );
				contactLinks = populateLinksModel ( resource.getChild(Constants.Properties.CONTACT_LINKS) );
				socialLinks = populateLinksModel ( resource.getChild(Constants.Properties.SOCIAL_LINKS) );
				bottomLinks = populateLinksModel ( resource.getChild(Constants.Properties.BOTTOM_LINKS) );
			}
		}		
	}

	
	/**
	 * Returns a List of TextUrlLinks 
	 * <p>
	 * This method translates a series of navigation related nodes defined in the content policy to a collection of
	 * links which can more readily be written out to the page. 
	 *
	 * @param  resource  a JCR resource corresponding with the content policy used to satisfy the current request.
	 * @return List<TextUrlLinks> a list of TextUrlLinks
	 * @see TextUrlLinks
	 */	
	public List<TextUrlLinks> populateLinksModel(Resource resource) {
		List<TextUrlLinks> listOfLinks = new ArrayList<>();
		if (resource != null) {							
			Iterator<Resource> resources = resource.listChildren();
			while (resources.hasNext()) {			
				TextUrlLinks item = resources.next().adaptTo(TextUrlLinks.class);
				listOfLinks.add(item);
			}
		}
		return listOfLinks;
	}

// getter methods	
	
	public String getLeftTitle() {
		return leftTitle;
	}

	public String getCenterTitle() {
		return centerTitle;
	}

	public String getRightTitle() {
		return rightTitle;
	}

	public String getContactTitle() {
		return contactTitle;
	}

	public String getSocialTitle() {
		return socialTitle;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPhoneLabel() {
		return phoneLabel;
	}

	public String getEdcLogoPath() {
		return edcLogoPath;
	}

	public String getEdcLogoPath2x() {
		return edcLogoPath2x;
	}

	public String getCanadaLogoPath() {
		return canadaLogoPath;
	}

	public String getCanadaLogoPath2x() {
		return canadaLogoPath2x;
	}

	public String getEdcLogoPathBlack() {
		return edcLogoPathBlack;
	}

	public String getEdcLogoPath2xBlack() {
		return edcLogoPath2xBlack;
	}

	public String getCanadaLogoPathBlack() {
		return canadaLogoPathBlack;
	}

	public String getCanadaLogoPath2xBlack() {
		return canadaLogoPath2xBlack;
	}

	public List<TextUrlLinks> getLeftLinks() {
		return new ArrayList<>(leftLinks);
	}

	public List<TextUrlLinks> getCenterLinks() {
		return new ArrayList<>(centerLinks);
	}

	public List<TextUrlLinks> getRightLinks() {
		return new ArrayList<>(rightLinks);
	}

	public List<TextUrlLinks> getContactLinks() {
		return new ArrayList<>(contactLinks);
	}

	public List<TextUrlLinks> getBottomLinks() {
		return new ArrayList<>(bottomLinks);
	}

	public List<TextUrlLinks> getSocialLinks() {
		return new ArrayList<>(socialLinks);
	}


	public String getEdcLogoUrl() {
		return edcLogoUrl;
	}


	public String getEdcLogoTarget() {
		return edcLogoTarget;
	}

	public String getEdcImgAlt() {
		return edcImgAlt;
	}

	public String getCanadaLogoUrl() {
		return canadaLogoUrl;
	}


	public String getCanadaLogoTarget() {
		return canadaLogoTarget;
	}

	public String getCanadaImgAlt() {
		return canadaImgAlt;
	}

    public String getCompanyName() {
        return companyName;
    }

    public String getEdcAddress() {
        return edcAddress;
    }

	public String getLeftID() {
		return leftID;
	}

	public String getCenterID() {
		return centerID;
	}

	public String getRightID() {
		return rightID;
	}

	public String getContactID() {
		return contactID;
	}

	public String getLeftAriaControls() {
		return leftAriaControls;
	}

	public String getCenterAriaControls() {
		return centerAriaControls;
	}

	public String getRightAriaControls() {
		return rightAriaControls;
	}

	public String getContactAriaControls() {
		return contactAriaControls;
	}

	public String getClasses() {
		return classes;
	}

	public String getAriaLabel() {
		return ariaLabel;
	}
}

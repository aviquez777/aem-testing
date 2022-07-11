package com.edc.edcweb.core.models;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.CountryInfoHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsGRC;
// import com.edc.edcweb.core.helpers.progressiveProfiling.ProgressiveProfilingHelper;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 *        Backing Java code EDC Position and CCR Rating component. Specific
 *        position is defined in property position in country resource under
 *        /data/countryinfo. * Values for position are:
 *        open,closed,restricted,highlyrestricted,limitedinformation,underreview
 *        Values for rating are: lowrisk, lowmediumrisk, mediumrisk,
 *        mediumhighrisk, highrisk, noinforisk,underreviewrisk
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class CountryInfo {
    private static final Logger log = LoggerFactory.getLogger(CountryInfo.class);

    @ScriptVariable
    private ValueMap properties;

    private String countryCode;
    private boolean hasPolicy = false;    
    private boolean accessGranted = false;
    
    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    private Page currentPage;

    // will have another map for the rating
    private HashMap<String, String> positionMap = new HashMap<>();
    private HashMap<String, String> ratingMap = new HashMap<>();

    /**
     * Populates the model.
     *
     */
    @PostConstruct
    public void initModel() {
        // default implementation for model initialization not needed as all members are
        // injected.
        log.debug(" initModel  ");

        try {
            // - Get MyEdc Login status
            // accessGranted = ProgressiveProfilingHelper.checkMyEdcSession(request);
            
            String[] pageSelectors = request.getRequestPathInfo().getSelectors();

            // - GRC(Global Risks Check) case: Get the country code from selector
            if (pageSelectors.length > 1 && pageSelectors[0].equalsIgnoreCase(ConstantsGRC.GRCProperties.GRC_VERIFICATION)) {
                this.countryCode = pageSelectors[1].trim().toUpperCase();
                
            }
            // - Regular case: get from page tag
            else {
            	String countrytag = CountryInfoHelper.getPageRegionTag(currentPage, this.request);
            	this.countryCode = CountryInfoHelper.fetchCountryID(countrytag, request);
            }
            
            log.debug(" Country Code = " + this.countryCode);
            populateFromPolicy();
        	
        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }
    }

    /**
     * Populates from policy associated to the country info position data.
     *
     */
    private void populateFromPolicy() {
        log.debug("PopulateFromPolicy ");
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
        if (contentPolicy != null) {
            hasPolicy = true;
            log.debug("contentPolicy != null ");
            populateInformationFromPolicy(contentPolicy);
        }

    }

    /**
     * Populates position from policy associated to the country info position data.
     *
     */
    private void populateInformationFromPolicy(ContentPolicy contentPolicy) {

        String position = CountryInfoHelper.getPosition(this.countryCode, request);
        log.debug("populatePositionFromPolicy position:{} ", position);
        // String rating = CountryInfoHelper.getRating(this.countryCode, request);
        // log.debug("populatePositionFromPolicy rating:{} ", rating);

        ValueMap myproperties = contentPolicy.getProperties();
        populatePositionMapFromPolicy(position, myproperties);
        // populateRatingMapFromPolicy(rating, myproperties);
    }

    /**
     * Populates position map from policy associated to the country info position
     * data.
     *
     */
    private void populatePositionMapFromPolicy(String position, ValueMap myproperties) {

        // add the title from the content policy, not specific to current position ...
        String value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_TITLE, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_TITLE, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_SUBTITLE, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_SUBTITLE, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_URL, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_URL, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_URLTEXT, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_URLTEXT, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_HELPTITLE, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_HELPTITLE, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_HELPTEXT, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_HELPTEXT, value);
        value = myproperties.get(Constants.Properties.COUTRYINFO_POSITION_EXITTEXT, String.class);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_EXITTEXT, value);

        // populate the position map from the correct policy value, specific to position
        // (open, closed, restricted)
        // each tab in design dialog has if comonent suffixed with position available
        populateFromAttribute(Constants.Properties.COUTRYINFO_POSITION_IMAGE, position, myproperties, positionMap);
        populateFromAttribute(Constants.Properties.COUTRYINFO_POSITION_TABLET, position, myproperties, positionMap);
        populateFromAttribute(Constants.Properties.COUTRYINFO_POSITION_ALT, position, myproperties, positionMap);

        populateFromAttribute(Constants.Properties.COUTRYINFO_POSITION_HEADING, position, myproperties, positionMap);
        populateFromAttribute(Constants.Properties.COUTRYINFO_POSITION_TEXT, position, myproperties, positionMap);

        // add the html to the link url
        String linkResolved = positionMap.get(Constants.Properties.COUTRYINFO_POSITION_URL);
        linkResolved = LinkResolver.addHtmlExtension(linkResolved);
        positionMap.put(Constants.Properties.COUTRYINFO_POSITION_URL, linkResolved);
    }

    /**
     * Populates risk rating map from policy associated to the country info rating
     * data.
     *
     */
    // private void populateRatingMapFromPolicy(String rating, ValueMap myproperties) {
    //     String value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_TITLE, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_TITLE, value);
    //     value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_SUBTITLE, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_SUBTITLE, value);
    //     // Description for 'under review' is different with other risk levels
    //     value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_URLTEXT, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_URLTEXT, value);
    //     value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_HELPTITLE, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_HELPTITLE, value);
    //     value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_HELPTEXT, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_HELPTEXT, value);
    //     value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_EXITTEXT, String.class);
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_EXITTEXT, value);

    //     if (!rating.equalsIgnoreCase(Constants.Properties.COUTRYINFO_RATING_UNDERREVIEW)) {
    //         value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_DESCRIPTION, String.class);
    //     } else {
    //         value = myproperties.get(Constants.Properties.COUTRYINFO_RATING_DESCRIPTION_UNDERREVIEW, String.class);
    //     }
    //     ratingMap.put(Constants.Properties.COUTRYINFO_RATING_DESCRIPTION, value);

    //     // populate the risk rating map from the correct policy value, specific to
    //     // rating (low, high, medium)
    //     populateFromAttribute(Constants.Properties.COUTRYINFO_RATING_HEADING, rating, myproperties, ratingMap);
    //     populateFromAttribute(Constants.Properties.COUTRYINFO_RATING_IMAGE, rating, myproperties, ratingMap);
    //     populateFromAttribute(Constants.Properties.COUTRYINFO_RATING_TABLET, rating, myproperties, ratingMap);
    //     populateFromAttribute(Constants.Properties.COUTRYINFO_RATING_ALT, rating, myproperties, ratingMap);
    // }

    /**
     * Populates position map from attribute associated to the specific position.
     *
     */
    // in the design dialog we use textopen, textclosed. closed coming from an
    // internal map
    private void populateFromAttribute(String name, String extension, ValueMap aproperties, Map<String, String> aMap) {
        String text = aproperties.get(name + extension, String.class);
        aMap.put(name, text);
    }

    /**
     *  Check My Edc Login State.
     * 
     *
     */
    public boolean isMyEdcSession() {
        return accessGranted;
    }

    /**
     * This map is used by the component to retrieve all the required values for
     * position section to display.
     * 
     *
     */
    public Map<String, String> getPositionMap() {
        return this.positionMap;
    }

    /**
     * This map is used by the component to retrieve all the required values for
     * Risk rating section to display.
     *
     *
     */
    public Map<String, String> getRatingMap() {
        return this.ratingMap;
    }

    /**
     * Used to know if policy is set.
     */
    public boolean isHasPolicy() {
        return hasPolicy;
    }
}

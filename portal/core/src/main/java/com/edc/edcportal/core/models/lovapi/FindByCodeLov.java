package com.edc.edcportal.core.models.lovapi;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.lovapi.helpers.LovDataObjectTransformationHelper;
import com.edc.edcportal.core.lovapi.helpers.LovHelper;
import com.edc.edcportal.core.lovapi.pojo.LovItem;
import com.edc.edcportal.core.lovapi.pojo.SingleLovDO;
import com.edc.edcportal.core.lovapi.service.LovApiDAOService;

/**
 * 
 * GenericLov Create a Sling model which will receive the lov type as parameter
 * The sling model will call the respective API point as per the parameter The
 * sling model will receive the data from the API and transform it to be
 * consumed by the HTL Modify the HTL mark up to use the new sling model.
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class FindByCodeLov {

    private static final Logger log = LoggerFactory.getLogger(FindByCodeLov.class);

    @Inject
    LovApiDAOService lovApiDAOService;

    @Inject
    private Page currentPage;

    // lov query as parameter from htl for re-usability
    @Inject
    @Optional
    private String lovQuery;

    private LovItem result;

    @PostConstruct
    protected void init() {
        if (lovQuery != null) {
            SingleLovDO singleLovDO = LovHelper.getSingleLovDO(lovQuery, lovApiDAOService);
            // Return a similar object as the one MultiFieldPolicyReader
            String language = currentPage.getLanguage().getLanguage();
            result = LovDataObjectTransformationHelper.singleLovToLovItem(singleLovDO.getResult(), language);
        } else {
            log.error("FindByCodeLov no lov type received");
        }
    }


    public LovItem getResult() {
        return result;
    }

}

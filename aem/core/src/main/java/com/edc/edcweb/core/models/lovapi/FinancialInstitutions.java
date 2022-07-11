package com.edc.edcweb.core.models.lovapi;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.lovapi.LovApiJsonDataBindingUtil;
import com.edc.edcweb.core.lovapi.helpers.LovDataObjectTransformationHelper;
import com.edc.edcweb.core.lovapi.pojo.BankItem;
import com.edc.edcweb.core.lovapi.pojo.FinancialInstitutionsDO;
import com.edc.edcweb.core.lovapi.service.LovApiDAOService;

/**
 * 
 * FinancialInstitutions Create a Sling model which will receive the form type
 * as parameter The sling model will call the respective API point as per the
 * parameter The sling model will receive the data from the API and transform it
 * to be consumed by the HTL Modify the HTL mark up to use the new sling model.
 * 
 * Read the Banks from our authored LOV on the component’s policy for TELP and
 * MMG / BCAP will use the API (logic on htl)
 *
 */

@Model(adaptables = SlingHttpServletRequest.class)
public class FinancialInstitutions {

    private static final Logger log = LoggerFactory.getLogger(FinancialInstitutions.class);

    @Inject
    LovApiDAOService lovApiDAOService;

    @Inject
    private Page currentPage;

    // form type as parameter from htl for re-usability
    @Inject
    @Optional
    private String formType;

    // re-used the variable name as on MultiFieldPolicyReader 
    // to minimize code changes
    private List<BankItem> valueMapList = Collections.emptyList();

    @PostConstruct
    protected void init() {
        if (formType != null) {
            FinancialInstitutionsDO financialInstitutionDO = new FinancialInstitutionsDO();
            String responseString = lovApiDAOService.getFiLov(formType);
            try {
                financialInstitutionDO = LovApiJsonDataBindingUtil.jsonToFinancialInstitutionsDO(responseString);
            } catch (IOException e) {
                // Unable to transform, set the error message to display a message to the user
                financialInstitutionDO.setErrorMessage(responseString);
                log.error("FinancialInstitutions error on jsonToFinancialInstitutionsDO {}", e.getMessage());
            }
            // Return a similar object as the one MultiFieldPolicyReader
            String language = currentPage.getLanguage().getLanguage();
            valueMapList = LovDataObjectTransformationHelper.fiLovToBankLov(financialInstitutionDO.getResult(),
                    language);
        } else {
            log.error("FinancialInstitutions no form type received");
        }
    }

    // same getter as MultiFieldPolicyReader
    public List<BankItem> getValueMapList() {
        return valueMapList;
    }

    public void setValueMapList(List<BankItem> valueMapList) {
        this.valueMapList = valueMapList;
        valueMapList = new ArrayList<>(valueMapList);
        this.valueMapList = Collections.unmodifiableList(valueMapList);  
    }
}

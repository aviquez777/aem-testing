package com.edc.edcweb.core.covid19triagetool.service.impl;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;

import com.edc.edcweb.core.covid19triagetool.TirageDataBindingUtil;
import com.edc.edcweb.core.covid19triagetool.helpers.TriageDataTransformationHelper;
import com.edc.edcweb.core.covid19triagetool.pojo.TriageQestionnaireDO;
import com.edc.edcweb.core.covid19triagetool.service.TriageDAOService;

@Component(immediate = true, service = TriageDAOService.class)
public class TriageDAOServiceImpl implements TriageDAOService {

    @Override
    public String getFormJson(SlingHttpServletRequest request) throws IOException {
        TriageQestionnaireDO triageQestionnaireDO = TriageDataTransformationHelper
                .populateQuestionnaire(request);
        String questionnaireJson = TirageDataBindingUtil.triageQestionnaireDOToJson(triageQestionnaireDO);
        String flowJson = TriageDataTransformationHelper.populateFlowJson(request);
        String solutionsJson = TriageDataTransformationHelper.populateSolutionJson(request);
        String concatenated = questionnaireJson.concat(flowJson).concat(solutionsJson);
        return concatenated.replace("}{", ",");
    }
}

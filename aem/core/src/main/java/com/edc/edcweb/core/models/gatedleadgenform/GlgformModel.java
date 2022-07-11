package com.edc.edcweb.core.models.gatedleadgenform;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import com.edc.edcweb.core.helpers.gatedleadgenform.GlgfConstants;
import com.edc.edcweb.core.models.gatedleadgenform.pojo.GlgfQuestion;
import com.edc.edcweb.core.service.EDCSystemConfigurationService;

@Model(adaptables = SlingHttpServletRequest.class)

public class GlgformModel {

    @Inject
    private Resource resource;

    @Inject
    ResourceResolver resourceResolver;

    List<GlgfQuestion> questionList = new LinkedList<>();

    @Inject
    private EDCSystemConfigurationService edcSystemConfigurationService;

    @PostConstruct
    public void initModel() {
        Resource questionsMultifield = resource.getChild("questions");
        if (questionsMultifield != null) {
            Iterator<Resource> questions = questionsMultifield.listChildren();
            while (questions.hasNext()) {
                Resource mfQuestion = questions.next();
                ValueMap mfQuestionVm = mfQuestion.getValueMap();
                Resource questionToUse = resourceResolver
                        .getResource(mfQuestionVm.get(GlgfConstants.QUESTION_TO_USE, String.class));
                if (questionToUse != null) {
                    ValueMap questionToUseVm = questionToUse.getValueMap();
                    GlgfQuestion cglgfQuestion = new GlgfQuestion();
                    // Properties from the multifieldQuestion Value Map
                    String helperText = mfQuestionVm.get(GlgfConstants.QUESTION_MF_HELPER_TEXT, String.class);
                    cglgfQuestion.setQuestionHelperText(helperText);
                    String questionLabel = mfQuestionVm.get(GlgfConstants.QUESTION_MF_LABEL, String.class);
                    cglgfQuestion.setQuestionLabel(questionLabel);
                    String questionOtherlabel = mfQuestionVm.get(GlgfConstants.QUESTION_MF_OTHER_LABEL, String.class);
                    cglgfQuestion.setQuestionOtherlabel(questionOtherlabel);
                    String questionOtherHelperText = mfQuestionVm.get(GlgfConstants.QUESTION_MF_OTHER_HELPER_TEXT, String.class);
                    cglgfQuestion.setQuestionOtherHelperText(questionOtherHelperText);

                    String questionOverride = mfQuestionVm.get(GlgfConstants.QUESTION_MF_OVERRIDE, String.class);
                    cglgfQuestion.setQuestionOverride(questionOverride);

                    // Properties from the "original original question resource"
                    String questionFielName = questionToUseVm.get(GlgfConstants.QUESTION_FIELD_NAME, String.class);
                    cglgfQuestion.setQuestionFieldName(questionFielName);
                    String questionType = questionToUseVm.get(GlgfConstants.QUESTION_TYPE, String.class);
                    cglgfQuestion.setQuestionType(questionType);

                    // Check for LOV
                    String lovPath = questionToUseVm.get(GlgfConstants.QUESTION_LOV, String.class);
                    if (StringUtils.isBlank(lovPath) && questionType.contains(GlgfConstants.CONTAINS_SELECT)) {
                        Resource lovRes = questionToUse.getChild(GlgfConstants.QUESTION_LOV);
                        if (lovRes != null) {
                            lovPath = lovRes.getPath();
                        }
                    }
                    cglgfQuestion.setQuestionLovPath(lovPath);
                    questionList.add(cglgfQuestion);
                }
            }
        }
    }

    public List<GlgfQuestion> getQuestionList() {
        return questionList;
    }
    
    public String getAddressCompleteCSSUrl() {
        return edcSystemConfigurationService.getAddressCompleteCSSUrl();
    }

    public String getAddressCompleteJSUrl() {
        return edcSystemConfigurationService.getAddressCompleteJSUrl();
    }

    public String getAddressCompleteKey() {
        return edcSystemConfigurationService.getAddressCompleteKey();
    }
    
    public String getAddressCompleteService() {
        return edcSystemConfigurationService.getAddressCompleteService();
    }

}

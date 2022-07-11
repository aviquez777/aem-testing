package com.edc.edcweb.core.models.questionnaire;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsQuestionnaire;

@Model(adaptables = SlingHttpServletRequest.class)
public class Questionnaire {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    // Model
    private String questionnairejson;
    private String approvalvalue;
    private String rejectionvalue;
    private String questiontext;
    private String reviewyouranswers;
    private String endofquestionnaire;
    private String approvedtitle;
    private String rejectedtitle;
    private String approvedmsg;
    private String rejectedmsg;
    private String resetbtntext;
    private String dropdownlabel;
    private String dropdownrequirederrormsg;
    private String dropdownselectonemsg;
    private String loadingtext;
    private String nextbtntext;
    private String submitbtntext;
    private String modalariaopen;
    private String modalariclose;
    private String questionnaireType;
    private String multiconfirmtext;
    private String multiclearalltext;
    private String multisingulartext;
    private String multipluraltext;
    private String rejectedctatext;
    private String rejectedctaurl;

    @PostConstruct
    public void initModel() {
        this.populateModel();
        this.populateFromPolicy();
    }

    private void populateFromPolicy() {
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);

        if (contentPolicy != null) {
            ValueMap policy = contentPolicy.getProperties();

            // Get values from component policy
            this.nextbtntext = policy.get(ConstantsQuestionnaire.NEXT_BTN_TEXT, String.class);
            this.resetbtntext = policy.get(ConstantsQuestionnaire.RESET_BTN_TEXT, String.class);
            this.dropdownlabel = policy.get(ConstantsQuestionnaire.DROPDOWN_LABEL, String.class);
            this.dropdownrequirederrormsg = policy.get(ConstantsQuestionnaire.DD_REQUIRED_ERROR_MSG, String.class);
            this.dropdownselectonemsg = policy.get(ConstantsQuestionnaire.DD_SELECT_ONE_MSG, String.class);
            this.loadingtext = policy.get(ConstantsQuestionnaire.LOADING_TEXT, String.class);
            this.submitbtntext = policy.get(ConstantsQuestionnaire.SUBMIT_BTN_TEXT, String.class);
            this.modalariaopen = policy.get(ConstantsQuestionnaire.MODAL_ARIA_OPEN, String.class);
            this.modalariclose = policy.get(ConstantsQuestionnaire.MODAL_ARIA_CLOSE, String.class);
            this.multiconfirmtext = policy.get(ConstantsQuestionnaire.MULTIPLE_SELECT_CONFIRM, String.class);
            this.multiclearalltext = policy.get(ConstantsQuestionnaire.MULTIPLE_SELECT_CLEAR_ALL, String.class);
            this.multisingulartext = policy.get(ConstantsQuestionnaire.MULTIPLE_SELECT_SINGULAR, String.class);
            this.multipluraltext = policy.get(ConstantsQuestionnaire.MULTIPLE_SELECT_PLURAL, String.class);
        }
    }

    private void populateModel() {
        // Get values from component properties
        this.questionnairejson = properties.get(ConstantsQuestionnaire.QUESTIONNAIRE_JSON, String.class);
        this.approvalvalue = properties.get(ConstantsQuestionnaire.APPROVAL_VALUE, String.class);
        this.rejectionvalue = properties.get(ConstantsQuestionnaire.REJECTION_VALUE, String.class);
        this.questiontext = properties.get(ConstantsQuestionnaire.QUESTION_TEXT, String.class);
        this.reviewyouranswers = properties.get(ConstantsQuestionnaire.REVIEW_ANSWERS, String.class);
        this.endofquestionnaire = properties.get(ConstantsQuestionnaire.END_QUESTIONNAIRE, String.class);
        this.approvedtitle = properties.get(ConstantsQuestionnaire.APPROVED_TITLE, String.class);
        this.rejectedtitle = properties.get(ConstantsQuestionnaire.REJECTED_TITLE, String.class);
        this.approvedmsg = properties.get(ConstantsQuestionnaire.APPROVED_MESSAGE, String.class);
        this.rejectedmsg = properties.get(ConstantsQuestionnaire.REJECTED_MESSAGE, String.class);
        this.questionnaireType = properties.get(ConstantsQuestionnaire.QUESTIONNAIRE_TYPE, String.class);
        this.rejectedctatext = properties.get(ConstantsQuestionnaire.REJECTED_CTA_TEXT, String.class);
        this.rejectedctaurl = properties.get(ConstantsQuestionnaire.REJECTED_CTA_URL, String.class);
    }

    public String getQuestionnairejson() {
        return questionnairejson;
    }

    public String getApprovalvalue() {
        return approvalvalue;
    }

    public String getRejectionvalue() {
        return rejectionvalue;
    }

    public String getQuestiontext() {
        return questiontext;
    }

    public String getReviewyouranswers() {
        return reviewyouranswers;
    }

    public String getEndofquestionnaire() {
        return endofquestionnaire;
    }

    public String getApprovedtitle() {
        return approvedtitle;
    }

    public String getRejectedtitle() {
        return rejectedtitle;
    }

    public String getApprovedmsg() {
        return approvedmsg;
    }

    public String getRejectedmsg() {
        return rejectedmsg;
    }

    public String getResetbtntext() {
        return resetbtntext;
    }

    public String getDropdownlabel() {
        return dropdownlabel;
    }

    public String getDropdownrequirederrormsg() {
        return dropdownrequirederrormsg;
    }

    public String getDropdownselectonemsg() {
        return dropdownselectonemsg;
    }

    public String getLoadingtext() {
        return loadingtext;
    }

    public String getNextbtntext() {
        return nextbtntext;
    }

    public String getSubmitbtntext() {
        return submitbtntext;
    }

    public String getModalariaopen() {
        return modalariaopen;
    }

    public String getModalariclose() {
        return modalariclose;
    }

    public String isDynamic() {
        return ConstantsQuestionnaire.QUESTIONNAIRE_TYPE_DYNAMIC.equals(questionnaireType) ? "true" : "false";
    }

    public String getMulticonfirmtext() {
        return multiconfirmtext;
    }

    public String getMulticlearalltext() {
        return multiclearalltext;
    }

    public String getMultisingulartext() {
        return multisingulartext;
    }

    public String getMultipluraltext() {
        return multipluraltext;
    }

    public String getRejectedctatext() {
        return rejectedctatext;
    }

    public String getRejectedctaurl() {
        return rejectedctaurl;
    }

}

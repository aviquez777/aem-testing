package com.edc.edcweb.core.helpers.progressiveprofiling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.FormFieldsUtil;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.constants.ConstantsForm;
import com.edc.edcweb.core.models.FormFieldOption;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * Use to keep the data related to progressive profiling.
 * keep form data definition as well as form pages headers and subtitles and casl information.
 *
 */
public class ProgressiveProfilingFormData
{
    private static final String DROPDOWNTYPE_EDCDATALIST = "EDCDATALIST";

    private static final int FIELDLEVEL_1 = 1;
    private static final int FIELDLEVEL_2 = 2;
    private static final int FIELDLEVEL_3 = 3;
    private static final int FIELDLEVEL_4 = 4;
    private static final int FIELDLEVEL_5 = 5;


    private Map<String, ProgressiveProfilingFormField> formFieldMap = new HashMap<>();

    //the form data owns the form content which is a list of input
    private List<ProgressiveProfilingFormField> formFields = new ArrayList<>();

    public ProgressiveProfilingFormData() {

        this.formFields = new ArrayList<>();
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_EMAIL, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_EMAIL, "", "", FIELDLEVEL_1, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_FNAME, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_FNAME,"","", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_LNAME, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_LNAME, "","", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TITLE, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_TITLE,"","", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TRADESTATUS, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_TRADESTATUS,DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYNAME, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYNAME, "","", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_MAINPHONE, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_MAINPHONE, "","", FIELDLEVEL_2, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR1, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYADDR1, "","", FIELDLEVEL_3, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYADDR2, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYADDR2,"","", FIELDLEVEL_3, false);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCITY, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYCITY, "","", FIELDLEVEL_3, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPROVINCE,  Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYPROVINCE, DROPDOWNTYPE_EDCDATALIST,Constants.Paths.REGION_CANADA_TAGS, FIELDLEVEL_3, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPOSTAL, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYPOSTAL, "","", FIELDLEVEL_3, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCOUNTRY, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_COMPANYCOUNTRY, DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_3, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_ANNUALSALES, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_ANNUALSALES, DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_4, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_EMPOLYEES, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_EMPOLYEES, DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_4, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_PAINPOINT, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_PAINPOINT, DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_4, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_INDUSTRY, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_INDUSTRY, DROPDOWNTYPE_EDCDATALIST,Constants.Paths.INDUSTRY_TAGS, FIELDLEVEL_5, true);
        buildFormField(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_MARKETSINS, Constants.Properties.PROGRESSIVEPROFILING_ELOQUANAME_MARKETSINS, DROPDOWNTYPE_EDCDATALIST,"", FIELDLEVEL_5, true);

    }
    public void initializeInputFieldsFromPolicyContent(SlingHttpServletRequest request, ContentPolicy contentPolicy) {
        ValueMap properties = contentPolicy.getProperties();

        String langAbbr = getLanguageAbbr(request);

        //fill the content label and help
        Iterator<ProgressiveProfilingFormField> it = this.formFields.iterator();

        while (it.hasNext()){

            ProgressiveProfilingFormField anInputField = it.next();
            String labelname = anInputField.getId() + "Label";
            String label = properties.get(labelname, String.class);
            String helpname = anInputField.getId() + "Help";
            String help = properties.get(helpname, String.class);
            String confirmname = anInputField.getId() + "Confirm";
            String confirm = properties.get(confirmname, String.class);
            String clearname = anInputField.getId() + "Clear";
            String clear = properties.get(clearname, String.class);
            String singularname = anInputField.getId() + "Singular";
            String singular = properties.get(singularname, String.class);
            String pluralname = anInputField.getId() + "Plural";
            String plural = properties.get(pluralname, String.class);
            anInputField.updateDialogValues(label, help, confirm, clear, singular, plural);

            if(anInputField.getInputype().equalsIgnoreCase(DROPDOWNTYPE_EDCDATALIST)) {

                List<FormFieldOption>fieldOptions = new ArrayList<>();

                if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_ANNUALSALES)) {
                    fieldOptions =  FormFieldsUtil.getAnnualSalesList(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_EMPOLYEES)) {
                    fieldOptions =  FormFieldsUtil.getEmployeeList(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_PAINPOINT)) {
                    fieldOptions =  FormFieldsUtil.getPainPointList(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYCOUNTRY)) {
                    fieldOptions =  FormFieldsUtil.getCountriesForAddress(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_MARKETSINS)) {
                    fieldOptions =  FormFieldsUtil.getCountriesForMarketOfInterest(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_COMPANYPROVINCE)) {
                    fieldOptions =  FormFieldsUtil.getProvinceList(request);
                    String helpusstatename = anInputField.getId() + "UsStatesHelp";
                    String helpusstate = properties.get(helpusstatename, String.class);
                    String helpinputpname = anInputField.getId() + "InputHelp";
                    String helpinput = properties.get(helpinputpname, String.class);
                    Map<String, String> usstatesitems = FormFieldsUtil.getUsStatesMap(request);
                    anInputField.addProvinceFields(helpusstate, helpinput, usstatesitems);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_INDUSTRY)) {
                    fieldOptions =  FormFieldsUtil.getIndustryList(request);
                }
                else if(anInputField.getId().equalsIgnoreCase(ConstantsForm.FormProperties.FORM_PROGRESSIVEPROFILEING_TRADESTATUS)) {
                    fieldOptions =  FormFieldsUtil.getTradeStatusList(request);
                }

                Iterator<FormFieldOption> iterator = fieldOptions.iterator();

                while(iterator.hasNext()) {

                    FormFieldOption anOption = iterator.next();
                    //Get display value base on current page language
                    String displayText = langAbbr.equalsIgnoreCase("en")?anOption.getEnName():anOption.getFrName();
                    String eloquaValue = anOption.getValue();

                    anInputField.addSelectField(eloquaValue, displayText);
                }
            }
        }
    }
    private void buildFormField(String attrName, String eloquaName, String type,String selectorId, int level, boolean required) {
        ProgressiveProfilingFormField anInputField = new ProgressiveProfilingFormField(attrName, "", "", "", "", "", "", eloquaName, type, selectorId, level, required);

        this.formFields.add(anInputField);
        this.formFieldMap.put(anInputField.getId() , anInputField);
    }

    public List<ProgressiveProfilingFormField> getContent(){
        return this.formFields;
    }
    public Map<String, ProgressiveProfilingFormField>  getContentMap(){
        return this.formFieldMap;
    }

    private String getLanguageAbbr(SlingHttpServletRequest request) {

        String langAbbr = "";
        String url = request.getRequestURL().toString();
        langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(url, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());

        return langAbbr;
    }
}
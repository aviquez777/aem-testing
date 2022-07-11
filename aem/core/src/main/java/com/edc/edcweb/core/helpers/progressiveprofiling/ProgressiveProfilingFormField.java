package com.edc.edcweb.core.helpers.progressiveprofiling;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * Use to keep the data related to progressive profiling form fields.
 * each input can have a label, help, level, type, selector type.
 * eloquaname is the name of the attribute to post to eloqua.
 * an input could be not required.
 * intputs that are selection habe their selectionContent list fill up.
 *
 */

public class ProgressiveProfilingFormField
{

    private String label;
    private String help;
    private String usstatehelp;
    private String inputhelp;
    private String confirm;
    private String clear;
    private String singular;
    private String plural;
    private int level;
    private boolean required = true;
    private String eloquaName;
    private String id;
    private String inputType; //countryList, tagList or policySelect.

    private String selectorid; //use in conjonction with the input type go fetch the values for the selection... can be a path to the tags.

    private Map<String, String> selectionMap = new LinkedHashMap<>();
    private Map<String, String> statesMap = new LinkedHashMap<>();

    public ProgressiveProfilingFormField(String id, String label, String help, String confirm, String clear, String singular, String plural, String eloquaName, String type, String selectorId, int level, boolean required) {

        this.label = label;
        this.level = level;
        this.help = help;
        this.confirm = confirm;
        this.clear = clear;
        this.singular = singular;
        this.plural = plural;
        this.eloquaName = eloquaName;
        this.id = id;
        this.required = required;
        this.selectorid = selectorId;
        this.inputType = type;
    }

    public void updateDialogValues( String ilabel, String ihelp, String iconfirm, String iclear, String isingular, String iplural){
        this.label = ilabel;
        this.help = ihelp;
        this.confirm = iconfirm;
        this.clear = iclear;
        this.singular = isingular;
        this.plural = iplural;
    }

    public void addProvinceFields(String usstatehelp, String inputhelp, Map<String, String> statesMap) {
        this.usstatehelp = usstatehelp;
        this.inputhelp = inputhelp;
        this.statesMap = statesMap;
    }

    public void  addSelectField( String id, String ilabel){
         this.selectionMap.put(id, ilabel);
    }


    public String getLabel(){
        return this.label;
    }

    public String getHelp(){
        return this.help;
    }

    public String getUsstatehelp() {
        return this.usstatehelp;
    }

    public String getInputhelp() {
        return this.inputhelp;
    }

    public Map<String, String> getStatesMap() {
        return this.statesMap;
    }

    public String getConfirm(){
        return this.confirm;
    }

    public String getClear(){
        return this.clear;
    }

    public String getSingular(){
        return this.singular;
    }

    public String getPlural(){
        return this.plural;
    }

    public int getLevel(){
        return this.level;
    }

    public String getId(){
        return this.id;
    }

    public String getEloquaname(){
        return this.eloquaName;
    }

    public boolean getRequired(){
        return this.required;
    }

    public String getSelectorid(){
        return this.selectorid;
    }

    public String getInputype(){
        return this.inputType;
    }

    public Map<String,String> getSelectMap() {
          return this.selectionMap ;
    }

}

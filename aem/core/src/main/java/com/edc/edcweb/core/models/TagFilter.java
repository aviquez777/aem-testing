package com.edc.edcweb.core.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Bean object for Tag Filter in Tradeinsights Article search.
 *
 */
public class TagFilter {

    private String label = "";
    private String datalayer = "";
    private String value = "";
    // Bug 373842 use TagID
    private String tagId = null;

    //keep original values for checking
    private List<TagFilter> subTags = new ArrayList<>();

    public TagFilter() {
    }

    public TagFilter(String label, String datalayer, String value, List<TagFilter> subTags, String tagId) {
        this.label = label;
        this.datalayer = datalayer;
        this.value = value;
        this.subTags = subTags;
        this.tagId =  tagId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDatalayer() {
        return datalayer;
    }

    public void setDatalayer(String datalayer) {
        this.datalayer = datalayer;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<TagFilter> getSubTags() {
        return subTags;
    }

    public void setSubTags(List<TagFilter> subTags) {
        this.subTags = subTags;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

}

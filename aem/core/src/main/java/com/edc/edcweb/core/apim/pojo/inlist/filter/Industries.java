
package com.edc.edcweb.core.apim.pojo.inlist.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "FilterSectionId",
    "FilterSectionOrderNumber",
    "FilterSectionName",
    "IsFilterMatchAll",
    "ParentFilterSectionId",
    "ParentFilterValueKeyName",
    "FilterValues",
    "ValueKeyName"
})
public class Industries {

    @JsonProperty("FilterSectionId")
    private Integer filterSectionId;
    @JsonProperty("FilterSectionOrderNumber")
    private Integer filterSectionOrderNumber;
    @JsonProperty("FilterSectionName")
    private String filterSectionName;
    @JsonProperty("IsFilterMatchAll")
    private Boolean isFilterMatchAll;
    @JsonProperty("ParentFilterSectionId")
    private Object parentFilterSectionId;
    @JsonProperty("ParentFilterValueKeyName")
    private Object parentFilterValueKeyName;
    @JsonProperty("FilterValues")
    private List<FilterValueInIndustries> filterValues = Collections.emptyList();
    @JsonProperty("ValueKeyName")
    private String valueKeyName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("FilterSectionId")
    public Integer getFilterSectionId() {
        return filterSectionId;
    }

    @JsonProperty("FilterSectionId")
    public void setFilterSectionId(Integer filterSectionId) {
        this.filterSectionId = filterSectionId;
    }

    @JsonProperty("FilterSectionOrderNumber")
    public Integer getFilterSectionOrderNumber() {
        return filterSectionOrderNumber;
    }

    @JsonProperty("FilterSectionOrderNumber")
    public void setFilterSectionOrderNumber(Integer filterSectionOrderNumber) {
        this.filterSectionOrderNumber = filterSectionOrderNumber;
    }

    @JsonProperty("FilterSectionName")
    public String getFilterSectionName() {
        return filterSectionName;
    }

    @JsonProperty("FilterSectionName")
    public void setFilterSectionName(String filterSectionName) {
        this.filterSectionName = filterSectionName;
    }

    @JsonProperty("IsFilterMatchAll")
    public Boolean getIsFilterMatchAll() {
        return isFilterMatchAll;
    }

    @JsonProperty("IsFilterMatchAll")
    public void setIsFilterMatchAll(Boolean isFilterMatchAll) {
        this.isFilterMatchAll = isFilterMatchAll;
    }

    @JsonProperty("ParentFilterSectionId")
    public Object getParentFilterSectionId() {
        return parentFilterSectionId;
    }

    @JsonProperty("ParentFilterSectionId")
    public void setParentFilterSectionId(Object parentFilterSectionId) {
        this.parentFilterSectionId = parentFilterSectionId;
    }

    @JsonProperty("ParentFilterValueKeyName")
    public Object getParentFilterValueKeyName() {
        return parentFilterValueKeyName;
    }

    @JsonProperty("ParentFilterValueKeyName")
    public void setParentFilterValueKeyName(Object parentFilterValueKeyName) {
        this.parentFilterValueKeyName = parentFilterValueKeyName;
    }

    @JsonProperty("FilterValues")
    public List<FilterValueInIndustries> getFilterValues() {
        return filterValues;
    }

    @JsonProperty("FilterValues")
    public void setFilterValues(List<FilterValueInIndustries> filterValues) {
        filterValues = new ArrayList<>(filterValues);
        this.filterValues = Collections.unmodifiableList(filterValues);
    }

    @JsonProperty("ValueKeyName")
    public String getValueKeyName() {
        return valueKeyName;
    }

    @JsonProperty("ValueKeyName")
    public void setValueKeyName(String valueKeyName) {
        this.valueKeyName = valueKeyName;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

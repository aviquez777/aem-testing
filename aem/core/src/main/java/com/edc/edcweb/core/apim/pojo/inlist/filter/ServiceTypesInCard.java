
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
    "Values"
})
public class ServiceTypesInCard {

    @JsonProperty("FilterSectionId")
    private Integer filterSectionId;
    @JsonProperty("Values")
    private List<Integer> values = Collections.emptyList();
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

    @JsonProperty("Values")
    public List<Integer> getValues() {
        return values;
    }

    @JsonProperty("Values")
    public void setValues(List<Integer> values) {
        values = new ArrayList<>(values);
        this.values = Collections.unmodifiableList(values);
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


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
    "Filter",
    "SupplierShortCards"
})
public class SupplierCardsAndFiltersDO {

    @JsonProperty("Filter")
    private SupplierFiltersDO filter;
    @JsonProperty("SupplierShortCards")
    private List<SupplierShortCard> supplierShortCards = Collections.emptyList();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Filter")
    public SupplierFiltersDO getFilter() {
        return filter;
    }

    @JsonProperty("Filter")
    public void setFilter(SupplierFiltersDO filter) {
        this.filter = filter;
    }

    @JsonProperty("SupplierShortCards")
    public List<SupplierShortCard> getSupplierShortCards() {
        return supplierShortCards;
    }

    @JsonProperty("SupplierShortCards")
    public void setSupplierShortCards(List<SupplierShortCard> supplierShortCards) {
        supplierShortCards = new ArrayList<>(supplierShortCards);
        this.supplierShortCards = Collections.unmodifiableList(supplierShortCards);
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

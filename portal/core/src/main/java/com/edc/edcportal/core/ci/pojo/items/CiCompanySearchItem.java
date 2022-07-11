package com.edc.edcportal.core.ci.pojo.items;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class CiCompanySearchItem implements Serializable {
    /*
     * POJO to Unmarshal JSON
     */

    private static final long serialVersionUID = -3888743078848354177L;

    private List<CiCompanySearchItemResult> result;

    public List<CiCompanySearchItemResult> getResult() {
        // Task 22143 squid:S2384
        return result != null ? ImmutableList.copyOf(result) : result;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    public void setResult(List<CiCompanySearchItemResult> result) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.result = result;
    }
}

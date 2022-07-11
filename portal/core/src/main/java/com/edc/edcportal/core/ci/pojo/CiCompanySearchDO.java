package com.edc.edcportal.core.ci.pojo;

import java.io.Serializable;
import java.util.List;

import com.edc.edcportal.core.ci.pojo.items.CiCompanySearchItem;
import com.google.common.collect.ImmutableList;

public class CiCompanySearchDO implements Serializable {
    /*
     * POJO to Unmarshal JSON
     */

    private static final long serialVersionUID = 3678458222041826996L;

    private String responseStatus;
    private List<CiCompanySearchItem> results;

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<CiCompanySearchItem> getResults() {
        // Task 22143 squid:S2384
        return results != null ? ImmutableList.copyOf(results) : results;
    }

    // Task 22143 squid:S2384
    @SuppressWarnings("squid:S2384")
    public void setResults(List<CiCompanySearchItem> results) {
        /// we cannot store as an inmutable copy as we need to we need to modify the
        /// content on the property later.
        this.results = results;
    }
}

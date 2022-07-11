package com.edc.edcweb.core.telp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MmEntrySector {

    @JsonProperty("SectorCode")
    private String sectorCode;

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

}

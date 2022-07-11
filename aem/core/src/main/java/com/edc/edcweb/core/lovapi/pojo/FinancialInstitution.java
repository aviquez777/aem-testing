package com.edc.edcweb.core.lovapi.pojo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinancialInstitution {

    @JsonProperty("NameEn")
    private String nameEn;

    @JsonProperty("NameFr")
    private String nameFr;

    @JsonProperty("EdcCode")
    private String edcCode;

    @JsonProperty("EmailDomains")
    private List<String> emailDomains = Collections.emptyList();

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public String getEdcCode() {
        return edcCode;
    }

    public void setEdcCode(String edcCode) {
        this.edcCode = edcCode;
    }

    public List<String> getEmailDomains() {
        return emailDomains;
    }

    public void setEmailDomains(List<String> emailDomains) {
        this.emailDomains = emailDomains;
        emailDomains = new ArrayList<>(emailDomains);
        this.emailDomains = Collections.unmodifiableList(emailDomains);
    }

}

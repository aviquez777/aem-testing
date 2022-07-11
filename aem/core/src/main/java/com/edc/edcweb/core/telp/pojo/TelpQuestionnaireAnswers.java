package com.edc.edcweb.core.telp.pojo;

public class TelpQuestionnaireAnswers {
    private String exporterType;
    private String primaryCountryExport;
    private String primaryCountryExportDesc;
    private String eligibilityStatus;

    public String getExporterType() {
        return exporterType;
    }

    public void setExporterType(String exporterType) {
        this.exporterType = exporterType;
    }

    public String getPrimaryCountryExport() {
        return primaryCountryExport;
    }

    public void setPrimaryCountryExport(String primaryCountryExport) {
        this.primaryCountryExport = primaryCountryExport;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getPrimaryCountryExportDesc() {
        return primaryCountryExportDesc;
    }

    public void setPrimaryCountryExportDesc(String primaryCountryExportDesc) {
        this.primaryCountryExportDesc = primaryCountryExportDesc;
    }
}

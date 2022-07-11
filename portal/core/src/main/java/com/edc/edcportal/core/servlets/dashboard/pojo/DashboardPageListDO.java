package com.edc.edcportal.core.servlets.dashboard.pojo;

import java.util.LinkedList;
import java.util.List;

public class DashboardPageListDO {

    String errorMessage;
    List<PageItem> pageItems = new LinkedList<>();

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<PageItem> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<PageItem> pageItems) {
        this.pageItems = pageItems;
    }

}

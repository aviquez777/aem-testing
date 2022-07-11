package com.edc.edcportal.core.servlets.dashboard.pojo;

import java.util.List;

import com.edc.edcportal.core.models.pojo.DashboardItem;

public class PageItem {

    int pageNumber;
    List<DashboardItem> dashboardItems;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

}

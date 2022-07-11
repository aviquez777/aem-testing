package com.edc.edcweb.core.upcomingwebinars.pojo;

import java.util.LinkedList;
import java.util.List;

import com.edc.edcweb.core.components.UpcomingWBCEventCard;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UWResponse {

    String errorMessage;
    List<UpcomingWBCEventCard> pageItems = new LinkedList<>();

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<UpcomingWBCEventCard> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<UpcomingWBCEventCard> pageItems) {
        this.pageItems = pageItems;
    }

}

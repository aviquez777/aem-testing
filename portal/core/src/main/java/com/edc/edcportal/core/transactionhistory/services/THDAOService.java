package com.edc.edcportal.core.transactionhistory.services;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.wcm.api.Page;

public interface THDAOService {
    Boolean doWebinarTrackingRecord(SlingHttpServletRequest request, Page myPage);
}

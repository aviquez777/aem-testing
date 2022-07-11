package com.edc.edcweb.core.upcomingwebinars;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jcr.query.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.components.UpcomingWBCEventCard;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.Constants.SupportedLanguages;
import com.edc.edcweb.core.helpers.webinars.WebinarsHelper;
import com.edc.edcweb.core.upcomingwebinars.helpers.SQL2QueryHelpers;
import com.edc.edcweb.core.upcomingwebinars.pojo.UWResponse;

public class UWServletUtils {

    private UWServletUtils() {
        // Sonar lint
    }

    public static UWResponse getUWResponse(String[] paths, String[] tags, ResourceResolver resolver,
            Locale parentPageLocale) {
        UWResponse response = new UWResponse();
        String queryText = SQL2QueryHelpers.getPagesQueryStatement(paths, tags);
        // Execute the query
        Iterator<Resource> resList = resolver.findResources(queryText, Query.JCR_SQL2);
        // Get current date time
        Calendar rightNow = Calendar.getInstance();
        if (resList.hasNext()) {
            // No error message
            response.setErrorMessage("");
            List<UpcomingWBCEventCard> pageItems = new LinkedList<>();
            while (resList.hasNext()) {
                Page currentPage = resList.next().getParent().adaptTo(Page.class);
                // Get the endDateTime as the Query does not take time in account
                Calendar endDateTime = WebinarsHelper.getWebinarDateTime(currentPage, false);
                // if the end time is not now, the webinar should display
                if (endDateTime != null && endDateTime.after(rightNow)) {
                    pageItems.add(populateCard(currentPage, tags, parentPageLocale, endDateTime, resolver));
                }
            }
            pageItems.sort(Comparator.comparing(UpcomingWBCEventCard::getWebinarStartDateTime));
            // populate the response
            response.setPageItems(pageItems);
        }
        return response;
    }

    private static UpcomingWBCEventCard populateCard(Page page, String[] tags, Locale parentPageLocale, Calendar endDateTime, ResourceResolver resourceResolver) {
        UpcomingWBCEventCard eventCard = new UpcomingWBCEventCard();
        ValueMap vm = page.getProperties();

        Tag[] pageTags = page.getTags();
        for (Tag myTag : pageTags) {
            if (ArrayUtils.contains(tags, myTag.getTagID())) {
                eventCard.setEventType(myTag.getTitle(parentPageLocale));
            }
        }

        eventCard.setLinkText(vm.get(UWConstants.PAGE_TITLE, String.class));

        // Bug 374829 no page Alias
        String pagePath = resourceResolver.map(page.getPath());  // Make sure we get the alias
        // LinkResolver does not work on alias
        eventCard.setLinkUrl(pagePath.concat(Constants.HTML_EXTENTION));
        // End Bug 374829 no page Alias

        eventCard.setLinkTarget(vm.get(UWConstants.WEBINAR_LINK_TARGET, String.class));
        eventCard.setDescription(vm.get(UWConstants.PAGE_DESCRIPTION, String.class));
        eventCard.setImage(vm.get(UWConstants.TEASER_IMAGE, String.class));
        eventCard.setImageAlt(vm.get(UWConstants.IMAGE_ALT_TEXT, String.class));
        Calendar startDateTime = WebinarsHelper.getWebinarDateTime(page, true);
        eventCard.setWebinarStartDateTime(startDateTime);
        eventCard.setWebinarEndDateTime(endDateTime);

        if (startDateTime != null && endDateTime != null) {
            String dateFormat = SupportedLanguages.ENGLISH.getDateFormat();
            if (parentPageLocale.getLanguage().toUpperCase().equals(SupportedLanguages.FRENCH.getLanguageAbbreviation())) {
                dateFormat = SupportedLanguages.FRENCH.getDateFormat();
            }
            DateFormat outputFormat = new SimpleDateFormat(dateFormat, parentPageLocale);

            String startingDate = outputFormat.format(startDateTime.getTime());
            String endingDate = outputFormat.format(endDateTime.getTime());

            // Change to startDateTime.equals(endDateTime)) if time is required
            if (startingDate.equals(endingDate)) {
                eventCard.setWebinarDateText(startingDate);
            } else {
                eventCard.setWebinarDateText(
                        startingDate + " - " + endingDate);
            }
        }
        return eventCard;
    }
}

package com.edc.edcweb.core.helpers;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 *        Request to format date and time for locale.
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class DateFormater {
    private static final Logger log = LoggerFactory.getLogger(DateFormater.class);
    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Optional
    public Calendar date;

    @Inject
    @Optional
    @Named("timezone")
    private String timezoneProperty;

    @Inject
    private String formatEN;

    @Inject
    private String formatFR;

    private String dateConvertedLocale;

    public String getDateFormated() {
        if (date != null) {
            return date.toString();
        }
        return null;
    }

    public String getDateConvertedLocale() {
        if (date != null) {
            return dateConvertedLocale;
        }
        return null;
    }

    @PostConstruct
    public void initModel() {

        if (date == null) {
            return;
        }
        try {
            String lang = getLanguageAbbr(this.request).toLowerCase();
            String format = formatEN;
            if (lang.equalsIgnoreCase("fr")) {
                format = formatFR;
            }

            // timezoneProperty
            TimeZone timeZ;

            if (this.timezoneProperty != null) {
                log.debug("this.timezoneProperty {}", this.timezoneProperty);
                timeZ = TimeZone.getTimeZone(this.timezoneProperty);
            } else {
                timeZ = TimeZone.getDefault();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(format, new Locale(lang));
            DateFormatSymbols symbols = new DateFormatSymbols(new Locale(lang));
            if (lang.equalsIgnoreCase("fr")) {
                symbols.setAmPmStrings(new String[] { "h", "h" });
            } else {
                symbols.setAmPmStrings(new String[] { "am", "pm" });
            }

            dateFormat.setTimeZone(timeZ);
            dateFormat.setDateFormatSymbols(symbols);
            dateConvertedLocale = dateFormat.format(date.getTime());
            log.debug("dateConvertedLocale {} timezone {}", dateConvertedLocale, timeZ.getID());

        } catch (Exception e) {
            log.debug("initModel Exception error {}", e);
            log.error("error ", e);
        }
    }

    private static String getLanguageAbbr(SlingHttpServletRequest request) {

        String langAbbr = "";
        String url = request.getRequestURL().toString();
        langAbbr = LanguageUtil.getLanguageAbbreviationFromPath(url,
                Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
        return langAbbr;
    }

    /**
     * Implements a {@see java.util.Comparator} Comparator that will determine how
     * two dates should be sorted.
     */
    public static class ListSortTimeDate implements Comparator<Page>, Serializable {
        private static final long serialVersionUID = 204096578107748870L;
        private SortOrder sortOrder;

        public ListSortTimeDate(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        /**
         * Compare two data pages and determine alphabetical order.
         *
         * 
         * @return A negative integer, zero, or a positive integer as the first country
         *         name is less than, equal to, or greater than the second respectively.
         */
        @Override
        public int compare(Page event, Page event2) {
            int i = 0;
            // ------------------------------------------------------------------
            // ------------------------------------------------------------------
            if (event != null && event2 != null) {
                // get the time and date....ValueMap prop = listEvt.get(i)
                ValueMap eventProp = event.getProperties();
                ValueMap eventProp2 = event2.getProperties();

                Calendar date = eventProp.get("startDate", Calendar.class);
                Calendar date2 = eventProp2.get("startDate", Calendar.class);

                if (date != null && date2 != null) {
                    i = date.compareTo(date2);

                    if (i == 0) {
                        Calendar time = eventProp.get("startTime", Calendar.class);
                        Calendar time2 = eventProp2.get("startTime", Calendar.class);
                        if (time != null && time2 != null) {
                            i = time.compareTo(time2);
                        }

                    }
                }

                if (sortOrder == SortOrder.DESC) {
                    i = i * -1;
                }
            }
            return i;
        }
    }

    public enum SortOrder {
        ASC("asc"), DESC("desc");

        private String value;

        SortOrder(String value) {
            this.value = value;
        }

        public static SortOrder fromString(String value) {
            for (SortOrder s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ASC;
        }
    }
}

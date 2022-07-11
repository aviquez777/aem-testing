package com.edc.edcportal.core.helpers;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilsHelper {

    private static final Logger logger = LoggerFactory.getLogger(DateUtilsHelper.class);

    private DateUtilsHelper() {
        // Sonar Lint
    }

    /**
     * Convert from iSODate String To UnixTimestam String
     * 
     * @param createdDate iSODate String
     * @param returnNow   whether to send current time as fallback if conversion is
     *                    not successful
     * @return UnixTimestamp string
     */

    public static String iSOToUnixTimestamp(String createdDate, boolean returnNow) {
        String currentDate = returnNow ? Long.toString(Instant.now().getEpochSecond()) : null;
        if (StringUtils.isNotBlank(createdDate)) {
            try {
                createdDate = Long.toString(Instant.parse(createdDate).getEpochSecond());
            } catch (DateTimeException e) {
                // don't use an invalid date
                logger.debug("LoginRequestHeadersUtil error converting date {} {}", createdDate, e.getStackTrace());
                createdDate = currentDate;
            }
        } else {
            logger.debug("LoginRequestHeadersUtil header date was empty -{}-, used current date {}", createdDate,
                    currentDate);
            createdDate = currentDate;
        }
        return createdDate;
    }

    /**
     * Convert From UnixTimestam String to Date
     * 
     * @param createdDateStr UnixTimestam String
     * @param returnNow      whether to send current time as fallback if conversion
     *                       is not successful
     * @return Date
     */
    public static Date unixTimestampToDate(String createdDateStr, boolean returnNow) {
        Date createdDate = returnNow ? new Date() : null;
        if (StringUtils.isNotBlank(createdDateStr)) {
            try {
                createdDate = Date.from(Instant.ofEpochSecond(Long.parseLong(createdDateStr)));
            } catch (DateTimeException e) {
                // don't use an invalid date
                logger.debug("LoginRequestHeadersUtil error converting date {} {}", createdDate, e.getStackTrace());
            }
        } else {
            logger.debug("LoginRequestHeadersUtil header date was empty -{}-, used current date {}", createdDateStr,
                    createdDate);
        }
        return createdDate;
    }

    /**
     * Get next years from given date, current date if null
     * 
     * @param currentDate
     * @return a year from date
     */
    public static Date getNextYear(Calendar currentDate) {
        if (currentDate == null) {
            currentDate = Calendar.getInstance();
        }
        currentDate.add(Calendar.YEAR, 1);
        return currentDate.getTime();
    }

    /**
     *  Return the date formatted as per language
     * @param date
     * @param lang
     * @return formatted date string
     */
    public static String formatDate(Calendar date, Locale lang) {
        String format = Constants.SupportedLanguages.getLanguageFromAbbreviation(lang.getLanguage()).getDateFormat();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, lang);
        return dateFormat.format(date.getTime());
    }

}

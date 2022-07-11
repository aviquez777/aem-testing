package com.edc.edcweb.core.upcomingwebinars.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQL2QueryHelpers {
    
    private SQL2QueryHelpers () {
        // Sonar LInt
    }

    public static String getPagesQueryStatement(String[] paths, String[] tags) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * from [nt:base] AS t WHERE (");
        int counter = 1;
        for (String path : paths) {
            query.append("ISDESCENDANTNODE(t, [").append(path).append("])");
            if (paths.length > 1 && counter < paths.length) {
                query.append(" OR ");
                counter++;
            }
        }
        query.append(")");

        counter = 1;
        query.append( " AND (");
        for (String tag : tags) {
            query.append("(t.[cq:tags] = '").append(tag).append("')");
            if (tags.length > 1 && counter < tags.length) {
                query.append(" OR ");
                counter++;
            }
        }
        query.append(") ");
        // Unfortunately the endDate has no time, and endTime might not be the same date as the endDate 
        // We should query for webinars starting today and later, and filter the result set later
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
        String todayEOD = sdf.format(new Date());
        query.append("AND t.endDate >= CAST('").append(todayEOD).append("' AS DATE) ");
        return query.toString();
    }
}


package com.edc.edcweb.core.helpers;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = SlingHttpServletRequest.class)
public class HTLHelper {

    @Inject
    @Optional
    private String toApply;

    @Inject
    @Optional
    @Default(booleanValues = false)
    private boolean toLowerCase;

    @Inject
    @Optional
    private String changeToCsv;

    @Inject
    @Optional
    private String replace;

    @Inject
    @Optional
    private String append;

    @Inject
    @Optional
    private String prepend;

    /**
     * Manipulate Strings
     * 
     * @return strings with manipulations applied
     */
    public String getApplied() {
        String applied = null;
        if (StringUtils.isNotBlank(toApply)) {
            applied = toApply;
            // Change to csv
            if (StringUtils.isNotBlank(changeToCsv)) {
                String[] stringArr = applied.split(changeToCsv);
                applied = String.join(",", stringArr);
            }
            // replace values given by a pair delimited by "~"
            if (StringUtils.isNotBlank(replace) && StringUtils.contains(replace, "~")) {
                String[] replaceArr = replace.split("~");
                applied = StringUtils.replace(applied, replaceArr[0], replaceArr[1]);
            }
            // To lower case
            if (toLowerCase) {
                applied = StringUtils.lowerCase(applied);
            }
            // Add Chars after
            if (StringUtils.isNotBlank(append)) {
                applied = applied.concat(append);
            }
            // Add Chars before
            if (StringUtils.isNotBlank(prepend)) {
                applied = prepend.concat(applied);
            }
        }
        return applied;
    }

    @Inject
    @Optional
    private String listQty;

    /**
     * Sequential numbers required by file uploader
     * 
     * @return sequential array
     */
    public Object[] getList() {
        Object[] list = null;
        if (StringUtils.isNotBlank(listQty)) {
            int listQtyInt = Integer.parseInt(listQty);
            list = IntStream.rangeClosed(1, listQtyInt).boxed().collect(Collectors.toList()).toArray();
        }
        return list;
    }
}

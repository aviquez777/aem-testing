package com.edc.edcweb.core.helpers;

public class FieldMapUtils {

    private FieldMapUtils() {
        // Sonar Lint
    }

    public static String getFieldName(String[] arr) {
        String id = null;
        if (arr != null && arr.length > 0) {
            id = arr[0];
        }
        return id;
    }

    public static String getEloquaId(String[] arr) {
        String id = null;
        if (arr != null && arr.length > 1) {
            id = arr[1];
        }
        return id;
    }

    public static String getPPId(String[] arr) {
        String id = null;
        if (arr != null && arr.length > 2) {
            id = arr[2];
        }
        return id;
    }

    public static String getTransId(String[] arr) {
        String id = null;
        if (arr != null && arr.length > 3) {
            id = arr[3];
        }
        return id;
    }


}

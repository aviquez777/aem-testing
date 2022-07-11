package com.edc.edcweb.core.gatedcontentaccess.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.serviceImpl.pojo.EloquaContact;

public class GatedContentAccessUtils {
    
    private static final Logger log = LoggerFactory.getLogger(GatedContentAccessUtils.class);
    
    private GatedContentAccessUtils () {
        // SonarQube
    }

    /**
     *  Check if the fields has data to decide whether it should be shown on the front end
     * @param eloquaContact
     * @param fieldName
     * @return true if no data false otherwise
     */
    public static boolean isShowField(EloquaContact eloquaContact, String fieldName) {
        String value = null;
            // if any error, mark the field as empty
            try {
                value = getEloquaValue(eloquaContact, fieldName);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                log.error("GatedContentAccessUtils error getting info for field {}: ", fieldName, e);
            }
        // Return true if no value to add the field
        return StringUtils.isBlank(value);
    }
    /**
     *  Get the eloqua value by invoking the method with the field name.
     * @param eloquaContact
     * @param fieldName
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
 */
    private static String getEloquaValue(EloquaContact eloquaContact, String fieldName) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String[] searchList = {"-","_"};
        String[] replacementList = {"",""};
        String safeFieldName = StringUtils.replaceEach(fieldName, searchList, replacementList);
        String methodName = GatedContentAccessConstats.GET_METHOD.concat(StringUtils.capitalize(safeFieldName));
        Method method = eloquaContact.getClass().getDeclaredMethod(methodName);
        return (String) method.invoke(eloquaContact);
    }
}

package com.edc.edcweb.core.helpers.formvalidation;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FieldValidators {

    private static final Logger log = LoggerFactory.getLogger(FieldValidators.class);
    
    private FieldValidators () {
        //. Sonar Lint
    }
    
    /**
     *  Validate if email seems ok
     * @param email
     * @return true if valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        boolean result = true;
        try {
           InternetAddress emailAddr = new InternetAddress(email);
           emailAddr.validate();
        } catch (AddressException ex) {
           result = false;
           log.error("FieldValidators validateEmail invalidEmail {}", email);
        }
        return result;
     }
    
    
    /**
     * Check if String contains specific words
     * @param inputString
     * @param items array of words to find
     * @return true if found at least one, false otherwise
     */
    public static boolean containsWord(String inputString, String[] items) {
        boolean found = false;
        for (String item : items) {
            if (inputString.contains(item)) {
                found = true;
                break;
            }
        }
        return found;
    }
}

/**
 * 
 */
package com.edc.edcportal.core.exception;

/**
 * @author gavik.wadhwa
 *
 */
public class EDCAPIMSystemException extends EDCExternalSystemException {

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 4072412995625713185L;

    /**
     * Default Constructor
     */
    public EDCAPIMSystemException() {
        super();
    }

    /**
     * @param errorMessage
     * @param className
     * @param methodName
     * @param originalExceptionMessage
     */
    public EDCAPIMSystemException(String errorMessage, String className, String methodName,
            String originalExceptionMessage) {
        super(errorMessage, className, methodName, originalExceptionMessage);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EDCAPIMSystemException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public EDCAPIMSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public EDCAPIMSystemException(String message) {
        super(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EDCAPIMSystemException [errorMessage=" + getErrorMessage() + ", className=" + getClassName()
                + ", methodName=" + getMethodName() + ", originalExceptionMessage=" + getOriginalExceptionMessage()
                + "]";
    }

}

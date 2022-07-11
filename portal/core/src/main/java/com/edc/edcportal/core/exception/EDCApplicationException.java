/**
 * 
 */
package com.edc.edcportal.core.exception;

/**
 * @author gavik.wadhwa
 *
 */
public class EDCApplicationException extends EDCException {

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 7350328306526707968L;

    /**
     * Default Constructor
     */
    public EDCApplicationException() {
        super();
    }

    /**
     * @param errorMessage
     * @param className
     * @param methodName
     * @param originalExceptionMessage
     */
    public EDCApplicationException(String errorMessage, String className, String methodName,
            String originalExceptionMessage) {
        super(errorMessage, className, methodName, originalExceptionMessage);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EDCApplicationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public EDCApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public EDCApplicationException(String message) {
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

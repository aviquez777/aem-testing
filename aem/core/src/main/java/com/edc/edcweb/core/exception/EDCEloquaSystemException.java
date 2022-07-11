/**
 *
 */
package com.edc.edcweb.core.exception;

/**
 * @author gavik.wadhwa
 *
 */
public class EDCEloquaSystemException extends EDCExternalSystemException {

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -7404284327949240262L;

    /**
     * Default Constructor
     */
    public EDCEloquaSystemException() {
        super();
    }

    /**
     * @param errorMessage
     * @param className
     * @param methodName
     * @param originalExceptionMessage
     */
    public EDCEloquaSystemException(String errorMessage, String className, String methodName,
            String originalExceptionMessage) {
        super(errorMessage, className, methodName, originalExceptionMessage);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EDCEloquaSystemException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public EDCEloquaSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public EDCEloquaSystemException(String message) {
        super(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EDCEloquaSystemException [errorMessage=" + getErrorMessage() + ", className=" + getClassName()
                + ", methodName=" + getMethodName() + ", originalExceptionMessage=" + getOriginalExceptionMessage()
                + "]";
    }

}

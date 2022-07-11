/**
 * 
 */
package com.edc.edcportal.core.exception;

/**
 * @author gavik.wadhwa
 *
 */
public class EDCException extends Exception {

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -5377943248968171161L;

    /**
     * This property is use to set application/system level error message.
     **/
    private String errorMessage;

    /**
     * This property is use to set the class name where exception originate.
     */
    private String className;

    /**
     * This property is use to set the method name where exception originate.
     */
    private String methodName;

    /**
     * This property is use to set the message getting from original exception
     * 
     */
    private String originalExceptionMessage;

    /**
     * Default Constructor
     */
    public EDCException() {
        super();
    }

    /**
     * @param errorMessage
     * @param className
     * @param methodName
     * @param originalExceptionMessage
     */
    public EDCException(String errorMessage, String className, String methodName, String originalExceptionMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.className = className;
        this.methodName = methodName;
        this.originalExceptionMessage = originalExceptionMessage;
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EDCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorMessage = message;
    }

    /**
     * @param message
     * @param cause
     */
    public EDCException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    /**
     * @param message
     */
    public EDCException(String message) {
        super(message);
        this.errorMessage = message;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the originalExceptionMessage
     */
    public String getOriginalExceptionMessage() {
        return originalExceptionMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EDCException [errorMessage=" + getErrorMessage() + ", className=" + getClassName() + ", methodName="
                + getMethodName() + ", originalExceptionMessage=" + getOriginalExceptionMessage() + "]";
    }

}

/**
 * 
 */
package com.edc.edcportal.core.exception;

/**
 * @author gavik.wadhwa
 *
 */
public class EDCExternalSystemException extends EDCException {

	private static final long serialVersionUID = 7350328306526707968L;
	
	
	/**
	 * 
	 */
	public EDCExternalSystemException() {
		super();
	}


	/**
	 * @param errorMessage
	 * @param className
	 * @param methodName
	 * @param originalExceptionMessage
	 */
	public EDCExternalSystemException(String errorMessage, String className, String methodName,
			String originalExceptionMessage) {
		super(errorMessage, className, methodName, originalExceptionMessage);
	}


	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EDCExternalSystemException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	/**
	 * @param message
	 * @param cause
	 */
	public EDCExternalSystemException(String message, Throwable cause) {
		super(message, cause);
	}


	/**
	 * @param message
	 */
	public EDCExternalSystemException(String message) {
		super(message);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EDCExternalSystemException [errorMessage=" + getErrorMessage() + ", className=" + getClassName() + ", methodName=" + getMethodName()
				+ ", originalExceptionMessage=" + getOriginalExceptionMessage() + "]";
	}
}

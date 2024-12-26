package com.pdd.redcurrant.application.exception;

/**
 * Defines the policy contract for custom exceptions which need to adhere to it for
 * providing a standardized behavior.
 */
public interface ExceptionPolicy {

    /**
     * Get the exception code.
     * @return the exception code.
     */
    String getCode();

    /**
     * Get the exception message.
     * @return the exception message.
     */
    String getMessage();

    /**
     * Get the http status associated with the exception.
     * @return the http status associated with the exception
     */
    HttpStatus getHttpStatus();

}

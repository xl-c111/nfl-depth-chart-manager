package com.fanduel.depthchart.exception;

/**
 * Unchecked exception for depth chart contract and validation violations.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public class DepthChartValidationException extends RuntimeException {

    /**
     * Constructs a validation exception with a message.
     *
     * @param message the error message
     */
    public DepthChartValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a validation exception with a message and cause.
     *
     * @param message the error message
     * @param cause the root cause
     */
    public DepthChartValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

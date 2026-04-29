package com.fanduel.depthchart.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepthChartValidationExceptionTest {

    @Test
    void constructor_shouldStoreMessage() {
        DepthChartValidationException exception = new DepthChartValidationException("invalid depth");

        assertEquals("invalid depth", exception.getMessage());
    }

    @Test
    void constructor_shouldStoreMessageAndCause() {
        IllegalArgumentException cause = new IllegalArgumentException("bad input");

        DepthChartValidationException exception = new DepthChartValidationException("invalid input", cause);

        assertEquals("invalid input", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}

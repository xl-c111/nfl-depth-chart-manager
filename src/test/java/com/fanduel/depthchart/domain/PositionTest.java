package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionTest {

    @Test
    void shouldNormalizePositionCodeWhenCreatingPosition() {
        Position position = Position.of(" qb ");

        assertEquals("QB", position.getCode());
        assertEquals("QB", position.toString());
    }

    @Test
    void shouldRejectNullCodeWhenCreatingPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> Position.of(null));
    }

    @Test
    void shouldRejectBlankCodeWhenCreatingPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> Position.of("   "));
    }

    @Test
    void shouldRejectUnsupportedNflPositionCodeWhenCreatingPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> Position.of("XYZ"));
    }

    @Test
    void shouldBeEqualWhenPositionsHaveSameNormalizedCode() {
        Position lowercase = Position.of("qb");
        Position uppercase = Position.of("QB");

        assertEquals(lowercase, uppercase);
        assertEquals(lowercase.hashCode(), uppercase.hashCode());
    }

    @Test
    void shouldReturnTrueWhenEqualsCalledWithSameInstance() {
        Position qb = Position.of("QB");

        assertTrue(qb.equals(qb));
    }

    @Test
    void shouldReturnFalseWhenEqualsCalledWithNullOrDifferentType() {
        Position qb = Position.of("QB");

        assertFalse(qb.equals(null));
        assertFalse(qb.equals("QB"));
    }

    @Test
    void shouldNotBeEqualWhenPositionsHaveDifferentCodes() {
        Position qb = Position.of("QB");
        Position rb = Position.of("RB");

        assertNotEquals(qb, rb);
    }
}

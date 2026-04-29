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
    void of_shouldNormalizePositionCode() {
        Position position = Position.of(" qb ");

        assertEquals("QB", position.getCode());
        assertEquals("QB", position.toString());
    }

    @Test
    void of_shouldRejectNullCode() {
        assertThrows(
                DepthChartValidationException.class,
                () -> Position.of(null));
    }

    @Test
    void of_shouldRejectBlankCode() {
        assertThrows(
                DepthChartValidationException.class,
                () -> Position.of("   "));
    }

    @Test
    void positionsWithSameNormalizedCode_shouldBeEqual() {
        Position lowercase = Position.of("qb");
        Position uppercase = Position.of("QB");

        assertEquals(lowercase, uppercase);
        assertEquals(lowercase.hashCode(), uppercase.hashCode());
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        Position qb = Position.of("QB");

        assertTrue(qb.equals(qb));
    }

    @Test
    void equals_shouldReturnFalseForNullOrDifferentType() {
        Position qb = Position.of("QB");

        assertFalse(qb.equals(null));
        assertFalse(qb.equals("QB"));
    }

    @Test
    void positionsWithDifferentCodes_shouldNotBeEqual() {
        Position qb = Position.of("QB");
        Position rb = Position.of("RB");

        assertNotEquals(qb, rb);
    }
}

package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}

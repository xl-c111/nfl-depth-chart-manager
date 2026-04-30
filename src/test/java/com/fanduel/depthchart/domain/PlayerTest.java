package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

    @Test
    void shouldExposeNumberAndNameWhenPlayerIsConstructed() {
        Player player = new Player(12, "Tom Brady");

        assertEquals(12, player.getNumber());
        assertEquals("Tom Brady", player.getName());
    }

    @Test
    void shouldBeEqualWhenPlayersShareSameNumber() {
        Player tomBrady = new Player(12, "Tom Brady");
        Player renamedPlayer = new Player(12, "Different Name");

        assertEquals(tomBrady, renamedPlayer);
        assertEquals(tomBrady.hashCode(), renamedPlayer.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenPlayersHaveDifferentNumbers() {
        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");

        assertNotEquals(tomBrady, blaineGabbert);
    }

    @Test
    void shouldReturnTrueWhenEqualsCalledWithSameInstance() {
        Player tomBrady = new Player(12, "Tom Brady");

        assertTrue(tomBrady.equals(tomBrady));
    }

    @Test
    void shouldReturnFalseWhenEqualsCalledWithNullOrDifferentType() {
        Player tomBrady = new Player(12, "Tom Brady");

        assertFalse(tomBrady.equals(null));
        assertFalse(tomBrady.equals("Tom Brady"));
    }

    @Test
    void shouldRejectNullNameWhenConstructingPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(12, null));
    }

    @Test
    void shouldRejectBlankNameWhenConstructingPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(12, "   "));
    }

    @Test
    void shouldAllowZeroNumberWhenConstructingPlayer() {
        Player player = new Player(0, "Zero Number");

        assertEquals(0, player.getNumber());
    }

    @Test
    void shouldRejectNegativeNumberWhenConstructingPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(-12, "Tom Brady"));
    }

    @Test
    void shouldRejectNumberGreaterThanNinetyNineWhenConstructingPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(100, "Too Large"));
    }

    @Test
    void shouldTrimNameWhenConstructingPlayer() {
        Player player = new Player(12, "  Tom Brady  ");

        assertEquals("Tom Brady", player.getName());
    }

    @Test
    void shouldReturnChallengeFormatWhenConvertingPlayerToString() {
        Player player = new Player(12, "Tom Brady");

        assertEquals("(#12, Tom Brady)", player.toString());
    }
}

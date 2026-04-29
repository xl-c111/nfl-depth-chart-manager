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
    void player_shouldExposeNumberAndName() {
        Player player = new Player(12, "Tom Brady");

        assertEquals(12, player.getNumber());
        assertEquals("Tom Brady", player.getName());
    }

    @Test
    void playersWithSameNumber_shouldBeEqualEvenWhenNameDiffers() {
        Player tomBrady = new Player(12, "Tom Brady");
        Player renamedPlayer = new Player(12, "Different Name");

        assertEquals(tomBrady, renamedPlayer);
        assertEquals(tomBrady.hashCode(), renamedPlayer.hashCode());
    }

    @Test
    void playersWithDifferentNumbers_shouldNotBeEqual() {
        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");

        assertNotEquals(tomBrady, blaineGabbert);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        Player tomBrady = new Player(12, "Tom Brady");

        assertTrue(tomBrady.equals(tomBrady));
    }

    @Test
    void equals_shouldReturnFalseForNullOrDifferentType() {
        Player tomBrady = new Player(12, "Tom Brady");

        assertFalse(tomBrady.equals(null));
        assertFalse(tomBrady.equals("Tom Brady"));
    }

    @Test
    void player_shouldRejectNullName() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(12, null));
    }

    @Test
    void player_shouldRejectBlankName() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(12, "   "));
    }

    @Test
    void player_shouldRejectNonPositiveNumber() {
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(0, "Tom Brady"));
        assertThrows(
                DepthChartValidationException.class,
                () -> new Player(-12, "Tom Brady"));
    }

    @Test
    void player_shouldTrimName() {
        Player player = new Player(12, "  Tom Brady  ");

        assertEquals("Tom Brady", player.getName());
    }

    @Test
    void toString_shouldReturnChallengePlayerFormat() {
        Player player = new Player(12, "Tom Brady");

        assertEquals("(#12, Tom Brady)", player.toString());
    }
}

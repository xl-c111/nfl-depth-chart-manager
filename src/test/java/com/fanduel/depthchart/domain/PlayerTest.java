package com.fanduel.depthchart.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    void player_shouldRejectNullName() {
        assertThrows(
                NullPointerException.class,
                () -> new Player(12, null));
    }

    @Test
    void toString_shouldReturnChallengePlayerFormat() {
        Player player = new Player(12, "Tom Brady");

        assertEquals("(#12, Tom Brady)", player.toString());
    }
}

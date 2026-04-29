package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import org.junit.jupiter.api.Test;

import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.JAELON_DARDEN;
import static com.fanduel.depthchart.fixture.PlayerFixture.KYLE_TRASK;
import static com.fanduel.depthchart.fixture.PlayerFixture.MIKE_EVANS;
import static com.fanduel.depthchart.fixture.PlayerFixture.SCOTT_MILLER;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;

class InMemoryDepthChartServiceTest {
    private DepthChartService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryDepthChartService();
    }

    private void addSampleDepthChart() {
        service.addPlayerToDepthChart("QB", TOM_BRADY, 0);
        service.addPlayerToDepthChart("QB", BLAINE_GABBERT, 1);
        service.addPlayerToDepthChart("QB", KYLE_TRASK, 2);

        service.addPlayerToDepthChart("LWR", MIKE_EVANS, 0);
        service.addPlayerToDepthChart("LWR", JAELON_DARDEN, 1);
        service.addPlayerToDepthChart("LWR", SCOTT_MILLER, 2);
    }

    @Test
    void addPlayerToDepthChart_shouldSucceedAfterAddLogicIsImplemented() {
        DepthChartService service = new InMemoryDepthChartService();

        assertDoesNotThrow(
                () -> service.addPlayerToDepthChart("QB", new Player(12, "Tom Brady"), 0));
    }

    @Test
    void getBackups_shouldReturnSampleQuarterbackBackups() {
        addSampleDepthChart();

        List<Player> backups = service.getBackups("QB", TOM_BRADY);

        assertEquals(List.of(BLAINE_GABBERT, KYLE_TRASK), backups);
    }

    @Test
    void getFullDepthChart_shouldReturnFormattedSampleDepthChart() {
        addSampleDepthChart();

        String expected = String.join(System.lineSeparator(),
                "QB - (#12, Tom Brady), (#11, Blaine Gabbert), (#2, Kyle Trask)",
                "LWR - (#13, Mike Evans), (#1, Jaelon Darden), (#10, Scott Miller)");

        assertEquals(expected, service.getFullDepthChart());
    }

    @Test
    void removePlayerFromDepthChart_shouldRemovePlayerAndUpdateFullChart() {
        addSampleDepthChart();

        List<Player> removed = service.removePlayerFromDepthChart("LWR", MIKE_EVANS);

        assertEquals(List.of(MIKE_EVANS), removed);

        String expected = String.join(System.lineSeparator(),
                "QB - (#12, Tom Brady), (#11, Blaine Gabbert), (#2, Kyle Trask)",
                "LWR - (#1, Jaelon Darden), (#10, Scott Miller)");

        assertEquals(expected, service.getFullDepthChart());
    }

    @Test
    void addPlayerToDepthChart_shouldNormalizePositionInput() {
        service.addPlayerToDepthChart(" qb ", TOM_BRADY, 0);

        assertEquals(List.of(), service.getBackups("QB", TOM_BRADY));

        assertEquals("QB - (#12, Tom Brady)", service.getFullDepthChart());
    }
}

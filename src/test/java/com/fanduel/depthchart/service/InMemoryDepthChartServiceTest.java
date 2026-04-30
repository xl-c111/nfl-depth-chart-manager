package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.DepthChart;
import com.fanduel.depthchart.domain.Position;
import com.fanduel.depthchart.exception.DepthChartValidationException;
import com.fanduel.depthchart.formatter.DepthChartFormatter;
import org.junit.jupiter.api.Test;

import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.JAELON_DARDEN;
import static com.fanduel.depthchart.fixture.PlayerFixture.KYLE_TRASK;
import static com.fanduel.depthchart.fixture.PlayerFixture.MIKE_EVANS;
import static com.fanduel.depthchart.fixture.PlayerFixture.SCOTT_MILLER;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

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
    void constructor_shouldRejectNullDepthChart() {
        assertThrows(
                NullPointerException.class,
                () -> new InMemoryDepthChartService(null));
    }

    @Test
    void getBackups_shouldReturnSampleQuarterbackBackups() {
        addSampleDepthChart();

        List<Player> backups = service.getBackups("QB", TOM_BRADY);

        assertEquals(List.of(BLAINE_GABBERT, KYLE_TRASK), backups);
    }

    @Test
    void getBackups_shouldReturnEmptyListWhenPositionDoesNotMatch() {
        addSampleDepthChart();

        List<Player> backups = service.getBackups("QB", JAELON_DARDEN);

        assertEquals(List.of(), backups);
    }

    @Test
    void getFullDepthChart_shouldReturnSampleDepthChartSnapshot() {
        addSampleDepthChart();

        Map<Position, List<Player>> expected = Map.of(
                Position.of("QB"), List.of(TOM_BRADY, BLAINE_GABBERT, KYLE_TRASK),
                Position.of("LWR"), List.of(MIKE_EVANS, JAELON_DARDEN, SCOTT_MILLER));

        assertEquals(expected, service.getFullDepthChart());
    }

    @Test
    void removePlayerFromDepthChart_shouldRemovePlayerAndUpdateFullChart() {
        addSampleDepthChart();

        List<Player> removed = service.removePlayerFromDepthChart("LWR", MIKE_EVANS);

        assertEquals(List.of(MIKE_EVANS), removed);

        Map<Position, List<Player>> expected = Map.of(
                Position.of("QB"), List.of(TOM_BRADY, BLAINE_GABBERT, KYLE_TRASK),
                Position.of("LWR"), List.of(JAELON_DARDEN, SCOTT_MILLER));

        assertEquals(expected, service.getFullDepthChart());
    }

    @Test
    void removePlayerFromDepthChart_shouldReturnEmptyListWhenPositionDoesNotMatch() {
        addSampleDepthChart();

        List<Player> removed = service.removePlayerFromDepthChart("WR", MIKE_EVANS);

        assertEquals(List.of(), removed);
        assertEquals(
                Map.of(
                        Position.of("QB"), List.of(TOM_BRADY, BLAINE_GABBERT, KYLE_TRASK),
                        Position.of("LWR"), List.of(MIKE_EVANS, JAELON_DARDEN, SCOTT_MILLER)),
                service.getFullDepthChart());
    }

    @Test
    void removePlayerFromDepthChart_shouldDropPositionFromFullDepthChartWhenLastPlayerRemoved() {
        service.addPlayerToDepthChart("QB", TOM_BRADY, 0);

        List<Player> removed = service.removePlayerFromDepthChart("QB", TOM_BRADY);

        assertEquals(List.of(TOM_BRADY), removed);
        assertEquals(Map.of(), service.getFullDepthChart());
    }

    @Test
    void getFullDepthChart_shouldReturnEmptySnapshotWhenChartIsEmpty() {
        assertEquals(Map.of(), service.getFullDepthChart());
    }

    @Test
    void getFullDepthChart_shouldBeStableAcrossRepeatedReads() {
        addSampleDepthChart();

        Map<Position, List<Player>> first = service.getFullDepthChart();
        List<Player> ignored = service.getBackups("QB", TOM_BRADY);
        Map<Position, List<Player>> second = service.getFullDepthChart();
        Map<Position, List<Player>> third = service.getFullDepthChart();

        assertEquals(List.of(BLAINE_GABBERT, KYLE_TRASK), ignored);
        assertEquals(first, second);
        assertEquals(second, third);
    }

    @Test
    void addPlayerToDepthChart_shouldNormalizePositionInput() {
        service.addPlayerToDepthChart(" qb ", TOM_BRADY, 0);

        assertEquals(List.of(), service.getBackups("QB", TOM_BRADY));

        assertEquals(Map.of(Position.of("QB"), List.of(TOM_BRADY)), service.getFullDepthChart());
    }

    @Test
    void addPlayerToDepthChart_shouldRejectNullPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> service.addPlayerToDepthChart(null, TOM_BRADY, 0));
    }

    @Test
    void addPlayerToDepthChart_shouldRejectBlankPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> service.addPlayerToDepthChart("   ", TOM_BRADY, 0));
    }

    @Test
    void addPlayerToDepthChart_shouldRejectNullPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> service.addPlayerToDepthChart("QB", null, 0));
    }

    @Test
    void service_shouldSupportSamePlayerAcrossMultiplePositionsIndependently() {
        Player sharedBackup = new Player(72, "Shared Backup");
        Player leftTackleStarter = new Player(76, "Left Tackle Starter");
        Player rightTackleStarter = new Player(78, "Right Tackle Starter");

        service.addPlayerToDepthChart("LT", leftTackleStarter, 0);
        service.addPlayerToDepthChart("LT", sharedBackup, 1);
        service.addPlayerToDepthChart("RT", rightTackleStarter, 0);
        service.addPlayerToDepthChart("RT", sharedBackup, 1);

        assertEquals(List.of(sharedBackup), service.getBackups("LT", leftTackleStarter));
        assertEquals(List.of(sharedBackup), service.getBackups("RT", rightTackleStarter));

        List<Player> removedFromLt = service.removePlayerFromDepthChart("LT", sharedBackup);
        assertEquals(List.of(sharedBackup), removedFromLt);

        assertEquals(List.of(), service.getBackups("LT", leftTackleStarter));
        assertEquals(List.of(sharedBackup), service.getBackups("RT", rightTackleStarter));
    }

    @Test
    void getFullDepthChart_shouldKeepStablePositionAndDepthOrder() {
        service.addPlayerToDepthChart("LWR", MIKE_EVANS, 0);
        service.addPlayerToDepthChart("QB", TOM_BRADY, 0);
        service.addPlayerToDepthChart("QB", KYLE_TRASK, 1);
        service.addPlayerToDepthChart("QB", BLAINE_GABBERT, 1);
        service.addPlayerToDepthChart("LWR", SCOTT_MILLER, 1);
        service.addPlayerToDepthChart("LWR", JAELON_DARDEN, 1);

        String expected = String.join(System.lineSeparator(),
                "LWR - (#13, Mike Evans), (#1, Jaelon Darden), (#10, Scott Miller)",
                "QB - (#12, Tom Brady), (#11, Blaine Gabbert), (#2, Kyle Trask)");

        assertEquals(expected, new DepthChartFormatter().format(service.getFullDepthChart()));
    }
}

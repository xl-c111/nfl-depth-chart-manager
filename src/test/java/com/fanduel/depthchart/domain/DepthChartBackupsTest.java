package com.fanduel.depthchart.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.KYLE_TRASK;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class DepthChartBackupsTest {
    private DepthChart depthChart;
    private Position qb;

    @BeforeEach
    void setUp() {
        depthChart = new DepthChart();
        qb = Position.of("QB");
    }

    private void addQbDepthChart() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(qb, BLAINE_GABBERT, 1);
        depthChart.addPlayer(qb, KYLE_TRASK, 2);
    }

    @Test
    void getBackups_shouldReturnAllPlayersBelowHeadPlayer() {
        addQbDepthChart();
        List<Player> backups = depthChart.getBackups(qb, TOM_BRADY);

        assertEquals(List.of(BLAINE_GABBERT, KYLE_TRASK), backups);
    }

    @Test
    void getBackups_shouldReturnPlayersBelowMiddlePlayer() {
        addQbDepthChart();
        List<Player> backups = depthChart.getBackups(qb, BLAINE_GABBERT);

        assertEquals(List.of(KYLE_TRASK), backups);
    }

    @Test
    void getBackups_shouldReturnEmptyListWhenPlayerIsLast() {
        addQbDepthChart();
        List<Player> backups = depthChart.getBackups(qb, KYLE_TRASK);

        assertEquals(List.of(), backups);
    }

    @Test
    void getBackups_shouldReturnEmptyListWhenPlayerIsAbsent() {
        addQbDepthChart();
        List<Player> backups = depthChart.getBackups(qb, new Player(99, "Unknown Player"));

        assertEquals(List.of(), backups);
    }

    @Test
    void getBackups_shouldReturnEmptyListWhenPositionIsAbsent() {
        addQbDepthChart();
        Position rb = Position.of("RB");
        List<Player> backups = depthChart.getBackups(rb, TOM_BRADY);

        assertEquals(List.of(), backups);
    }
}

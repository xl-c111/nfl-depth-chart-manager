package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.KYLE_TRASK;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DepthChartRemoveTest {
    private DepthChart depthChart;
    private Position qb;

    @BeforeEach
    void setUp() {
        depthChart = new DepthChart();
        qb = Position.of("QB");
    }

    private List<Player> qbDepthChart() {
        return depthChart.snapshot().get(qb);
    }

    private void addQbDepthChart() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(qb, BLAINE_GABBERT, 1);
        depthChart.addPlayer(qb, KYLE_TRASK, 2);
    }

    @Test
    void removePlayer_shouldReturnRemovedPlayerAndCompactDepthChartWhenListed() {
        addQbDepthChart();

        List<Player> removed = depthChart.removePlayer(qb, BLAINE_GABBERT);

        assertEquals(List.of(BLAINE_GABBERT), removed);
        assertEquals(
                List.of(TOM_BRADY, KYLE_TRASK),
                qbDepthChart());
    }

    @Test
    void removePlayer_shouldReturnEmptyListWhenPlayerIsAbsent() {
        addQbDepthChart();

        List<Player> removed = depthChart.removePlayer(qb, new Player(99, "Unknown Player"));

        assertEquals(List.of(), removed);
        assertEquals(
                List.of(TOM_BRADY, BLAINE_GABBERT, KYLE_TRASK),
                qbDepthChart());
    }

    @Test
    void removePlayer_shouldReturnEmptyListWhenPositionIsAbsent() {
        Position rb = Position.of("RB");
        List<Player> removed = depthChart.removePlayer(rb, TOM_BRADY);

        assertEquals(List.of(), removed);
        assertNull(depthChart.snapshot().get(rb));
    }

    @Test
    void removePlayer_shouldRemovePositionWhenLastPlayerIsRemoved() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);

        List<Player> removed = depthChart.removePlayer(qb, TOM_BRADY);

        assertEquals(List.of(TOM_BRADY), removed);
        assertFalse(depthChart.snapshot().containsKey(qb));
    }

    @Test
    void removePlayer_shouldRejectNullPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.removePlayer(null, TOM_BRADY));
    }

    @Test
    void removePlayer_shouldRejectNullPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.removePlayer(qb, null));
    }
}

package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.KYLE_TRASK;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepthChartAddTest {
    private DepthChart depthChart;
    private Position qb;

    @BeforeEach
    // Arrange: setup happens in @BeforeEach
    void setUp() {
        depthChart = new DepthChart();
        qb = Position.of("QB");
    }

    // get the player list at the position qb
    private List<Player> qbDepthChart() {
        return depthChart.snapshot().get(qb);
    }

    @Test
    void addPlayer_shouldAppendWhenDepthIsNull() {
        depthChart.addPlayer(qb, TOM_BRADY, null);

        assertEquals(List.of(TOM_BRADY), qbDepthChart());
    }

    @Test
    void addPlayer_shouldInsertAtRequestedDepthAndShiftPlayersDown() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(qb, KYLE_TRASK, 1);

        depthChart.addPlayer(qb, BLAINE_GABBERT, 1);

        assertEquals(
                // List.of() creates an immutable list with the elements in the given order
                List.of(TOM_BRADY, BLAINE_GABBERT, KYLE_TRASK),
                qbDepthChart());
    }

    @Test
    void addPlayer_shouldAllowDepthEqualToCurrentSize() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(qb, BLAINE_GABBERT, 1);

        assertEquals(List.of(TOM_BRADY, BLAINE_GABBERT), qbDepthChart());
    }

    @Test
    // Test logic
    // Given: a new empty depth chart
    // When: I add Tom Brady at depth -1
    // Then: DepthChartValidationException should be thrown
    void addPlayer_shouldRejectNegativeDepth() {
        // assertThrows: expect this code throw an exception
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.addPlayer(qb, TOM_BRADY, -1));
    }

    @Test
    void addPlayer_shouldRejectDepthGreaterThanCurrentSize() {
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.addPlayer(qb, TOM_BRADY, 1));
    }

    @Test
    void addPlayer_shouldRejectNullPosition() {
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.addPlayer(null, TOM_BRADY, 0));
    }

    @Test
    void addPlayer_shouldRejectNullPlayer() {
        assertThrows(
                DepthChartValidationException.class,
                () -> depthChart.addPlayer(qb, null, 0));
    }

    @Test
    void addPlayer_shouldRepositionExistingPlayerAtSamePosition() {
        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(qb, BLAINE_GABBERT, 1);
        depthChart.addPlayer(qb, TOM_BRADY, 1);

        assertEquals(List.of(BLAINE_GABBERT, TOM_BRADY), qbDepthChart());
    }

    @Test
    void addPlayer_shouldTreatSameJerseyNumberIndependentlyAcrossPositions() {
        Position lwr = Position.of("LWR");
        Player numberTwelveWideReceiver = new Player(12, "Different #12");

        depthChart.addPlayer(qb, TOM_BRADY, 0);
        depthChart.addPlayer(lwr, numberTwelveWideReceiver, 0);

        assertEquals(List.of(TOM_BRADY), depthChart.snapshot().get(qb));
        assertEquals(List.of(numberTwelveWideReceiver), depthChart.snapshot().get(lwr));
    }
}

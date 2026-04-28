package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryDepthChartServiceTest {

    @Test
    void addPlayerToDepthChart_shouldThrowUntilDomainLogicIsImplemented() {
        DepthChartService service = new InMemoryDepthChartService();

        assertThrows(
                UnsupportedOperationException.class,
                () -> service.addPlayerToDepthChart("QB", new Player(12, "Tom Brady"), 0)
        );
    }
}

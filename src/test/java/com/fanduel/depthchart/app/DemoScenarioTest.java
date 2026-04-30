package com.fanduel.depthchart.app;

import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoScenarioTest {

    @Test
    void run_shouldPrintKeySampleSections() {
        DepthChartService service = new InMemoryDepthChartService();
        DemoScenario scenario = new DemoScenario(service);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            scenario.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Backups for Tom Brady at QB:"));
        assertTrue(output.contains("#11 - Blaine Gabbert"));
        assertTrue(output.contains("Full depth chart:"));
        assertTrue(output.contains("Removed from LWR:"));
        assertTrue(output.contains("Full depth chart after removal:"));
    }
}

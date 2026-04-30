package com.fanduel.depthchart.app;

import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void run_shouldThrowWhenSampleFileIsMissing() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/does-not-exist.json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, scenario::run);
        assertTrue(exception.getMessage().contains("sample data file not found"));
    }

    @Test
    void run_shouldThrowWhenRootIsNotArray() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/invalid-root-not-array.json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, scenario::run);
        assertTrue(exception.getMessage().contains("sample data must be a JSON array"));
    }

    @Test
    void run_shouldThrowWhenRowIsNotObject() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/invalid-row-not-object.json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, scenario::run);
        assertTrue(exception.getMessage().contains("sample row must be a JSON object"));
    }

    @Test
    void run_shouldThrowWhenRequiredFieldsAreMissing() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/missing-required-field.json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, scenario::run);
        assertTrue(exception.getMessage().contains("sample row missing required fields"));
    }

    @Test
    void run_shouldThrowWhenNumberTypeIsInvalid() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/invalid-number-type.json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, scenario::run);
        assertTrue(exception.getMessage().contains("failed to load sample depth chart data"));
    }

    @Test
    void run_shouldHandleNullDepthAsAppendAndExplicitDepthAsInsert() {
        DemoScenario scenario = new DemoScenario(
                new InMemoryDepthChartService(),
                new com.fanduel.depthchart.formatter.DepthChartFormatter(),
                "data/valid-depth-null-and-explicit.json");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            scenario.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("QB - (#12, Tom Brady), (#11, Blaine Gabbert), (#2, Kyle Trask)"));
    }

    @Test
    void run_shouldPrintExactPlayerListFormatsForEmptyAndNonEmptyCases() {
        DemoScenario scenario = new DemoScenario(new InMemoryDepthChartService());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            scenario.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Backups for Tom Brady at QB:\n#11 - Blaine Gabbert\n#2 - Kyle Trask\n"));
        assertTrue(output.contains("Backups for Mike Evans at QB:\n<NO LIST>"));
    }
}

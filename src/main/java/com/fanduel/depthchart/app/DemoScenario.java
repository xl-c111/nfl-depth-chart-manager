package com.fanduel.depthchart.app;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.formatter.DepthChartFormatter;
import com.fanduel.depthchart.service.DepthChartService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo scenario runner for the take-home sample flow.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public class DemoScenario {
    private static final String DEFAULT_SAMPLE_DATA_PATH = "data/tb-depth-chart-sample.json";

    private final DepthChartService service;
    private final DepthChartFormatter formatter;
    private final String sampleDataPath;

    /**
     * Constructs a demo scenario runner.
     *
     * @param service depth chart service used by the demo flow
     */
    public DemoScenario(DepthChartService service) {
        this(service, new DepthChartFormatter(), DEFAULT_SAMPLE_DATA_PATH);
    }

    /**
     * Constructs a demo scenario runner with explicit formatter.
     *
     * @param service   depth chart service used by the demo flow
     * @param formatter formatter for full depth chart output
     */
    public DemoScenario(DepthChartService service, DepthChartFormatter formatter) {
        this(service, formatter, DEFAULT_SAMPLE_DATA_PATH);
    }

    /**
     * Constructs a demo scenario runner with explicit formatter and sample data
     * path.
     *
     * @param service        depth chart service used by the demo flow
     * @param formatter      formatter for full depth chart output
     * @param sampleDataPath classpath location of sample depth chart JSON
     */
    public DemoScenario(DepthChartService service, DepthChartFormatter formatter, String sampleDataPath) {
        this.service = service;
        this.formatter = formatter;
        this.sampleDataPath = sampleDataPath;
    }

    /**
     * Runs the full sample scenario from the challenge prompt.
     * Step by step:
     * 1) load sample rows from JSON into the in-memory service
     * 2) build a few Player query objects
     * 3) print backup results for multiple player/position cases
     * 4) print full depth chart, remove one player, then print again
     */
    public void runDemoScenario() {
        // Read bundled JSON and populate the depth chart before any queries.
        seedSampleDepthChartFromJson();

        // Create player objects used for backup lookups and removal examples.
        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");
        Player kyleTrask = new Player(2, "Kyle Trask");
        Player mikeEvans = new Player(13, "Mike Evans");
        Player jaelonDarden = new Player(1, "Jaelon Darden");

        // Show backup lists for normal and edge cases from the prompt.
        printBackups("Tom Brady", "QB", tomBrady);
        printBackups("Jaelon Darden", "LWR", jaelonDarden);
        printBackups("Jaelon Darden", "QB", jaelonDarden);
        printBackups("Mike Evans", "QB", mikeEvans);
        printBackups("Blaine Gabbert", "QB", blaineGabbert);
        printBackups("Kyle Trask", "QB", kyleTrask);

        // Print a snapshot of the full depth chart.
        System.out.println();
        System.out.println("Full depth chart:");
        System.out.println(formatter.format(service.getFullDepthChart()));

        // Remove one player from LWR and print what was removed.
        System.out.println();
        System.out.println("Removed from LWR:");
        System.out.println(formatPlayerList(service.removePlayerFromDepthChart("LWR", mikeEvans)));

        // Print the full chart again so the removal effect is visible.
        System.out.println();
        System.out.println("Full depth chart after removal:");
        System.out.println(formatter.format(service.getFullDepthChart()));
    }

    /**
     * Loads the demo depth chart rows from the bundled JSON file and seeds the
     * service.
     * This method handles resource loading, parsing, and inserting rows one by one.
     */
    private void seedSampleDepthChartFromJson() {
        // Open sample JSON from classpath using the configured path.
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(sampleDataPath)) {
            // Fail fast if the resource path is wrong or file is missing.
            if (input == null) {
                throw new IllegalStateException("sample data file not found: " + sampleDataPath);
            }

            // Parse JSON rows into typed row objects first.
            List<DepthChartRow> rows = parseRows(input);
            // Insert each parsed row into the service.
            for (DepthChartRow row : rows) {
                service.addPlayerToDepthChart(
                        row.position(),
                        new Player(row.number(), row.name()),
                        row.depth());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("failed to load sample depth chart data", exception);
        }
    }

    /**
     * Parses depth chart rows from a JSON array input stream.(handle the full list)
     *
     * @param input JSON input stream containing an array of row objects
     * @return parsed depth chart rows
     * @throws IOException if parsing fails
     */
    private List<DepthChartRow> parseRows(InputStream input) throws IOException {
        // Create a Jackson factory to build a streaming parser.
        JsonFactory factory = new JsonFactory();
        // Collect parsed rows in insertion order.
        List<DepthChartRow> rows = new ArrayList<>();
        try (JsonParser parser = factory.createParser(input)) {
            // The root JSON must be an array: [ {...}, {...} ].
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("sample data must be a JSON array");
            }

            // Read objects until we hit the end of the array.
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                rows.add(parseRow(parser));
            }
        }
        return rows;
    }

    /**
     * Parses a single depth chart row object.(handles one object)
     *
     * @param parser JSON parser positioned at {@code START_OBJECT}
     * @return parsed depth chart row
     * @throws IOException if parsing fails
     */
    private DepthChartRow parseRow(JsonParser parser) throws IOException {
        // Each row must be a JSON object: {"number":...,"name":...,...}.
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("sample row must be a JSON object");
        }

        // Temporary holders while reading fields from the object.
        Integer number = null;
        String name = null;
        String position = null;
        Integer depth = null;

        // Iterate through all fields in this row object.
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            // Move from field name token to field value token.
            parser.nextToken();

            // Read known fields and ignore unknown fields safely.
            if ("number".equals(fieldName)) {
                number = parser.getIntValue();
            } else if ("name".equals(fieldName)) {
                name = parser.getValueAsString();
            } else if ("position".equals(fieldName)) {
                position = parser.getValueAsString();
            } else if ("depth".equals(fieldName)) {
                // Allow null depth to represent "append to end".
                depth = parser.currentToken() == JsonToken.VALUE_NULL ? null : parser.getIntValue();
            } else {
                // Skip nested content for unsupported/extra fields.
                parser.skipChildren();
            }
        }

        // Validate required fields before creating the immutable row record.
        if (number == null || name == null || position == null) {
            throw new IllegalStateException("sample row missing required fields: number, name, position");
        }
        return new DepthChartRow(number, name, position, depth);
    }

    /**
     * Represents one depth chart input row.(holds the parsed data for that single
     * row)
     *
     * @param number   jersey number
     * @param name     player name
     * @param position position code
     * @param depth    position depth (null means append)
     */
    // Records automatically generates constructor, getter, equals(), hashCde(),
    // toString(), lightweight data carrier
    private record DepthChartRow(int number, String name, String position, Integer depth) {

    }

    // private static final class DepthChartRow {
    // private final int number;
    // private final String name;
    // private final String position;
    // private final Integer depth;

    // private DepthChartRow(int number, String name, String position, Integer
    // depth) {
    // this.number = number;
    // this.name = name;
    // this.position = position;
    // this.depth = depth;
    // }

    // int number() {
    // return number;
    // }

    // String name() {
    // return name;
    // }

    // String position() {
    // return position;
    // }

    // Integer depth() {
    // return depth;
    // }

    // @Override
    // public boolean equals(Object other) {
    // if (this == other) {
    // return true;
    // }
    // if (!(other instanceof DepthChartRow that)) {
    // return false;
    // }
    // return number == that.number
    // && java.util.Objects.equals(name, that.name)
    // && java.util.Objects.equals(position, that.position)
    // && java.util.Objects.equals(depth, that.depth);
    // }

    // @Override
    // public int hashCode() {
    // return java.util.Objects.hash(number, name, position, depth);
    // }

    // @Override
    // public String toString() {
    // return "DepthChartRow[number=" + number
    // + ", name=" + name
    // + ", position=" + position
    // + ", depth=" + depth + "]";
    // }
    // }

    /**
     * Prints backups for a player at a position.
     *
     * @param playerName display name for output
     * @param position   position code
     * @param player     player to query
     */
    private void printBackups(String playerName, String position, Player player) {
        // Add spacing for readable console output.
        System.out.println();
        // Print a title line for this specific query.
        System.out.println("Backups for " + playerName + " at " + position + ":");
        // Query service and print result in friendly line format.
        System.out.println(formatPlayerList(service.getBackups(position, player)));
    }

    /**
     * Formats a player list for demo output.
     *
     * @param players players to format
     * @return formatted players or {@code <NO LIST>} when empty
     */
    private String formatPlayerList(List<Player> players) {
        // Match prompt wording when no backups or no removed player exists.
        if (players.isEmpty()) {
            return "<NO LIST>";
        }
        // Build multi-line output: "#12 - Tom Brady".
        StringBuilder result = new StringBuilder();

        // Add one output line per player in list order.
        for (Player player : players) {
            result.append("#")
                    .append(player.getNumber())
                    .append(" - ")
                    .append(player.getName())
                    .append(System.lineSeparator());
        }

        return result.toString();
    }
}

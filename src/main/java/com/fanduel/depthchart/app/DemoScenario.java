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
     * @param service depth chart service used by the demo flow
     * @param formatter formatter for full depth chart output
     */
    public DemoScenario(DepthChartService service, DepthChartFormatter formatter) {
        this(service, formatter, DEFAULT_SAMPLE_DATA_PATH);
    }

    /**
     * Constructs a demo scenario runner with explicit formatter and sample data path.
     *
     * @param service depth chart service used by the demo flow
     * @param formatter formatter for full depth chart output
     * @param sampleDataPath classpath location of sample depth chart JSON
     */
    public DemoScenario(DepthChartService service, DepthChartFormatter formatter, String sampleDataPath) {
        this.service = service;
        this.formatter = formatter;
        this.sampleDataPath = sampleDataPath;
    }

    /**
     * Runs the full sample scenario from the challenge prompt.
     */
    public void run() {
        seedSampleDepthChartFromJson();

        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");
        Player kyleTrask = new Player(2, "Kyle Trask");
        Player mikeEvans = new Player(13, "Mike Evans");
        Player jaelonDarden = new Player(1, "Jaelon Darden");

        printBackups("Tom Brady", "QB", tomBrady);
        printBackups("Jaelon Darden", "LWR", jaelonDarden);
        printBackups("Jaelon Darden", "QB", jaelonDarden);
        printBackups("Mike Evans", "QB", mikeEvans);
        printBackups("Blaine Gabbert", "QB", blaineGabbert);
        printBackups("Kyle Trask", "QB", kyleTrask);

        System.out.println();
        System.out.println("Full depth chart:");
        System.out.println(formatter.format(service.getFullDepthChart()));

        System.out.println();
        System.out.println("Removed from LWR:");
        System.out.println(formatPlayerList(service.removePlayerFromDepthChart("LWR", mikeEvans)));

        System.out.println();
        System.out.println("Full depth chart after removal:");
        System.out.println(formatter.format(service.getFullDepthChart()));
    }

    /**
     * Loads the demo depth chart rows from the bundled JSON file and seeds the service.
     */
    private void seedSampleDepthChartFromJson() {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(sampleDataPath)) {
            if (input == null) {
                throw new IllegalStateException("sample data file not found: " + sampleDataPath);
            }

            List<DepthChartRow> rows = parseRows(input);
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
     * Parses depth chart rows from a JSON array input stream.
     *
     * @param input JSON input stream containing an array of row objects
     * @return parsed depth chart rows
     * @throws IOException if parsing fails
     */
    private List<DepthChartRow> parseRows(InputStream input) throws IOException {
        JsonFactory factory = new JsonFactory();
        List<DepthChartRow> rows = new ArrayList<>();
        try (JsonParser parser = factory.createParser(input)) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("sample data must be a JSON array");
            }

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                rows.add(parseRow(parser));
            }
        }
        return rows;
    }

    /**
     * Parses a single depth chart row object.
     *
     * @param parser JSON parser positioned at {@code START_OBJECT}
     * @return parsed depth chart row
     * @throws IOException if parsing fails
     */
    private DepthChartRow parseRow(JsonParser parser) throws IOException {
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("sample row must be a JSON object");
        }

        Integer number = null;
        String name = null;
        String position = null;
        Integer depth = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();

            if ("number".equals(fieldName)) {
                number = parser.getIntValue();
            } else if ("name".equals(fieldName)) {
                name = parser.getValueAsString();
            } else if ("position".equals(fieldName)) {
                position = parser.getValueAsString();
            } else if ("depth".equals(fieldName)) {
                depth = parser.currentToken() == JsonToken.VALUE_NULL ? null : parser.getIntValue();
            } else {
                parser.skipChildren();
            }
        }

        if (number == null || name == null || position == null) {
            throw new IllegalStateException("sample row missing required fields: number, name, position");
        }
        return new DepthChartRow(number, name, position, depth);
    }

    /**
     * Represents one depth chart input row.
     *
     * @param number jersey number
     * @param name player name
     * @param position position code
     * @param depth position depth (null means append)
     */
    private record DepthChartRow(int number, String name, String position, Integer depth) {
    }

    /**
     * Prints backups for a player at a position.
     *
     * @param playerName display name for output
     * @param position position code
     * @param player player to query
     */
    private void printBackups(String playerName, String position, Player player) {
        System.out.println();
        System.out.println("Backups for " + playerName + " at " + position + ":");
        System.out.println(formatPlayerList(service.getBackups(position, player)));
    }

    /**
     * Formats a player list for demo output.
     *
     * @param players players to format
     * @return formatted players or {@code <NO LIST>} when empty
     */
    private String formatPlayerList(List<Player> players) {
        if (players.isEmpty()) {
            return "<NO LIST>";
        }
        StringBuilder result = new StringBuilder();

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

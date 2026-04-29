package com.fanduel.depthchart.app;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.service.DepthChartService;
import java.util.List;

/**
 * Demo scenario runner for the take-home sample flow.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public class DemoScenario {
    private final DepthChartService service;

    /**
     * Constructs a demo scenario runner.
     *
     * @param service depth chart service used by the demo flow
     */
    public DemoScenario(DepthChartService service) {
        this.service = service;
    }

    /**
     * Runs the full sample scenario from the challenge prompt.
     */
    public void run() {
        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");
        Player kyleTrask = new Player(2, "Kyle Trask");
        Player mikeEvans = new Player(13, "Mike Evans");
        Player jaelonDarden = new Player(1, "Jaelon Darden");
        Player scottMiller = new Player(10, "Scott Miller");

        seedSampleDepthChart(
                tomBrady,
                blaineGabbert,
                kyleTrask,
                mikeEvans,
                jaelonDarden,
                scottMiller);

        printBackups("Tom Brady", "QB", tomBrady);
        printBackups("Jaelon Darden", "LWR", jaelonDarden);
        printBackups("Jaelon Darden", "QB", jaelonDarden);
        printBackups("Mike Evans", "QB", mikeEvans);
        printBackups("Blaine Gabbert", "QB", blaineGabbert);
        printBackups("Kyle Trask", "QB", kyleTrask);

        System.out.println();
        System.out.println("Full depth chart:");
        System.out.println(service.getFullDepthChart());

        System.out.println();
        System.out.println("Removed from LWR:");
        System.out.println(formatPlayerList(service.removePlayerFromDepthChart("LWR", mikeEvans)));

        System.out.println();
        System.out.println("Full depth chart after removal:");
        System.out.println(service.getFullDepthChart());
    }

    private void seedSampleDepthChart(
            Player tomBrady,
            Player blaineGabbert,
            Player kyleTrask,
            Player mikeEvans,
            Player jaelonDarden,
            Player scottMiller) {
        service.addPlayerToDepthChart("QB", tomBrady, 0);
        service.addPlayerToDepthChart("QB", blaineGabbert, 1);
        service.addPlayerToDepthChart("QB", kyleTrask, 2);

        service.addPlayerToDepthChart("LWR", mikeEvans, 0);
        service.addPlayerToDepthChart("LWR", jaelonDarden, 1);
        service.addPlayerToDepthChart("LWR", scottMiller, 2);
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

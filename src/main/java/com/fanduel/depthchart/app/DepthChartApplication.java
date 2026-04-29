package com.fanduel.depthchart.app;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;
import java.util.List;

public final class DepthChartApplication {

    private DepthChartApplication() {
    }

    public static void main(String[] args) {
        DepthChartService service = new InMemoryDepthChartService();

        Player tomBrady = new Player(12, "Tom Brady");
        Player blaineGabbert = new Player(11, "Blaine Gabbert");
        Player kyleTrask = new Player(2, "Kyle Trask");
        Player mikeEvans = new Player(13, "Mike Evans");
        Player jaelonDarden = new Player(1, "Jaelon Darden");
        Player scottMiller = new Player(10, "Scott Miller");

        service.addPlayerToDepthChart("QB", tomBrady, 0);
        service.addPlayerToDepthChart("QB", blaineGabbert, 1);
        service.addPlayerToDepthChart("QB", kyleTrask, 2);

        service.addPlayerToDepthChart("LWR", mikeEvans, 0);
        service.addPlayerToDepthChart("LWR", jaelonDarden, 1);
        service.addPlayerToDepthChart("LWR", scottMiller, 2);

        System.out.println();
        System.out.println("Backups for Tom Brady at QB:");
        System.out.println(formatPlayerList(service.getBackups("QB", tomBrady)));

        System.out.println();
        System.out.println("Backups for Jaelon Darden at LWR:");
        System.out.println(formatPlayerList(service.getBackups("LWR", jaelonDarden)));

        System.out.println();
        System.out.println("Backups for Jaelon Darden at QB:");
        System.out.println(formatPlayerList(service.getBackups("QB", jaelonDarden)));

        System.out.println();
        System.out.println("Backups for Mike Evans at QB:");
        System.out.println(formatPlayerList(service.getBackups("QB", mikeEvans)));

        System.out.println();
        System.out.println("Backups for Blaine Gabbert at QB:");
        System.out.println(formatPlayerList(service.getBackups("QB", blaineGabbert)));

        System.out.println();
        System.out.println("Backups for Kyle Trask at QB:");
        System.out.println(formatPlayerList(service.getBackups("QB", kyleTrask)));

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

    private static String formatPlayerList(List<Player> players) {
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

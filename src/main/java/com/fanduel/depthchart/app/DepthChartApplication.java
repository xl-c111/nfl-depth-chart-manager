package com.fanduel.depthchart.app;

import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;
import com.fanduel.depthchart.domain.Player;

public final class DepthChartApplication {

    private DepthChartApplication() {
    }

    public static void main(String[] args) {
        DepthChartService service = new InMemoryDepthChartService();
        Player tomBrady = new Player(12, "Tom Brady");

        try {
            service.addPlayerToDepthChart("QB", tomBrady, 0);
        } catch (UnsupportedOperationException ignored) {
            System.out.println("DepthChart skeleton initialized. Core add/remove/backups logic pending.");
        }
    }
}

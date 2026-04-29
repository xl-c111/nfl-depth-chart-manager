package com.fanduel.depthchart.app;

import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;

public final class DepthChartApplication {

    private DepthChartApplication() {
    }

    public static void main(String[] args) {
        DepthChartService service = new InMemoryDepthChartService();
        new DemoScenario(service).run();
    }
}

package com.fanduel.depthchart.app;

import com.fanduel.depthchart.service.DepthChartService;
import com.fanduel.depthchart.service.InMemoryDepthChartService;

/**
 * Application entrypoint for running the depth chart demo flow.
 *
 * @author Xiaoling Cui
 * @version 2.0
 */
public final class DepthChartApplication {

    /**
     * Constructs a DepthChartApplication.
     */
    private DepthChartApplication() {
    }

    /**
     * Starts the application and runs the demo scenario.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DepthChartService service = new InMemoryDepthChartService();
        new DemoScenario(service).run();
    }
}

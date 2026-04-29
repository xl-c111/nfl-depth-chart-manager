package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.DepthChart;
import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import com.fanduel.depthchart.formatter.DepthChartFormatter;
import java.util.List;
import java.util.Objects;

/** In-memory service for NFL depth chart use cases. */
public class InMemoryDepthChartService implements DepthChartService {
    private final DepthChart depthChart;
    private final DepthChartFormatter formatter;

    public InMemoryDepthChartService() {
        this(new DepthChart(), new DepthChartFormatter());
    }

    public InMemoryDepthChartService(DepthChart depthChart, DepthChartFormatter formatter) {
        // Fail fast on null dependencies
        this.depthChart = Objects.requireNonNull(depthChart, "depthChart must not be null");
        this.formatter = Objects.requireNonNull(formatter, "formatter must not be null");
    }

    @Override
    public void addPlayerToDepthChart(String position, Player player, Integer positionDepth) {
        depthChart.addPlayer(Position.of(position), player, positionDepth);
    }

    @Override
    public List<Player> removePlayerFromDepthChart(String position, Player player) {
        return depthChart.removePlayer(Position.of(position), player);
    }

    @Override
    public List<Player> getBackups(String position, Player player) {
        return depthChart.getBackups(Position.of(position), player);
    }

    @Override
    public String getFullDepthChart() {
        return formatter.format(depthChart.snapshot());
    }
}

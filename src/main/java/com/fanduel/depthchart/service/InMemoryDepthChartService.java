package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.DepthChart;
import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory implementation of depth chart service operations.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public class InMemoryDepthChartService implements DepthChartService {
    private final DepthChart depthChart;

    /**
     * Constructs an in-memory service with default dependencies.
     */
    public InMemoryDepthChartService() {
        this(new DepthChart());
    }

    /**
     * Constructs an in-memory service with injected dependencies.
     *
     * @param depthChart depth chart aggregate
     */
    // Dependency injection: class receives its dependencies from outside, without
    // creating them internally
    // InMemoryDepthChartService depends on DepthChart, instead of creating a new
    // DepthChart itself, the DepthChart is passed into the constructor.
    public InMemoryDepthChartService(DepthChart depthChart) {
        // Fail fast on null dependencies
        this.depthChart = Objects.requireNonNull(depthChart, "depthChart must not be null");
    }

    /**
     * Adds a player to a position at a target depth.
     *
     * @param position      position code
     * @param player        player to add
     * @param positionDepth target depth (null means append)
     */
    @Override
    public void addPlayerToDepthChart(String position, Player player, Integer positionDepth) {
        depthChart.addPlayer(Position.of(position), player, positionDepth);
    }

    /**
     * Removes a player from a position.
     *
     * @param position position code
     * @param player   player to remove
     * @return a single-element list {@code [player]} when removed, or an empty list
     *         {@code []} when not listed at that position
     */
    @Override
    public List<Player> removePlayerFromDepthChart(String position, Player player) {
        return depthChart.removePlayer(Position.of(position), player);
    }

    /**
     * Returns backups behind a player at a position.
     *
     * @param position position code
     * @param player   reference player
     * @return backups behind the given player
     */
    @Override
    public List<Player> getBackups(String position, Player player) {
        return depthChart.getBackups(Position.of(position), player);
    }

    /**
     * Returns the full depth chart as structured snapshot.
     *
     * @return full depth chart by position
     */
    @Override
    public Map<Position, List<Player>> getFullDepthChart() {
        return depthChart.snapshot();
    }
}

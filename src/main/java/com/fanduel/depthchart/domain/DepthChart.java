package com.fanduel.depthchart.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Aggregate root for depth chart rules and position-based player ordering.
 *
 * @author Xiaoling Cui
 * @version 3.0
 */
public class DepthChart {
    private final Map<Position, List<Player>> chart = new LinkedHashMap<>();

    /**
     * Adds a player at a given position and depth.
     *
     * @param position target position
     * @param player   player to add
     * @param depth    target depth (null means append)
     * @throws DepthChartValidationException if inputs are invalid or depth is out
     *                                       of range
     */
    public void addPlayer(Position position, Player player, Integer depth) {
        validateRequiredInputs(position, player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            playerAtPosition = new ArrayList<>();
            chart.put(position, playerAtPosition);
        }

        // treat re-add as reposition
        playerAtPosition.remove(player);

        if (depth == null) {
            playerAtPosition.add(player);
            return;
        }
        if (depth < 0 || depth > playerAtPosition.size()) {
            throw new DepthChartValidationException(
                    "depth must be between 0 and current size for position " + position);
        }

        playerAtPosition.add(depth, player);

    }

    /**
     * Removes a player from a position.
     *
     * @param position target position
     * @param player   player to remove
     * @return a single-element list when removed, or empty list when absent
     * @throws DepthChartValidationException if required inputs are null
     */
    public List<Player> removePlayer(Position position, Player player) {
        validateRequiredInputs(position, player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            return List.of();
        }

        boolean removed = playerAtPosition.remove(player);
        if (!removed) {
            return List.of();
        }

        // Keep only positions that currently have players on the depth chart
        if (playerAtPosition.isEmpty()) {
            chart.remove(position);
        }
        // Return the removed player only when it was actually listed at this position
        return List.of(player);

    }

    /**
     * Returns all players behind the given player at a position.
     *
     * @param position target position
     * @param player   reference player
     * @return players with lower priority depth, or empty list when none
     * @throws DepthChartValidationException if required inputs are null
     */
    public List<Player> getBackups(Position position, Player player) {
        validateRequiredInputs(position, player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            return List.of();
        }

        int playerDepth = playerAtPosition.indexOf(player);
        // return empty list for both cases: player not found; player has no backups
        if (playerDepth == -1 || playerDepth == playerAtPosition.size() - 1) {
            return List.of();
        }

        return List.copyOf(playerAtPosition.subList(playerDepth + 1, playerAtPosition.size()));
    }

    private void validateRequiredInputs(Position position, Player player) {
        if (position == null) {
            throw new DepthChartValidationException("position must not be null");
        }
        if (player == null) {
            throw new DepthChartValidationException("player must not be null");
        }
    }

    /**
     * Returns an immutable snapshot of the full depth chart.
     *
     * @return unmodifiable map of positions to unmodifiable player lists
     */
    public Map<Position, List<Player>> snapshot() {
        Map<Position, List<Player>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<Position, List<Player>> entry : chart.entrySet()) {
            snapshot.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(snapshot);
    }
}

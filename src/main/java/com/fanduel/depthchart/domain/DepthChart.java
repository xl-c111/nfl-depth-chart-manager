package com.fanduel.depthchart.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Aggregate root for depth chart rules.
 *
 * Implementation intentionally deferred while project skeleton is being
 * finalized.
 */
public class DepthChart {
    private final Map<Position, List<Player>> chart = new LinkedHashMap<>();

    public void addPlayer(Position position, Player player, Integer depth) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(player, "player must not be null");

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

    public List<Player> removePlayer(Position position, Player player) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(player, "player must not be null");

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            return List.of();
        }

        boolean removed = playerAtPosition.remove(player);
        if (!removed) {
            return List.of();
        }
        // Return the removed player only when it was actually listed at this position.
        return List.of(player);

    }

    public List<Player> getBackups(Position position, Player player) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(player, "player must not be null");

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

    public Map<Position, List<Player>> snapshot() {
        Map<Position, List<Player>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<Position, List<Player>> entry : chart.entrySet()) {
            snapshot.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(snapshot);
    }
}

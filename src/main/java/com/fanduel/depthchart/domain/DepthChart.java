package com.fanduel.depthchart.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Aggregate root for depth chart rules and position-based player ordering.
 *
 * <p>
 * This in-memory aggregate uses coarse synchronized method-level locking for
 * single-JVM consistency.
 * </p>
 *
 * @author Xiaoling Cui
 * @version 3.0
 */
public class DepthChart {
    private final Map<Position, List<Player>> chart = new LinkedHashMap<>();

    /**
     * Adds or repositions a player at a given position and depth.
     *
     * <p>
     * Inserting at a depth shifts existing players at and below that depth down.
     * Re-adding an existing player at the same position repositions the player.
     * </p>
     *
     * @param position target position
     * @param player   player to add or reposition
     * @param depth    target depth; {@code null} means append
     * @throws DepthChartValidationException if inputs are invalid or depth is out
     *                                       of range
     */
    public synchronized void addPlayer(Position position, Player player, Integer depth) {
        validateRequiredInputs(position, player);
        validatePlayerIdentityConsistency(player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            playerAtPosition = new ArrayList<>();
            chart.put(position, playerAtPosition);
        }

        // Validate requested depth against the pre-change list size.
        int originalSize = playerAtPosition.size();
        // Re-adding the same player at this position means repositioning, not
        // duplicating.
        boolean alreadyExists = playerAtPosition.contains(player);

        if (depth == null) {
            // Null depth means append; for an existing player this becomes "move to end".
            if (alreadyExists) {
                playerAtPosition.remove(player);
            }
            playerAtPosition.add(player);
            return;
        }
        if (depth < 0 || depth > originalSize) {
            throw new DepthChartValidationException(
                    "depth must be between 0 and current size for position " + position);
        }

        if (alreadyExists) {
            playerAtPosition.remove(player);
            // If depth equals original size, removal shrinks the list by one; clamp to tail
            // index.
            depth = Math.min(depth, playerAtPosition.size());
        }

        playerAtPosition.add(depth, player);
    }

    /**
     * Ensures the same jersey number is not used with different player names.
     *
     * @param candidate player being inserted or repositioned
     * @throws DepthChartValidationException if the number already exists with a
     *                                       different name
     */
    private void validatePlayerIdentityConsistency(Player candidate) {
        for (List<Player> players : chart.values()) {
            for (Player existing : players) {
                if (existing.getNumber() == candidate.getNumber()
                        && !namesEquivalent(existing.getName(), candidate.getName())) {
                    throw new DepthChartValidationException(
                            "player number " + candidate.getNumber()
                                    + " already exists with a different name in this team context");
                }
            }
        }
    }

    private boolean namesEquivalent(String left, String right) {
        return left.toLowerCase(Locale.ROOT).equals(right.toLowerCase(Locale.ROOT));
    }

    /**
     * Removes a player from a position.
     *
     * @param position target position
     * @param player   player to remove
     * @return a single-element list when removed, or empty list when absent at
     *         this position
     * @throws DepthChartValidationException if required inputs are null
     */
    public synchronized List<Player> removePlayer(Position position, Player player) {
        validateRequiredInputs(position, player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            return List.of();
        }

        int removedIndex = playerAtPosition.indexOf(player);
        if (removedIndex < 0) {
            return List.of();
        }

        // Remove by index so we return the player stored in the chart,
        // not the player object passed in by the caller.
        Player removedPlayer = playerAtPosition.remove(removedIndex);

        // Keep snapshots compact by removing empty positions entirely.
        if (playerAtPosition.isEmpty()) {
            chart.remove(position);
        }
        // Return the removed player only when it was actually listed at this position
        return List.of(removedPlayer);
    }

    /**
     * Returns all players behind the given player at a position.
     *
     * @param position target position
     * @param player   reference player
     * @return players with lower priority depth, or empty list when none or when
     *         the player is not listed at this position
     * @throws DepthChartValidationException if required inputs are null
     */
    public synchronized List<Player> getBackups(Position position, Player player) {
        validateRequiredInputs(position, player);

        List<Player> playerAtPosition = chart.get(position);
        if (playerAtPosition == null) {
            return List.of();
        }

        int playerDepth = playerAtPosition.indexOf(player);
        // Contract: both "not listed" and "no players behind" return an empty list.
        if (playerDepth == -1 || playerDepth == playerAtPosition.size() - 1) {
            return List.of();
        }

        return List.copyOf(playerAtPosition.subList(playerDepth + 1, playerAtPosition.size()));
    }

    /**
     * Validates required aggregate method inputs.
     *
     * @param position target position
     * @param player   target player
     * @throws DepthChartValidationException when either argument is null
     */
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
     * @return unmodifiable map of positions to unmodifiable player lists; position
     *         iteration order is preserved
     */
    public synchronized Map<Position, List<Player>> snapshot() {
        Map<Position, List<Player>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<Position, List<Player>> entry : chart.entrySet()) {
            snapshot.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(snapshot);
    }
}

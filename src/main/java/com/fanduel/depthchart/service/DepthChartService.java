package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import java.util.List;
import java.util.Map;

/**
 * Service contract for depth chart operations.
 * Implementations manage a single team depth chart context.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public interface DepthChartService {

    /**
     * Adds a player to the depth chart at a position and depth.
     *
     * @param position      the position code
     * @param player        the player to add
     * @param positionDepth the target depth (null means append)
     * @throws com.fanduel.depthchart.exception.DepthChartValidationException
     *         if position/player/depth is invalid
     */
    void addPlayerToDepthChart(String position, Player player, Integer positionDepth);

    /**
     * Removes a player from a position in the depth chart.
     *
     * @param position the position code
     * @param player   the player to remove
     * @return a single-element list {@code [player]} when removed, or an empty list {@code []} when not listed at that position
     * @throws com.fanduel.depthchart.exception.DepthChartValidationException
     *         if position or player is invalid
     */
    List<Player> removePlayerFromDepthChart(String position, Player player);

    /**
     * Gets backup players behind a given player at a position.
     *
     * @param position the position code
     * @param player   the reference player
     * @return the backup players behind the given player
     * @throws com.fanduel.depthchart.exception.DepthChartValidationException
     *         if position or player is invalid
     */
    List<Player> getBackups(String position, Player player);

    /**
     * Gets the full depth chart as a structured immutable snapshot.
     *
     * @return full depth chart by position as an immutable snapshot
     */
    Map<Position, List<Player>> getFullDepthChart();
}

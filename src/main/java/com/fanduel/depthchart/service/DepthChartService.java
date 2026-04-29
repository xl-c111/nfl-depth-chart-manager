package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import java.util.List;

/**
 * Service contract for depth chart operations.
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
     */
    void addPlayerToDepthChart(String position, Player player, Integer positionDepth);

    /**
     * Removes a player from a position in the depth chart.
     *
     * @param position the position code
     * @param player   the player to remove
     * @return a single-element list {@code [player]} when removed, or an empty list {@code []} when not listed at that position
     */
    List<Player> removePlayerFromDepthChart(String position, Player player);

    /**
     * Gets backup players behind a given player at a position.
     *
     * @param position the position code
     * @param player   the reference player
     * @return the backup players behind the given player
     */
    List<Player> getBackups(String position, Player player);

    /**
     * Gets the full depth chart as formatted text.
     *
     * @return the full depth chart string
     */
    String getFullDepthChart();
}

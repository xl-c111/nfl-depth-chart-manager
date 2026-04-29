package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Value object for an NFL player.
 *
 * @author Xiaoling Cui
 * @version 2.0
 */

public final class Player {
    private final int number;
    private final String name;

    /**
     * Constructs a Player.
     *
     * @param number the player number
     * @param name   the player name
     * @throws DepthChartValidationException if number is invalid or name is
     *                                       null/blank
     */
    public Player(int number, String name) {
        if (number <= 0) {
            throw new DepthChartValidationException("player number must be greater than 0");
        }
        if (name == null) {
            throw new DepthChartValidationException("player name must not be null");
        }
        String normalizedName = name.trim();
        if (normalizedName.isEmpty()) {
            throw new DepthChartValidationException("player name must not be blank");
        }
        this.number = number;
        this.name = normalizedName;
    }

    /**
     * Gets the player number.
     *
     * @return the player number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the player name.
     *
     * @return the player name
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Player player)) {
            return false;
        }
        return number == player.number;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }

    @Override
    public String toString() {
        return "(#" + number + ", " + name + ")";
    }
}

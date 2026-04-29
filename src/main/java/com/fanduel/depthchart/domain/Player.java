package com.fanduel.depthchart.domain;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/** Immutable NFL player identified by jersey number within one team context. */

public final class Player {
    // Player is immutable value obj
    private final int number;
    private final String name;

    public Player(int number, String name) {
        if (name == null) {
            throw new DepthChartValidationException("player name must not be null");
        }
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

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

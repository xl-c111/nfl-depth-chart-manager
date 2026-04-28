package com.fanduel.depthchart.domain;

import java.util.Objects;

/** Immutable NFL player identified by jersey number within one team context. */
public final class Player {
    private final int number;
    private final String name;

    public Player(int number, String name) {
        this.number = number;
        this.name = Objects.requireNonNull(name, "name must not be null");
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

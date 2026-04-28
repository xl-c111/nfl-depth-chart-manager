package com.fanduel.depthchart.domain;

import java.util.Locale;
import java.util.Objects;

/** Value object for NFL depth chart position code. */
public final class Position {
    private final String code;

    private Position(String code) {
        this.code = code;
    }

    public static Position of(String rawCode) {
        Objects.requireNonNull(rawCode, "position code must not be null");
        String normalized = rawCode.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("position code must not be blank");
        }
        return new Position(normalized);
    }

    public String code() {
        return code;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position position)) {
            return false;
        }
        return code.equals(position.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}

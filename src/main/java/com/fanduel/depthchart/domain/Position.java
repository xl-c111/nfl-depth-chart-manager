package com.fanduel.depthchart.domain;

import java.util.Locale;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Value object for NFL depth chart position code.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public final class Position {
    private final String code;

    /**
     * Constructs a Position.
     *
     * @param code the normalized position code
     */
    private Position(String code) {
        this.code = code;
    }

    /**
     * Creates a position from raw input.
     *
     * @param rawCode the raw position code
     * @return the created Position
     * @throws DepthChartValidationException if rawCode is null or blank
     */
    public static Position of(String rawCode) {
        if (rawCode == null) {
            throw new DepthChartValidationException("position must not be null");
        }

        String normalized = rawCode.trim().toUpperCase(Locale.ROOT);

        if (normalized.isEmpty()) {
            throw new DepthChartValidationException("position must not be blank");
        }

        return new Position(normalized);
    }

    /**
     * Gets the position code.
     *
     * @return the position code
     */
    public String getCode() {
        return code;
    }

    /**
     * Compares this Position with another object.
     *
     * @param other the object to compare
     * @return true if equal, false otherwise
     */
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

    /**
     * Returns the hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

    /**
     * Returns the string representation.
     *
     * @return the position code as string
     */
    @Override
    public String toString() {
        return code;
    }
}

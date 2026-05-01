package com.fanduel.depthchart.domain;

import java.util.Locale;
import java.util.Set;

import com.fanduel.depthchart.exception.DepthChartValidationException;

/**
 * Validated NFL position value object.
 *
 * <p>
 * The allowlist is intentionally NFL-specific for this challenge and can
 * be moved behind sport-specific rules if supporting multiple sports.
 * </p>
 *
 * @author Xiaoling Cui
 * @version 3.0
 */
public final class Position {
    private static final Set<String> ALLOWED_NFL_POSITION_CODES = Set.of(
            "QB", "RB", "FB", "WR", "LWR", "SWR", "RWR", "TE",
            "LT", "LG", "C", "RG", "RT",
            "DE", "DT", "NT", "LE", "RE",
            "LB", "ILB", "OLB", "LOLB", "MLB", "ROLB",
            "CB", "LCB", "RCB", "DB", "S", "FS", "SS",
            "K", "P", "LS", "KR", "PR");

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
     * Input is normalized via {@code trim()} and uppercase with
     * {@link Locale#ROOT}.
     * The normalized code must be in the allowed NFL position set.
     *
     * @param rawCode the raw position code
     * @return the created Position
     * @throws DepthChartValidationException if rawCode is null, blank, or not an
     *                                       allowed NFL position code
     */
    public static Position of(String rawCode) {
        if (rawCode == null) {
            throw new DepthChartValidationException("position must not be null");
        }

        String normalized = rawCode.trim().toUpperCase(Locale.ROOT);

        if (normalized.isEmpty()) {
            throw new DepthChartValidationException("position must not be blank");
        }
        if (!ALLOWED_NFL_POSITION_CODES.contains(normalized)) {
            throw new DepthChartValidationException("unsupported NFL position code: " + normalized);
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

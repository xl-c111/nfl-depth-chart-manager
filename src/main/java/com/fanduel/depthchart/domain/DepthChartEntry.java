package com.fanduel.depthchart.domain;

import java.util.Objects;

/** Snapshot entry containing player and current depth index for one position. */
public record DepthChartEntry(Player player, int depth) {
    public DepthChartEntry {
        Objects.requireNonNull(player, "player must not be null");
        if (depth < 0) {
            throw new IllegalArgumentException("depth must be >= 0");
        }
    }
}

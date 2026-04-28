package com.fanduel.depthchart.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate root for depth chart rules.
 *
 * Implementation intentionally deferred while project skeleton is being finalized.
 */
public class DepthChart {

    public void addPlayer(Position position, Player player, Integer depth) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Player> removePlayer(Position position, Player player) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Player> getBackups(Position position, Player player) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Map<Position, List<Player>> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>());
    }
}

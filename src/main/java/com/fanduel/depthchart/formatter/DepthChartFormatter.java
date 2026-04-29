package com.fanduel.depthchart.formatter;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Formatter for converting depth chart snapshots into display text.
 *
 * @author Xiaoling Cui
 * @version 1.0
 */
public class DepthChartFormatter {

    /**
     * Formats a depth chart snapshot into multi-line text.
     *
     * @param snapshot the depth chart snapshot
     * @return the formatted depth chart string
     */
    public String format(Map<Position, List<Player>> snapshot) {
        StringJoiner lines = new StringJoiner(System.lineSeparator());
        for (Map.Entry<Position, List<Player>> entry : snapshot.entrySet()) {
            StringJoiner players = new StringJoiner(", ");
            for (Player player : entry.getValue()) {
                players.add(player.toString());
            }
            lines.add(entry.getKey() + " - " + players);
        }
        return lines.toString();
    }
}

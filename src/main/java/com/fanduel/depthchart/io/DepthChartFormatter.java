package com.fanduel.depthchart.io;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/** Responsible only for converting a depth chart snapshot into display text. */
public class DepthChartFormatter {

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

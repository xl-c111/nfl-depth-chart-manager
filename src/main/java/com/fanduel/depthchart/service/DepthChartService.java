package com.fanduel.depthchart.service;

import com.fanduel.depthchart.domain.Player;
import java.util.List;

public interface DepthChartService {
    void addPlayerToDepthChart(String position, Player player, Integer positionDepth);

    List<Player> removePlayerFromDepthChart(String position, Player player);

    List<Player> getBackups(String position, Player player);

    String getFullDepthChart();
}

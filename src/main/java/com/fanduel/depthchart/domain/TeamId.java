package com.fanduel.depthchart.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fanduel.depthchart.exception.DepthChartValidationException;

public final class TeamId {
    private static final Set<String> VALID_TEAM_CODES = Set.of(
            "ARI", "ATL", "BAL", "BUF", "CAR", "CHI", "CIN", "CLE",
            "DAL", "DEN", "DET", "GB", "HOU", "IND", "JAX", "KC",
            "LV", "LAC", "LAR", "MIA", "MIN", "NE", "NO", "NYG",
            "NYJ", "PHI", "PIT", "SEA", "SF", "TB", "TEN", "WAS");

    private final String value;

    public TeamId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DepthChartValidationException("TeamId cannot be null or blank.");
        }

        String normalized = value.trim().toUpperCase();

        if (!VALID_TEAM_CODES.contains(normalized)) {
            throw new DepthChartValidationException("Invalid NFL team code: " + value);
        }

        this.value = normalized;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TeamId)) {
            return false;
        }

        TeamId other = (TeamId) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

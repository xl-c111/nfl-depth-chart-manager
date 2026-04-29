package com.fanduel.depthchart.formatter;

import com.fanduel.depthchart.domain.Player;
import com.fanduel.depthchart.domain.Position;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.fanduel.depthchart.fixture.PlayerFixture.BLAINE_GABBERT;
import static com.fanduel.depthchart.fixture.PlayerFixture.JAELON_DARDEN;
import static com.fanduel.depthchart.fixture.PlayerFixture.MIKE_EVANS;
import static com.fanduel.depthchart.fixture.PlayerFixture.TOM_BRADY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DepthChartFormatterTest {
    private final DepthChartFormatter formatter = new DepthChartFormatter();

    @Test
    void format_shouldReturnEmptyStringWhenSnapshotIsEmpty() {
        String output = formatter.format(Map.of());

        assertEquals("", output);
    }

    @Test
    void format_shouldFormatSinglePositionDepthChart() {
        Map<Position, List<Player>> snapshot = new LinkedHashMap<>();
        snapshot.put(Position.of("QB"), List.of(TOM_BRADY, BLAINE_GABBERT));

        String output = formatter.format(snapshot);

        assertEquals("QB - (#12, Tom Brady), (#11, Blaine Gabbert)", output);
    }

    @Test
    void format_shouldPreserveSnapshotIterationOrder() {
        Map<Position, List<Player>> snapshot = new LinkedHashMap<>();
        snapshot.put(Position.of("QB"), List.of(TOM_BRADY, BLAINE_GABBERT));
        snapshot.put(Position.of("LWR"), List.of(MIKE_EVANS, JAELON_DARDEN));

        String output = formatter.format(snapshot);

        String expected = String.join(System.lineSeparator(),
                "QB - (#12, Tom Brady), (#11, Blaine Gabbert)",
                "LWR - (#13, Mike Evans), (#1, Jaelon Darden)");
        assertEquals(expected, output);
    }
}

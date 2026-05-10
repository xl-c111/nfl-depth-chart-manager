# Depth Chart Full Flow (App + Service + Domain)

This version uses plain text only, so it renders in any Markdown viewer.

## Full Flow (ASCII)

```text
[Start]
  |
  v
DepthChartApplication.main()
  |
  +--> create InMemoryDepthChartService
  |       |
  |       +--> holds DepthChart (domain aggregate, in-memory state)
  |
  +--> new DemoScenario(service).run()
          |
          v
    seedSampleDepthChartFromJson()
          |
          +--> load JSON from classpath (sampleDataPath)
          |
          +--> parseRows(input)
          |       |
          |       +--> require START_ARRAY
          |       |
          |       +--> loop parseRow(parser) until END_ARRAY
          |               |
          |               +--> read fields: number, name, position, depth
          |               +--> skip unknown fields
          |               +--> validate required fields
          |               +--> build DepthChartRow
          |
          +--> for each row:
                  |
                  +--> service.addPlayerToDepthChart(position, player, depth)
                          |
                          +--> Service layer:
                          |      Position.of(position)
                          |      (normalize + validate position code)
                          |
                          +--> Domain layer:
                                 DepthChart.addPlayer(positionObj, player, depth)
                                   - validate required inputs
                                   - validate player identity consistency
                                   - insert/reposition in Map<Position, List<Player>>

    run() continues
      |
      +--> printBackups(...) x multiple cases
      |       |
      |       +--> service.getBackups(position, player)
      |               +--> Position.of(position)
      |               +--> depthChart.getBackups(positionObj, player)
      |
      +--> print full depth chart
      |       |
      |       +--> service.getFullDepthChart()
      |               +--> depthChart.snapshot()
      |               +--> formatter.format(snapshot)
      |
      +--> removePlayerFromDepthChart("LWR", mikeEvans)
      |       |
      |       +--> service.removePlayerFromDepthChart(position, player)
      |               +--> Position.of(position)
      |               +--> depthChart.removePlayer(positionObj, player)
      |
      +--> print removed list
      |
      +--> print full depth chart after removal

[End]
```

## Layer Responsibility

```text
App (DepthChartApplication + DemoScenario):
- Orchestrates scenario steps and console output.

Service (InMemoryDepthChartService):
- Accepts app-facing inputs (String position + Player).
- Converts/validates position via Position.of(...).
- Delegates business actions to domain.

Domain (DepthChart + Position + Player):
- Owns business rules and state transitions.
- Enforces add/remove/reorder/backups behavior.
- Returns immutable snapshot for read operations.
```

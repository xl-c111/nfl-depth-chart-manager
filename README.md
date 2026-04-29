# FanDuel Depth Chart Manager

## Overview
Java 17 + Maven implementation of the FanDuel Trading Solutions coding challenge for NFL depth charts.

Prompt note: the sample includes a few inconsistent position references (for example using `QB` where setup data is `LWR`, and `WR` where setup data is `LWR`). This implementation applies strict position matching and documents that behavior in tests and assumptions.

## How To Build And Run
1. Validate with tests:
```bash
mvn test
```
2. Run the demo scenario:
```bash
mvn -q exec:java -Dexec.mainClass="com.fanduel.depthchart.app.DepthChartApplication"
```

## Implemented Use Cases
The required use cases from the challenge are implemented through `DepthChartService`:
- `addPlayerToDepthChart(String position, Player player, Integer positionDepth)`
- `removePlayerFromDepthChart(String position, Player player)`
- `getBackups(String position, Player player)`
- `getFullDepthChart()`

Implementation classes:
- `InMemoryDepthChartService` for use-case orchestration
- `DepthChart` for core depth chart rules
- `DepthChartFormatter` for output formatting

## Design And Organization
- `domain`: business model and rules (`Player`, `Position`, `DepthChart`)
- `service`: challenge API and orchestration (`DepthChartService`, `InMemoryDepthChartService`)
- `formatter`: output formatting separated from domain logic
- `app`: thin entrypoint (`DepthChartApplication`) and demo workflow runner (`DemoScenario`)
- `exception`: business validation exception type
- `test`: unit and contract-style tests

## Assumptions
- Single NFL team scope in memory for this submission.
- Player identity is jersey `number` within one team context.
- A player can appear at multiple positions.
- Position input is normalized with `trim + uppercase`.
- Player constraints:
  - `number` must be greater than `0`
  - `name` must be non-null and non-blank
- Depth constraints:
  - `positionDepth == null` appends to the end
  - non-null depth must be within `[0, currentSize]`
- API return contract:
  - `removePlayerFromDepthChart` returns a `List<Player>` for consistency: `[player]` when removed, `[]` when not found at that position
  - In the demo output, `Removed from LWR` is the formatted display of that list result (`[player]` shown as one line, `[]` shown as `<NO LIST>`).
- Removing the last player at a position removes that position from the chart snapshot/output.

Full contract details:
- `docs/requirements_and_assumptions.md`

## Sample Inconsistencies In Prompt
The challenge sample includes a few inconsistent position references (for example using `QB` where setup data is `LWR`, and `WR` where setup data is `LWR`).  
This implementation applies strict position matching and documents the behavior in tests and assumptions.

## Testing Approach
- Unit tests cover domain rules, service behavior, formatter output, and validation exceptions.
- Edge cases covered include:
  - invalid depth bounds
  - null/blank position
  - null player
  - invalid player data (`number <= 0`, blank name)
  - same player listed at multiple positions and independent remove behavior per position
  - removing absent players
  - removing the last player at a position
  - backups for missing/non-listed players
  - immutable snapshot behavior

Coverage:
- Run: `mvn verify`
- Report: `target/site/jacoco/index.html`

## Scalability Notes
To scale this design in a production setting, the next concrete steps would be:

- Team dimension (`TeamId -> DepthChart`):
  - Introduce a `TeamId` value object and a `DepthChartRepository`.
  - Store and load charts by team key so one service instance can manage all NFL teams, not a single in-memory chart.

- Sport dimension (`DepthChartRules` + sport-specific implementations):
  - Define a `DepthChartRules` interface for add/remove/backups semantics.
  - Provide sport modules such as `NflDepthChartRules`, `NbaDepthChartRules`, and `MlbDepthChartRules` where roster/position behavior differs.

- Persistence dimension (in-memory adapter vs DB adapter):
  - Keep the current in-memory adapter for local runs and tests.
  - Add a DB-backed adapter (for example Postgres) behind the same repository contract so domain/service APIs remain unchanged.

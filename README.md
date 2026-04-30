# NFL Depth Chart Manager

## Overview
Java 17 + Maven implementation of the FanDuel Trading Solutions coding challenge for NFL depth charts.

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
- `getFullDepthChart()` (returns `Map<Position, List<Player>>`; formatting is handled in app layer)

Implementation classes:
- `InMemoryDepthChartService` for use-case orchestration
- `DepthChart` for core depth chart rules
- `DepthChartFormatter` for output formatting

## Design And Organization
```text
app (DepthChartApplication, DemoScenario)
 ├─ calls service
 └─ uses formatter for display output

service (DepthChartService, InMemoryDepthChartService)
 └─ orchestrates depth-chart use cases

domain (Player, Position, DepthChart)
 └─ core business model and rules

formatter (DepthChartFormatter)
 └─ converts structured snapshots to text output

exception (DepthChartValidationException)
 └─ validation and contract errors across layers

test
 └─ unit and contract-style coverage across domain/service/formatter/app
```

## Assumptions
- Single NFL team scope in memory for this submission.
- Player identity is jersey `number` within one team context.
- A player can appear at multiple positions.
- Position input is normalized with `trim + uppercase`.
- Player constraints: `number > 0`; `name` is non-null and non-blank.
- Depth constraints: `positionDepth == null` appends; non-null depth must be within `[0, currentSize]`.
- API return contract:
  - `removePlayerFromDepthChart` returns `List<Player>`: `[player]` when removed, `[]` when absent at that position.
  - `Optional<Player>` was intentionally not used, to keep empty-list semantics aligned with challenge output (`<NO LIST>`).
  - In demo output, `Removed from LWR` displays that list result (`[player]` as one line, `[]` as `<NO LIST>`).
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
  - null/blank position and null player
  - invalid player data (`number <= 0`, blank name)
  - re-adding same player at same position (reposition behavior)
  - same player listed at multiple positions with independent remove behavior
  - removing absent players and removing the last player at a position
  - backups for missing/non-listed players
  - immutable snapshot behavior and stable formatted output ordering
  - strict position matching for prompt inconsistencies (`QB` vs `LWR`, `WR` vs `LWR`)

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

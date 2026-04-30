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
The demo seeds data from `src/main/resources/data/tb-depth-chart-sample.json`.

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
- Re-adding the same player at the same position is treated as repositioning (not duplication). For repositioning, `positionDepth == currentSize` is valid and means moving that player to the end.
- API return contract: `removePlayerFromDepthChart` returns `List<Player>` (`[player]` when removed, `[]` when absent). `Optional<Player>` is intentionally not used to preserve challenge-style empty-list semantics (`<NO LIST>` in demo output).
- Removing the last player at a position removes that position from the chart snapshot/output.

Full contract details:
- `docs/requirements_and_assumptions.md`

## Sample Inconsistencies In Prompt
The challenge sample includes a few inconsistent position references (for example using `QB` where setup data is `LWR`, and `WR` where setup data is `LWR`).  
This implementation applies strict position matching and documents the behavior in tests and assumptions.

## Testing Approach
- Unit tests cover domain rules, service behavior, formatter output, and validation exceptions.
- Edge cases covered include:
  - input validation (depth bounds, null/blank position, null player, invalid player data)
  - ordering semantics (insert shift-down, reposition including move-to-end, stable output ordering)
  - position-scoped behavior (same player across multiple positions, strict position matching)
  - removal semantics (absent player returns empty list, last removal drops the position key)
  - query semantics (`getBackups` for missing/non-listed/terminal players, immutable snapshots)

Coverage:
- Run: `mvn verify`
- Report: `target/site/jacoco/index.html`

## Scalability Notes
This submission intentionally targets one in-memory NFL team. To address the scaling questions in the prompt, evolve it in three steps:

- Multi-team NFL support: introduce a `TeamId` and replace the single in-memory `DepthChart` instance with a repository keyed by team, so one service can manage all 32 teams with the same use-case API; at that point, player identity should evolve from jersey number to `(teamId, number)` or a global `playerId`.
- Multi-sport support: keep `DepthChart` as the core ordering model, and move sport-specific constraints behind a `DepthChartRules` contract (for example NFL vs NBA position vocabularies and roster semantics).
- Production persistence and throughput: keep the current in-memory adapter for tests/demo, add a database-backed repository for durable state, and add optimistic locking/version checks so concurrent updates do not corrupt ordering.

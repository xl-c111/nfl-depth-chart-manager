# NFL Depth Chart Manager

## Overview
Java 17 + Maven implementation of the FanDuel Trading Solutions coding challenge for NFL depth charts.

Requirements: Java 17, Maven

## How To Build And Run
1. Run tests and coverage:
```bash
mvn verify
```
2. Run the demo scenario:
```bash
mvn -q exec:java -Dexec.mainClass="com.fanduel.depthchart.app.DepthChartApplication"
```
The demo seeds from `src/main/resources/data/tb-depth-chart-sample.json`.

## Implemented Use Cases
Implemented through `DepthChartService`:
- `addPlayerToDepthChart(String position, Player player, Integer positionDepth)`
- `removePlayerFromDepthChart(String position, Player player)`
- `getBackups(String position, Player player)`
- `getFullDepthChart()`

## Design
```text
app (DepthChartApplication, DemoScenario)
 └─ demo flow and console output

service (DepthChartService, InMemoryDepthChartService)
 └─ use-case orchestration

domain (Player, Position, DepthChart)
 └─ core rules and invariants

formatter (DepthChartFormatter)
 └─ formatting of full chart output
```

## Assumptions
- Single NFL team scope in memory for this submission.
- Player identity is jersey `number` within one team context (`[0, 99]`).
- A player can appear at multiple positions.
- Position input is normalized with `trim + uppercase`.
- Position codes are validated against an explicit NFL allowlist (for example `QB`, `RB`, `WR`, `LWR`, `SWR`, `RWR`, `TE`, `LT`, `LG`, `C`, `RG`, `RT`, `CB`, `FS`, `SS`, `K`, `P`, `LS`, `KR`, `PR`); unsupported codes throw `DepthChartValidationException`.
- Strict position matching is used (`WR` and `LWR` are different position keys).
- Same number with materially different name is rejected; name consistency check is case-insensitive only (`Tom Brady` equals `TOm BrADY`, but `Brady` does not equal `Brandy`).
- Player name is non-null, non-blank, and trimmed.
- Depth constraints: `positionDepth == null` appends; non-null depth must be within `[0, currentSize]`.
- Re-adding the same player at the same position is treated as repositioning (not duplication). For repositioning, `positionDepth == currentSize` is valid and means moving that player to the end.
- `removePlayerFromDepthChart` returns `List<Player>` (`[player]` when removed, `[]` when absent) to align with challenge empty-list semantics.
- Removing the last player at a position removes that position from the chart snapshot/output.
- Concurrency model: `DepthChart` uses in-process `synchronized` method-level locking to prevent race conditions on concurrent access.
- Thread-safety scope: this protects concurrent access within one JVM process only; distributed concurrency control is out of scope for this submission.

## Sample Inconsistencies In Prompt
The prompt includes inconsistent position references (for example `QB` used where setup data is `LWR`, and `WR` used where setup data is `LWR`).
This implementation keeps strict matching and treats these as prompt typos.

## Testing
- Unit tests cover domain rules, service behavior, formatter output, demo flow, and validation errors.
- Edge cases include invalid inputs, depth bounds, strict position matching, reposition semantics, absent removals, terminal backups, and immutable snapshots.
- Coverage report (local): `target/site/jacoco/index.html`
- Coverage report (online): `https://xl-c111.github.io/nfl-depth-chart-manager/coverage/index.html`

Coverage snapshot (latest `main` run):
- Instruction: `100%`
- Branch: `100%`
- Line: `100%`

## Documentation
- Full contract details: `docs/requirements_and_assumptions.md`
- Javadocs (online): `https://xl-c111.github.io/nfl-depth-chart-manager/javadocs/index.html`

## Scalability Notes
This submission intentionally targets one in-memory NFL team. To address the scaling questions in the prompt, evolve it in three steps:

- Multi-team NFL support: introduce a `TeamId` and replace the single in-memory `DepthChart` instance with a repository keyed by team, so one service can manage all 32 teams with the same use-case API; at that point, player identity should evolve from jersey number to `(teamId, number)` or a global `playerId`.
- Multi-sport support: keep `DepthChart` as the core ordering model, and move sport-specific constraints behind a `DepthChartRules` contract (for example NFL vs NBA position vocabularies and roster semantics).
- Production persistence and throughput: keep the current in-memory adapter for tests/demo, add a database-backed repository for durable state, and add optimistic locking/version checks so concurrent updates do not corrupt ordering.

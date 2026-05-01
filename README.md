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
- Position codes are validated against an explicit NFL allowlist; unsupported codes throw `DepthChartValidationException`.
- The NFL allowlist is intentionally strict for this challenge; in a multi-sport version, this would move behind a sport-specific rules provider.
- Strict position matching is used (`WR` and `LWR` are different position keys).
- `positionDepth == null` appends; non-null depth must be within `[0, currentSize]`.
- Re-adding the same player at the same position is treated as repositioning, not duplication.
- `removePlayerFromDepthChart` returns `List<Player>` (`[player]` when removed, `[]` when absent) to align with challenge empty-list semantics.
- Concurrency is limited to single-JVM in-process safety using coarse `synchronized` locking.

## Sample Inconsistencies In Prompt
The prompt includes inconsistent position references (for example `QB` used where setup data is `LWR`, and `WR` used where setup data is `LWR`).
This implementation keeps strict matching and treats these as prompt typos.

## Testing
- The test suite focuses on highest-risk behavior: ordering, identity consistency, boundary validation, strict position matching, and immutable snapshots.
- Tests also cover service behavior, formatter output, demo flow, and validation errors.
- Coverage report (local): `target/site/jacoco/index.html`
- Coverage report (online): `https://xl-c111.github.io/nfl-depth-chart-manager/coverage/index.html`

## Documentation
- Full contract details: `docs/requirements_and_assumptions.md`
- Javadocs (online): `https://xl-c111.github.io/nfl-depth-chart-manager/javadocs/index.html`

## Design Tradeoffs
- Strict position matching over fuzzy matching: avoids silent data corruption from ambiguous keys.
- Structured service output over preformatted strings: keeps domain and service logic reusable across interfaces.
- In-memory implementation over persistence: appropriate for challenge scope and deterministic tests.
- Coarse `synchronized` locking over fine-grained locking: simpler and safer for this scope, at the cost of throughput under contention.

## Scalability Notes
This submission intentionally focuses on one in-memory NFL team. To scale the design:

- **Multiple NFL teams**: introduce a `TeamId` and a repository keyed by team, so the same service API can manage all 32 teams. Player identity can then evolve from jersey `number` to `(teamId, number)` or a global `playerId`.

- **Multiple sports**: keep `DepthChart` as the reusable ordering model, and move sport-specific validation behind a `DepthChartRules` abstraction.

- **Production persistence**: keep the in-memory implementation for tests and demos, and add a database-backed repository for durable state, concurrency control, and higher-throughput reads/writes.

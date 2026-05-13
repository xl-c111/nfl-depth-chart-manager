# Requirements and Assumptions

## Purpose
This document captures the functional contract and implementation decisions for the FanDuel NFL Depth Chart challenge.

## Scope
- Single NFL team
- In-memory state only
- Java service API (no external API endpoint)
- Demo initialization from a bundled local JSON file

## Required Use Cases
1. `addPlayerToDepthChart(position, player, positionDepth)`
2. `removePlayerFromDepthChart(position, player)`
3. `getBackups(position, player)`
4. `getFullDepthChart()`

## API Contract
### `addPlayerToDepthChart(position, player, positionDepth)`
- Adds `player` at `position`.
- `positionDepth == null` appends to end.
- Depth is zero-based.
- Inserted player takes priority; existing players at and below that index shift down.
- Invalid depth (`< 0` or `> current size`) throws `DepthChartValidationException`.
- Re-adding the same player at the same position is treated as reposition (not duplication): remove current occurrence, then insert; `positionDepth == current size` is valid and moves that player to the end.

### `removePlayerFromDepthChart(position, player)`
- Removes `player` from `position`.
- Returns a single-item list containing removed player when found.
- Returns empty list when player is not present at that position.

### `getBackups(position, player)`
- Returns all players below `player` at the specified `position`.
- Returns empty list when player has no backups or is not listed at that position.

### `getFullDepthChart()`
- Returns full depth chart for all positions currently present.
- Domain returns structured snapshot; formatting is handled separately in formatter layer.

## Data and Validation Assumptions
1. `Player.number` is unique within one team context.
2. `Player.number` must be in `[0, 99]`.
3. The same `number` cannot be associated with different `name` values within a team context.
4. Name consistency for the same `number` is case-insensitive only (`Tom Brady` equals `TOm BrADY`), and does not include typo/spelling correction (`Brady` does not equal `Brandy`).
5. A player may exist at multiple positions simultaneously.
6. Position input is normalized as `trim + uppercase`.
7. Position codes must be valid NFL codes from the implementation allowlist; unsupported codes are rejected.
   The current NFL allowlist is intentionally strict for this challenge; in a multi-sport version, position validation should move behind a sport-specific rules provider rather than remain hard-coded in `Position`.
8. Required inputs (`position`, `player`) must be non-null.

## Challenge Sample Inconsistencies and Resolution
1. Sample call `getBackups("QB", JaelonDarden)` conflicts with setup (`JaelonDarden` added at `LWR`).
Resolution: treated as sample typo; tests validate backups with correct position context.

2. Sample call `removePlayerFromDepthChart("WR", MikeEvans)` conflicts with setup (`LWR`).
Resolution: treated as sample typo; implementation uses strict normalized position key matching.

3. Sample JSON occasionally shows key `"number "` (trailing space).
Resolution: treated as documentation typo; typed model fields are authoritative.

## Non-Goals
1. Production infrastructure concerns (persistence, distributed consistency, and multi-team orchestration).
2. External delivery interfaces (REST/GraphQL API layer).
3. External document ingestion/parsing beyond bundled local demo data.

## Concurrency
This implementation is thread-safe for in-process access to one `DepthChart` instance using `synchronized` method-level locking.
- `addPlayer`, `removePlayer`, `getBackups`, and `snapshot` are synchronized.

Scope note: this covers concurrency within a single JVM process only. Distributed consistency and cross-process coordination are out of scope for this challenge submission.

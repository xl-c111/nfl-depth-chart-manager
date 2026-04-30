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
- Domain returns structured snapshot; formatting is handled separately in IO layer.

## Data and Validation Assumptions
1. `Player.number` is unique within one team context.
2. A player may exist at multiple positions simultaneously.
3. Position input is normalized as `trim + uppercase`.
4. Required inputs (`position`, `player`) must be non-null.

## Challenge Sample Inconsistencies and Resolution
1. Sample call `getBackups("QB", JaelonDarden)` conflicts with setup (`JaelonDarden` added at `LWR`).
Resolution: treated as sample typo; tests validate backups with correct position context.

2. Sample call `removePlayerFromDepthChart("WR", MikeEvans)` conflicts with setup (`LWR`).
Resolution: treated as sample typo; implementation uses strict normalized position key matching.

3. Sample JSON occasionally shows key `"number "` (trailing space).
Resolution: treated as documentation typo; typed model fields are authoritative.

## Non-Goals
1. Persistence/database integration.
2. REST/GraphQL API layer.
3. PDF/webpage ingestion/parsing from external sources (demo uses bundled local JSON only).
4. Multi-team abstraction beyond current single-team scope.

## Concurrency
Current target is non-thread-safe in-memory behavior, which is acceptable for challenge scope. If concurrency is required, introduce synchronization or an immutable snapshot/update strategy.

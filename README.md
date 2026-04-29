# FanDuel Depth Chart Manager

## Overview
Java 17 + Maven implementation of the FanDuel Trading Solutions take-home challenge for NFL depth charts.

## Tech Stack
- Java 17
- Maven
- JUnit 5

## Project Structure
- `src/main/java/com/fanduel/depthchart/domain`
  - Core domain model and rules (`Player`, `Position`, `DepthChart`)
- `src/main/java/com/fanduel/depthchart/service`
  - Use-case API and orchestration (`DepthChartService`, `InMemoryDepthChartService`)
- `src/main/java/com/fanduel/depthchart/formatter`
  - Output formatting (`DepthChartFormatter`)
- `src/main/java/com/fanduel/depthchart/exception`
  - Business validation exception (`DepthChartValidationException`)
- `src/test/java/com/fanduel/depthchart`
  - Unit/contract tests

## Public API
- `addPlayerToDepthChart(String position, Player player, Integer positionDepth)`
- `removePlayerFromDepthChart(String position, Player player)`
- `getBackups(String position, Player player)`
- `getFullDepthChart()`

## Design Notes
1. Business rules are in `domain` (`DepthChart` aggregate root), not in app/main code.
2. `service` layer exposes challenge use cases and delegates to domain.
3. Formatter is separated from business logic for testability and reuse.
4. `Position` is a value object (normalized, not hard-coded NFL enum) to keep design extensible.

## Key Assumptions and Decisions
1. Single-team scope in memory for this submission.
2. Player identity is `number` within one team context.
3. Position input is normalized with `trim + uppercase`.
4. Invalid depth (`< 0` or `> current size`) throws validation exception.
5. Prompt sample typos are documented and handled explicitly in tests/docs.

For full details, see:
- `docs/requirements_and_assumptions.md`

## Build and Test
```bash
mvn test
```

## Current Status
Architecture skeleton and contracts are in place. Domain behavior is being implemented incrementally with small, review-friendly commits.

## Scalability Direction (Future)
1. Add repository abstraction for multi-team support.
2. Add sport-specific rule sets (NFL/NBA/MLB) behind shared service contract.
3. Add persistence adapter (in-memory -> DB) without changing domain API.

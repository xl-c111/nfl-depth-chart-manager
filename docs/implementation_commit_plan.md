# Implementation and Commit Plan

## Goal
Deliver the FanDuel NFL Depth Chart solution with production-style commit hygiene: small, scoped, test-backed commits that are easy to review.

## Commit Principles
1. One concern per commit.
2. Keep repository buildable at every commit.
3. Run `mvn test` before each commit.
4. Separate feature changes from test and documentation updates when possible.
5. Use clear conventional commit messages.

## Baseline
The repository currently contains architecture skeleton only:
- `domain`: core model and unimplemented aggregate methods
- `service`: API contract and in-memory orchestration
- `io`: formatter
- `exception`: validation exception type

## Planned Commit Sequence

### 1. Lock behavior contract
- Scope:
  - `docs/requirements_and_assumptions.md`
  - `README.md`
- Outcome:
  - Finalize edge-case decisions (depth bounds, duplicate-add semantics, sample typo handling).
- Message:
  - `docs: finalize challenge contracts and assumptions`

### 2. Implement validation foundation
- Scope:
  - `src/main/java/com/fanduel/depthchart/domain/Position.java`
  - `src/main/java/com/fanduel/depthchart/exception/DepthChartValidationException.java`
- Outcome:
  - Centralized input and position validation behavior.
- Message:
  - `feat(domain): implement position normalization and validation rules`

### 3. Implement add behavior in aggregate
- Scope:
  - `src/main/java/com/fanduel/depthchart/domain/DepthChart.java`
- Outcome:
  - Null-depth append, indexed insert, shift-down behavior, bounds enforcement.
- Message:
  - `feat(domain): implement add-player depth insertion logic`

### 4. Add tests for add behavior
- Scope:
  - `src/test/java/com/fanduel/depthchart/domain/DepthChartAddTest.java`
  - `src/test/java/com/fanduel/depthchart/fixture/PlayerFixture.java`
- Outcome:
  - Add path and depth-boundary coverage.
- Message:
  - `test(domain): cover add behavior and boundary conditions`

### 5. Implement remove behavior
- Scope:
  - `src/main/java/com/fanduel/depthchart/domain/DepthChart.java`
- Outcome:
  - Remove listed player, compact list, return empty list when absent.
- Message:
  - `feat(domain): implement remove-player behavior`

### 6. Add tests for remove behavior
- Scope:
  - `src/test/java/com/fanduel/depthchart/domain/DepthChartRemoveTest.java`
- Outcome:
  - Remove success, absent remove, empty-position remove, compaction validation.
- Message:
  - `test(domain): add remove behavior coverage`

### 7. Implement backups behavior
- Scope:
  - `src/main/java/com/fanduel/depthchart/domain/DepthChart.java`
- Outcome:
  - Return all players below target; empty list for absent/last player.
- Message:
  - `feat(domain): implement backups query behavior`

### 8. Add tests for backups behavior
- Scope:
  - `src/test/java/com/fanduel/depthchart/domain/DepthChartBackupsTest.java`
- Outcome:
  - Head/mid/tail backup cases, missing-player case, multi-position independence.
- Message:
  - `test(domain): add backups query coverage`

### 9. Implement full chart snapshot + formatting
- Scope:
  - `src/main/java/com/fanduel/depthchart/domain/DepthChart.java`
  - `src/main/java/com/fanduel/depthchart/io/DepthChartFormatter.java`
  - `src/main/java/com/fanduel/depthchart/service/InMemoryDepthChartService.java`
- Outcome:
  - Immutable chart snapshot and deterministic formatted output.
- Message:
  - `feat(io): implement full depth chart snapshot and formatter`

### 10. Add service-level contract tests
- Scope:
  - `src/test/java/com/fanduel/depthchart/service/InMemoryDepthChartServiceTest.java`
- Outcome:
  - End-to-end validation of challenge API and sample-aligned flow.
- Message:
  - `test(service): add FanDuel use-case contract tests`

### 11. Refactor and documentation pass
- Scope:
  - public classes under `domain/service/io/exception`
- Outcome:
  - Javadoc for public contracts/invariants, naming and method-size cleanup.
- Message:
  - `refactor: improve readability and add public API javadocs`

### 12. Submission polish
- Scope:
  - `README.md`
  - `docs/requirements_and_assumptions.md`
- Outcome:
  - Final runbook, design rationale, complexity notes, extension strategy.
- Message:
  - `docs: finalize submission documentation`

## Review Checklist Per Commit
1. `mvn test` passes.
2. Diff is scoped to one concern.
3. Commit message clearly describes user-visible or design-visible change.
4. Documentation updated when behavior contract changes.

## Anti-Patterns to Avoid
1. Large mixed commits (feature + refactor + docs + tests all together).
2. Breaking build between commits.
3. Silent behavior changes without matching test updates.
4. Unexplained contract decisions in code.

# NFL Depth Chart Manager Architecture

## 1. Layered System and Component Relationships
```mermaid
flowchart TD
    A[DepthChartApplication\nmain entry] --> B[DemoScenario\napplication orchestration]
    B --> C[DepthChartService\nservice interface]
    C --> D[InMemoryDepthChartService\nservice implementation]
    D --> E[DepthChart\ndomain aggregate root]

    B --> F[DepthChartFormatter\noutput formatter]
    B --> G[(tb-depth-chart-sample.json)]

    E --> H[Position\nvalue object]
    E --> I[Player\nvalue object]
    E --> J[(LinkedHashMap<Position, List<Player>>)]

    H -.invalid input.-> X[DepthChartValidationException]
    I -.invalid input.-> X
    E -.rule validation failure.-> X
```

## 2. Core Class Diagram (Simplified)
```mermaid
classDiagram
    class DepthChartApplication {
      +main(String[] args)
    }

    class DemoScenario {
      -DepthChartService service
      -DepthChartFormatter formatter
      -String sampleDataPath
      +run()
      -seedSampleDepthChartFromJson()
      -parseRows(InputStream)
      -parseRow(JsonParser)
    }

    class DepthChartService {
      <<interface>>
      +addPlayerToDepthChart(String, Player, Integer)
      +removePlayerFromDepthChart(String, Player) List~Player~
      +getBackups(String, Player) List~Player~
      +getFullDepthChart() Map~Position, List~Player~~
    }

    class InMemoryDepthChartService {
      -DepthChart depthChart
      +addPlayerToDepthChart(String, Player, Integer)
      +removePlayerFromDepthChart(String, Player) List~Player~
      +getBackups(String, Player) List~Player~
      +getFullDepthChart() Map~Position, List~Player~~
    }

    class DepthChart {
      -Map~Position, List~Player~~ chart
      +addPlayer(Position, Player, Integer)
      +removePlayer(Position, Player) List~Player~
      +getBackups(Position, Player) List~Player~
      +snapshot() Map~Position, List~Player~~
      -validateRequiredInputs(Position, Player)
      -validatePlayerIdentityConsistency(Player)
    }

    class Position {
      -String code
      +of(String) Position
      +getCode() String
    }

    class Player {
      -int number
      -String name
      +Player(int, String)
      +getNumber() int
      +getName() String
    }

    class DepthChartFormatter {
      +format(Map~Position, List~Player~~) String
    }

    class DepthChartValidationException

    DepthChartApplication --> DemoScenario
    DemoScenario --> DepthChartService
    DemoScenario --> DepthChartFormatter
    DepthChartService <|.. InMemoryDepthChartService
    InMemoryDepthChartService --> DepthChart
    DepthChart --> Position
    DepthChart --> Player
    Position ..> DepthChartValidationException
    Player ..> DepthChartValidationException
    DepthChart ..> DepthChartValidationException
```

## 3. Key Sequence Diagrams

### 3.1 Initialization and Sample Data Loading
```mermaid
sequenceDiagram
    participant App as DepthChartApplication
    participant Demo as DemoScenario
    participant Svc as InMemoryDepthChartService
    participant DC as DepthChart
    participant Pos as Position

    App->>Demo: run()
    Demo->>Demo: seedSampleDepthChartFromJson()
    loop each JSON row
      Demo->>Svc: addPlayerToDepthChart(position, player, depth)
      Svc->>Pos: Position.of(position)
      Pos-->>Svc: Position
      Svc->>DC: addPlayer(Position, Player, depth)
      DC-->>Svc: void
    end
```

### 3.2 Add/Reorder Player (addPlayer)
```mermaid
sequenceDiagram
    participant Caller as DemoScenario/Client
    participant Svc as InMemoryDepthChartService
    participant Pos as Position
    participant DC as DepthChart

    Caller->>Svc: addPlayerToDepthChart(posCode, player, depth)
    Svc->>Pos: Position.of(posCode)
    Pos-->>Svc: normalized Position
    Svc->>DC: addPlayer(position, player, depth)

    alt depth == null
      DC->>DC: if exists, remove first, then append to tail
    else invalid depth (<0 or >size)
      DC-->>Caller: DepthChartValidationException
    else valid depth
      DC->>DC: if exists, remove first, then insert by index
    end

    DC-->>Svc: void
    Svc-->>Caller: void
```

### 3.3 Query Backups (getBackups)
```mermaid
sequenceDiagram
    participant Caller as DemoScenario/Client
    participant Svc as InMemoryDepthChartService
    participant Pos as Position
    participant DC as DepthChart

    Caller->>Svc: getBackups(posCode, player)
    Svc->>Pos: Position.of(posCode)
    Pos-->>Svc: Position
    Svc->>DC: getBackups(position, player)

    alt position missing / player not at this position / player already last
      DC-->>Svc: []
    else
      DC-->>Svc: subList(playerDepth+1..end)
    end

    Svc-->>Caller: List<Player>
```

### 3.4 Remove Player (removePlayer)
```mermaid
sequenceDiagram
    participant Caller as DemoScenario/Client
    participant Svc as InMemoryDepthChartService
    participant Pos as Position
    participant DC as DepthChart

    Caller->>Svc: removePlayerFromDepthChart(posCode, player)
    Svc->>Pos: Position.of(posCode)
    Pos-->>Svc: Position
    Svc->>DC: removePlayer(position, player)

    alt player not at this position
      DC-->>Svc: []
    else
      DC->>DC: remove(index)
      opt position becomes empty
        DC->>DC: remove map key
      end
      DC-->>Svc: [removedPlayer]
    end

    Svc-->>Caller: List<Player>
```

## 4. Internal State Model
```mermaid
erDiagram
    DEPTH_CHART ||--o{ POSITION_BUCKET : contains
    POSITION_BUCKET ||--o{ PLAYER_ENTRY : ordered_by_depth

    DEPTH_CHART {
      string impl "LinkedHashMap"
      string key "Position"
      string value "List<Player>"
    }

    POSITION_BUCKET {
      string position_code
      int size
    }

    PLAYER_ENTRY {
      int number
      string name
      int depth_index
    }
```

## 5. Design Notes
- The architecture follows a lightweight layered style with a domain aggregate: `DemoScenario` orchestrates flow, `Service` defines the boundary, and `DepthChart` centralizes business rules.
- Thread safety is implemented inside `DepthChart` using method-level `synchronized` locking for single-JVM consistency.
- Data is in-memory only, with no database or remote API.
- Immutable snapshot patterns are used for reads (`snapshot` + `List.copyOf` + `unmodifiableMap`) to prevent external mutation.
- Validation errors are consistently raised via `DepthChartValidationException`.

## 6. Source Mapping
- Application entry: [DepthChartApplication.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/app/DepthChartApplication.java)
- Scenario orchestration: [DemoScenario.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/app/DemoScenario.java)
- Service interface: [DepthChartService.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/service/DepthChartService.java)
- Service implementation: [InMemoryDepthChartService.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/service/InMemoryDepthChartService.java)
- Domain aggregate: [DepthChart.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/domain/DepthChart.java)
- Value objects: [Player.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/domain/Player.java), [Position.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/domain/Position.java)
- Formatter: [DepthChartFormatter.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/formatter/DepthChartFormatter.java)
- Exception: [DepthChartValidationException.java](/Users/xiaolingcui/nfl-depth-chart-manager/src/main/java/com/fanduel/depthchart/exception/DepthChartValidationException.java)

# DemoScenario Flow

## Plain Text Flow (Always Renderable)

```text
run()
 ├─ seedSampleDepthChartFromJson()
 │   ├─ load JSON from classpath (sampleDataPath)
 │   ├─ parseRows(input)
 │   │   ├─ require START_ARRAY
 │   │   └─ loop parseRow(parser) until END_ARRAY
 │   │       ├─ require START_OBJECT
 │   │       ├─ read: number, name, position, depth
 │   │       ├─ skip unknown fields
 │   │       └─ validate required fields -> DepthChartRow
 │   └─ for each row: addPlayerToDepthChart(position, Player, depth)
 ├─ create query Player objects
 ├─ printBackups(...) for multiple sample cases
 ├─ print full depth chart
 ├─ remove Mike Evans from LWR
 ├─ print removed list
 └─ print full depth chart after removal
```

## Mermaid Flow (If Supported)

```mermaid
flowchart TD
    A[run()] --> B[seedSampleDepthChartFromJson()]
    B --> B1[Load JSON from classpath]
    B1 --> B2[parseRows(input)]
    B2 --> B21[Check START_ARRAY]
    B21 --> B22[Loop: parseRow(parser)]
    B22 --> B23[Build DepthChartRow list]
    B23 --> B3[For each row: addPlayerToDepthChart(position, Player, depth)]
    B3 --> C[Create query Player objects]

    C --> D1[printBackups Tom Brady at QB]
    C --> D2[printBackups Jaelon Darden at LWR]
    C --> D3[printBackups Jaelon Darden at QB]
    C --> D4[printBackups Mike Evans at QB]
    C --> D5[printBackups Blaine Gabbert at QB]
    C --> D6[printBackups Kyle Trask at QB]

    D1 --> E[Print Full Depth Chart]
    D2 --> E
    D3 --> E
    D4 --> E
    D5 --> E
    D6 --> E

    E --> F[Remove Mike Evans from LWR]
    F --> G[Print removed list via formatPlayerList]
    G --> H[Print Full Depth Chart after removal]
```

# AGENTS

Java 21 / Gradle multi-module cellular automata project. Prefer `README.md` for human docs; this file is agent constraints.

## Modules

| Module | Role | UI / JavaFX? |
|--------|------|--------------|
| `core` | Engine: grids, neighborhoods, boundaries, LUTs, genetic CA, `IExporter`, `IStatsCollector` | **No** — keep publishable / UI-free |
| `display` | `ColorMap`, `ImageWriter`, image helpers (depends on `core`) | No JavaFX |
| `examples` | Concrete rules (`GameOfLife`, `HPP`, …) | No JavaFX |
| `cli` | Headless batch (`BatchCli`, collectors) | **No** — no JavaFX |
| `demo` | JavaFX app (`DemoApp`, `CanvasExporter`) | **Yes** — only place for JavaFX |

Do not add JavaFX (or demo-only types) to `core`, `display`, `examples`, or `cli`.

## Commands

```bash
./gradlew :demo:run          # JavaFX demo
./gradlew :cli:run --args='--ca game-of-life --steps 500 --collect density'  # headless batch
./gradlew :core:jar          # library jar
./gradlew :core:test         # core unit tests
./gradlew test               # all modules
```

JDK 21 required.

## Add a rule

1. Subclass `IntegerCellularAutomata2D` in `examples` (`cellularautomata.examples`).
2. Build a `Setup` (boundary + neighborhood); implement `applyRule(x, y, values)` — `values[0]` is the cell.
3. Wire into `demo` via `DemoApp` picker + factory + a `ColorMap` from `display`.

Export via `exportValues(IExporter)` — demo uses `CanvasExporter`; files via `ImageWriter`.

## Style

- Match existing Java style in the target module; minimal diffs.
- No unsolicited docs/tests/refactors.
- Prefer extending existing factories (`BoundaryFactory`, `NeighborhoodFactory`, `LookupTableFactory`) over parallel APIs.

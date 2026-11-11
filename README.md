# cellular-automata

Java 21 library for building and evolving 2D cellular automata, plus a JavaFX demo that renders them live.

Rules live in a UI-free `core` module. Display goes through `IExporter` (integer grids → pixels or files), so the same automata can drive Swing, JavaFX, or other front ends.

## Modules

```text
cellular-automata/
  core/   # publishable library (no JavaFX)
  demo/   # JavaFX app (depends on core)
```

| Module | Role |
|--------|------|
| **core** | Setup, neighborhoods, boundaries, lookup tables, example rules, evolutionary CA engines, `IExporter` / `ColorMap` / `ImageWriter` |
| **demo** | JavaFX shell: pick a CA, play / pause / step / reset, optional HPP side panel |

## Requirements

- JDK 21
- A display for the JavaFX demo

## Run the demo

```bash
./gradlew :demo:run
```

The window lists Game of Life, Falling Sand, Langton’s Ant, HPP, Parity, Time Tunnel, West, and Genetic CA. Use Play / Pause / Step / Reset, size, and speed. Selecting HPP shows reverse, add-particle, and iteration controls.

Build the library jar alone:

```bash
./gradlew :core:jar
```

## Add a cellular automaton

1. Subclass `IntegerCellularAutomata2D` in `core` (package `cellularautomata.examples` or your own).
2. Construct a `Setup` with the boundary and neighborhood you need.
3. Implement `applyRule(int x, int y, int[] values)` — `values[0]` is the cell; the rest are neighbors in neighborhood order.
4. Export state with `exportValues(IExporter)` (demo uses `CanvasExporter`; `ImageWriter` writes files).

Minimal sketch:

```java
public class MyRule extends IntegerCellularAutomata2D {
    public MyRule(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        // initialize cells…
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        // return next state for (x, y)
        return values[0];
    }
}
```

Wire it into the demo by adding an entry in `DemoApp`’s example picker and a factory branch that builds your rule + a `ColorMap`.

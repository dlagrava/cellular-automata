# Example rules

Concrete automata in package `cellularautomata.examples`. Most appear in the JavaFX demo picker and as CLI `--ca` ids (kebab-case of the name below).

## Integer rules

### Game of Life

Conway’s Life: B3/S23 on a binary grid. CLI: `game-of-life`.

### Falling Sand

Three states — void, sand, glass — with Margolus-style falling-sand dynamics. CLI: `falling-sand`.

### Forest Fire

Drossel–Schwabl model: empty / tree / burning, with tree growth probability `p` and lightning probability `f` (`f ≪ p`). CLI: `forest-fire`.

### Langton’s Ant

Classic Langton ant(s) on a black/white grid: turn and flip the cell under each ant. CLI: `langtons-ant`.

### HPP

Hardy–Pomeau–Pazzis lattice gas: particles on four lattice directions with collisions and solid walls. CLI: `hpp`.

### Parity

Each cell becomes the sum of its neighbors modulo 2. CLI: `parity`.

### Time Tunnel

Two-bit history rule: each cell stores the previous and current bit for reversible-style tunneling patterns. CLI: `time-tunnel`.

### West

Copies the east neighbor (Von Neumann); patterns drift left (−x). CLI: `west`.

### Cyclic

Griffeath cyclic CA: states `0 .. n−1` (default **n = 20**). A cell at `s` becomes `(s + 1) mod n` when at least `threshold` neighbors already hold that successor (default threshold `1`). Demo/CLI use PERIODIC + Moore. CLI: `cyclic`.

### Brian's Brain

Three states — off / on / dying. Off turns on with exactly two on neighbors; on → dying → off. Demo/CLI use PERIODIC + Moore. CLI: `brians-brain`.

### HodgePodge

Gerhardt–Schuster hodgepodge machine: healthy (`0`), infected (`1..n−1`), ill (`n`). Infection/ill neighbor counts drive infection; infected cells average the neighborhood and grow by `g`; ill cells recover. Defaults: `n=100`, `k1=2`, `k2=3`, `g=3`. Standalone UI: `./gradlew :demo:runHodgePodge`. Emoticon showpiece (small grid, face ladder): `./gradlew :demo:runEmoticonHodge`.

## Double-valued rules

Both use `DoubleCellularAutomata2D` (Von Neumann; prefer PERIODIC or FIXED).

### Diffusion

Discrete heat diffusion: each cell becomes the mean of itself and its N/E/S/W neighbors. CLI: `diffusion`.

### Height-Field Water

Toy height-field “water”: exported state is height `h`; a private velocity field drives ripples. Not a real shallow-water solver. CLI: `height-field-water`.

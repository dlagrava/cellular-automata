/**
 * Evolutionary cellular automata: each cell owns a lookup-table genome.
 *
 * <p>Fitness is resource consumption (cell value {@code 1 → 0}). Periodically,
 * below-mean cells replace their LUT with a crossover offspring of themselves
 * and their fittest neighbor; optional bit-flip mutation may follow.
 *
 * <p>Typical init order:
 * <ol>
 *   <li>construct {@link evolutionaryAutomata.GeneticCellularAutomata}</li>
 *   <li>{@link evolutionaryAutomata.GeneticCellularAutomata#initializeRules}</li>
 *   <li>{@link evolutionaryAutomata.GeneticCellularAutomata#selectCrossoverType}</li>
 *   <li>optional {@link evolutionaryAutomata.GeneticCellularAutomata#selectMutation}</li>
 * </ol>
 *
 * <p>Export encodes each cell's LUT as a 32-bit integer (optionally masked by
 * the cell value) so rule diversity is visible in the renderer.
 *
 * @author Daniel Lagrava
 */
package evolutionaryAutomata;

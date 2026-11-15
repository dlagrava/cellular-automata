package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

/**
 * Strategy that builds an offspring lookup table from two parent genomes.
 *
 * @author Daniel Lagrava
 */
public interface ICrossoverEngine {

    /**
     * Crossover {@code parent1} and {@code parent2}.
     *
     * @param parent1    first parent (typically the unfit cell)
     * @param parent2    second parent (typically the fitter neighbor)
     * @param parameters engine-specific knobs; trailing value may be a fitness bias in {@code [0, 1]}
     * @return offspring lookup table
     */
    LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double... parameters);
}

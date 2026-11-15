package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.util.Random;

/**
 * Probabilistic single-bit flip mutation on a lookup table.
 *
 * @author Daniel Lagrava
 */
public class MutationEngine {

    private final double mutationProbability;
    private final int tableSize;
    private final Random rng;

    /**
     * @param mutationProbability probability in {@code [0, 1]} that a LUT mutates
     * @param tableSize           number of LUT entries (flip index range)
     */
    public MutationEngine(double mutationProbability, int tableSize) {
        this(mutationProbability, tableSize, new Random());
    }

    /**
     * @param mutationProbability probability in {@code [0, 1]} that a LUT mutates
     * @param tableSize           number of LUT entries (flip index range)
     * @param rng                 shared random source
     */
    public MutationEngine(double mutationProbability, int tableSize, Random rng) {
        this.mutationProbability = mutationProbability;
        this.tableSize = tableSize;
        this.rng = rng;
    }

    /**
     * Decide whether mutation occurs and which table index to flip.
     *
     * @return index to mutate, or {@code -1} if no mutation
     */
    public int isGoingToMutate() {
        if (rng.nextDouble() < mutationProbability) {
            return rng.nextInt(tableSize);
        }
        return -1;
    }

    /**
     * Flip one random LUT entry (0↔1) with {@link #mutationProbability}.
     *
     * @param lut genome to mutate in place
     */
    public void lookUpTableMutation(LookupTable lut) {
        int site = isGoingToMutate();
        if (site < 0) {
            return;
        }
        lut.setNewValue(site, 1 - lut.getValue(site));
    }
}

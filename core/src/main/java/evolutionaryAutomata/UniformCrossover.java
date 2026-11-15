package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.util.Random;

/**
 * Per-entry uniform crossover: each LUT index is taken from parent1 or parent2
 * according to a mixing ratio (overridable by a trailing fitness bias).
 *
 * @author Daniel Lagrava
 */
public class UniformCrossover implements ICrossoverEngine {

    private final Random rng;

    public UniformCrossover() {
        this(new Random());
    }

    public UniformCrossover(Random rng) {
        this.rng = rng;
    }

    @Override
    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double... parameters) {
        double mixingRatio = parameters.length > 0 ? parameters[0] : 0.5;
        // Trailing bias overrides the configured mixing ratio when provided.
        if (parameters.length > 1) {
            mixingRatio = clamp01(parameters[parameters.length - 1]);
        }
        int[] newLookupTable = new int[parent1.getLength()];
        for (int i = 0; i < parent1.getLength(); i++) {
            if (rng.nextDouble() > mixingRatio) {
                newLookupTable[i] = parent2.getValue(i);
            } else {
                newLookupTable[i] = parent1.getValue(i);
            }
        }
        return new LookupTable(newLookupTable);
    }

    private static double clamp01(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}

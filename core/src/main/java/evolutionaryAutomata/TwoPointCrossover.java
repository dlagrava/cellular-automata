package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.util.Random;

/**
 * Two-point crossover: the segment between two cut indices is swapped; one of
 * the two complementary offspring is chosen, biased toward the fitter parent
 * when a bias parameter is supplied.
 *
 * @author Daniel Lagrava
 */
public class TwoPointCrossover implements ICrossoverEngine {

    private final Random rng;

    public TwoPointCrossover() {
        this(new Random());
    }

    public TwoPointCrossover(Random rng) {
        this.rng = rng;
    }

    @Override
    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double... parameters) {
        int length = parent1.getLength();
        int cutA = rng.nextInt(length);
        int cutB = rng.nextInt(length);
        int start = Math.min(cutA, cutB);
        int end = Math.max(cutA, cutB);

        int[] offspringTable1 = new int[length];
        int[] offspringTable2 = new int[length];
        for (int i = 0; i < length; i++) {
            if (i >= start && i < end) {
                offspringTable1[i] = parent2.getValue(i);
                offspringTable2[i] = parent1.getValue(i);
            } else {
                offspringTable1[i] = parent1.getValue(i);
                offspringTable2[i] = parent2.getValue(i);
            }
        }

        double preferFitter = parameters.length > 0 ? clamp01(parameters[parameters.length - 1]) : 0.5;
        if (rng.nextDouble() < preferFitter) {
            return new LookupTable(offspringTable2);
        }
        return new LookupTable(offspringTable1);
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

package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.util.Random;

/**
 * One-point crossover: a single cut index swaps the LUT tails; one of the two
 * complementary offspring is chosen, biased toward the fitter parent when a
 * bias parameter is supplied.
 *
 * @author Daniel Lagrava
 */
public class OnePointCrossover implements ICrossoverEngine {

    private final Random rng;

    public OnePointCrossover() {
        this(new Random());
    }

    public OnePointCrossover(Random rng) {
        this.rng = rng;
    }

    @Override
    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double... parameters) {
        int length = parent1.getLength();
        int cutPoint = rng.nextInt(length);
        int[] offspringTable1 = new int[length];
        int[] offspringTable2 = new int[length];

        for (int i = 0; i < cutPoint; i++) {
            offspringTable1[i] = parent1.getValue(i);
            offspringTable2[i] = parent2.getValue(i);
        }
        for (int i = cutPoint; i < length; i++) {
            offspringTable1[i] = parent2.getValue(i);
            offspringTable2[i] = parent1.getValue(i);
        }

        // Bias toward parent2 (fitter): prefer offspring2 (parent2 prefix).
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

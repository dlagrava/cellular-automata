package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Hodgepodge machine (Gerhardt–Schuster): healthy (0), infected (1..n-1),
 * ill (n). Infection/ill neighbor counts drive the healthy→infected transition;
 * infected cells average neighborhood state and grow by {@code g}; ill cells
 * recover to healthy.
 */
public class HodgePodge extends IntegerCellularAutomata2D {

    public static final int DEFAULT_STATES = 256;
    public static final int DEFAULT_THRESHOLD = 1;
    private static final int CONSTANT_K1 = 2;
    private static final int CONSTANT_K2 = 3;
    private static final int GROWTH_RATE = 5;

    private final int states;
    private final int threshold;

    public HodgePodge(int sizeX, int sizeY, Setup setup) {
        this(sizeX, sizeY, setup, DEFAULT_STATES, DEFAULT_THRESHOLD);
    }

    public HodgePodge(int sizeX, int sizeY, Setup setup, int states, int threshold) {
        super(sizeX, sizeY, setup);
        if (states < 2) {
            throw new IllegalArgumentException("states must be >= 2");
        }
        if (threshold < 1) {
            throw new IllegalArgumentException("threshold must be >= 1");
        }
        this.states = states;
        this.threshold = threshold;

        int[] values = new int[states + 1];
        for (int i = 0; i <= states; i++) {
            values[i] = i;
        }
        Tools.initializeRandom(cells, values);
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        // healthy: floor(infected/k1) + floor(ill/k2)
        if (values[0] == 0) {
            int infected = 0;
            int ill = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] > 0 && values[i] < states) {
                    infected++;
                } else if (values[i] == states) {
                    ill++;
                }
            }
            return (int) Math.floor(infected / (double) CONSTANT_K1)
                    + (int) Math.floor(ill / (double) CONSTANT_K2);
        }

        // infected: floor(S/(infected+ill+1)) + g, S = self + neighbors
        if (values[0] > 0 && values[0] < states) {
            int ill = 0;
            int infected = 0;
            int totalSum = values[0];
            for (int i = 1; i < values.length; i++) {
                totalSum += values[i];
                if (values[i] > 0 && values[i] < states) {
                    infected++;
                } else if (values[i] == states) {
                    ill++;
                }
            }
            int next = (int) Math.floor(totalSum / (double) (ill + infected + 1))
                    + GROWTH_RATE;
            return Math.min(next, states);
        }

        // ill --> healthy
        if (values[0] == states) {
            return 0;
        }
        throw new IllegalArgumentException("invalid cell state: " + values[0]);
    }
}

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

    public static final int DEFAULT_STATES = 100;
    public static final int DEFAULT_K1 = 2;
    public static final int DEFAULT_K2 = 3;
    public static final int DEFAULT_G = 3;

    private final int states;
    private final int k1;
    private final int k2;
    private final int g;

    public HodgePodge(int sizeX, int sizeY, Setup setup) {
        this(sizeX, sizeY, setup, DEFAULT_STATES, DEFAULT_K1, DEFAULT_K2, DEFAULT_G);
    }

    public HodgePodge(int sizeX, int sizeY, Setup setup, int states, int k1, int k2, int g) {
        super(sizeX, sizeY, setup);
        if (states < 2) {
            throw new IllegalArgumentException("states must be >= 2");
        }
        if (k1 < 1) {
            throw new IllegalArgumentException("k1 must be >= 1");
        }
        if (k2 < 1) {
            throw new IllegalArgumentException("k2 must be >= 1");
        }
        if (g < 1) {
            throw new IllegalArgumentException("g must be >= 1");
        }
        this.states = states;
        this.k1 = k1;
        this.k2 = k2;
        this.g = g;

        int[] values = new int[states + 1];
        for (int i = 0; i <= states; i++) {
            values[i] = i;
        }
        Tools.initializeRandom(cells, values);
    }

    public int getStates() {
        return states;
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        // healthy: infected/k1 + ill/k2
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
            return infected / k1 + ill / k2;
        }

        // infected: S / A + g  (A = infected neighbors including self)
        if (values[0] > 0 && values[0] < states) {
            // +1 for self
            int infected = 1;
            int totalSum = values[0];
            for (int i = 1; i < values.length; i++) {
                totalSum += values[i];
                if (values[i] > 0 && values[i] < states) {
                    infected++;
                }
            }
            int next = totalSum / infected + g;
            return Math.min(next, states);
        }

        // ill --> healthy
        if (values[0] == states) {
            return 0;
        }
        throw new IllegalArgumentException("invalid cell state: " + values[0]);
    }
}

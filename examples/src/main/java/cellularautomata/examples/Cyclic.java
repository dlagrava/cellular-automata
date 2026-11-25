package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Classic Griffeath cyclic CA: states {@code 0 .. n-1}; a cell at {@code s}
 * advances to {@code (s + 1) mod n} when at least {@code threshold} neighbors
 * already hold that successor value.
 */
public class Cyclic extends IntegerCellularAutomata2D {

    // Default states too large takes longer to converge
    public static final int DEFAULT_STATES = 20;
    public static final int DEFAULT_THRESHOLD = 1;

    private final int states;
    private final int threshold;

    public Cyclic(int sizeX, int sizeY, Setup setup) {
        this(sizeX, sizeY, setup, DEFAULT_STATES, DEFAULT_THRESHOLD);
    }

    public Cyclic(int sizeX, int sizeY, Setup setup, int states, int threshold) {
        super(sizeX, sizeY, setup);
        if (states < 2) {
            throw new IllegalArgumentException("states must be >= 2");
        }
        if (threshold < 1) {
            throw new IllegalArgumentException("threshold must be >= 1");
        }
        this.states = states;
        this.threshold = threshold;

        int[] values = new int[states];
        for (int i = 0; i < states; i++) {
            values[i] = i;
        }
        Tools.initializeRandom(cells, values);
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        int s = values[0];
        int successor = (s + 1) % states;
        int count = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] == successor) {
                count++;
            }
        }
        return count >= threshold ? successor : s;
    }
}

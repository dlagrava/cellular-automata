package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;

import java.util.Objects;
import java.util.Random;

/**
 * Drossel–Schwabl forest-fire model: empty / tree / burning with growth
 * probability {@code p} and lightning probability {@code f} ({@code f ≪ p}).
 */
public class ForestFire extends IntegerCellularAutomata2D {

    public static final int EMPTY = 0;
    public static final int TREE = 1;
    public static final int BURNING = 2;

    public static final double DEFAULT_P = 0.01;
    public static final double DEFAULT_F = 0.0001;

    private final double p;
    private final double f;
    private final Random rng;

    public ForestFire(int sizeX, int sizeY, Setup setup) {
        this(sizeX, sizeY, setup, DEFAULT_P, DEFAULT_F, new Random());
    }

    public ForestFire(int sizeX, int sizeY, Setup setup, double p, double f, Random rng) {
        super(sizeX, sizeY, setup);
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("p must be in [0, 1]");
        }
        if (f < 0.0 || f > 1.0) {
            throw new IllegalArgumentException("f must be in [0, 1]");
        }
        this.p = p;
        this.f = f;
        this.rng = Objects.requireNonNull(rng, "rng");
        // start empty; growth fills the grid
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        if (values[0] == BURNING) {
            return EMPTY;
        }
        if (values[0] == TREE) {
            for (int i = 1; i < values.length; i++) {
                if (values[i] == BURNING) {
                    return BURNING;
                }
            }
            return rng.nextDouble() < f ? BURNING : TREE;
        }
        return rng.nextDouble() < p ? TREE : EMPTY;
    }
}

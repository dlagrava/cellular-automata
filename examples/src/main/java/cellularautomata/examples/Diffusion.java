package cellularautomata.examples;

import cellularautomata.core.DoubleCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Discrete heat diffusion: each cell becomes the mean of its Von Neumann
 * neighborhood (self + N/E/S/W).
 * <p>
 * Caller should pass a {@link Setup} with
 * {@link cellularautomata.core.NeighborhoodFactory.NeighborhoodType#VONNEUMANN}.
 * Prefer {@code PERIODIC} or {@code FIXED} with value {@code 0.0} (cold wall /
 * heat sink).
 *
 * @author Daniel Lagrava
 */
public class Diffusion extends DoubleCellularAutomata2D {

    public Diffusion(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        int[] center = {sizeX / 2, sizeY / 2};
        Tools.initializeDisk(cells, 1.0, center, sizeX / 4);
    }

    @Override
    public double applyRule(int x, int y, double[] values) {
        double sum = 0.0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }
}

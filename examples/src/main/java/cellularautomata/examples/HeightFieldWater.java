package cellularautomata.examples;

import cellularautomata.core.DoubleCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Toy height-field “water”: exported state is height {@code h}; a private
 * vertical-ish velocity field drives ripples. Not a real shallow-water solver.
 * <p>
 * Caller should pass a {@link Setup} with
 * {@link cellularautomata.core.NeighborhoodFactory.NeighborhoodType#VONNEUMANN}
 * and {@code PERIODIC} or {@code FIXED} boundaries.
 *
 * @author Daniel Lagrava
 */
public class HeightFieldWater extends DoubleCellularAutomata2D {

    /** Resting water height (also used for colormap mid-range). */
    public static final double REST_HEIGHT = 0.5;
    /** Droplet peak height for splash contrast. */
    public static final double DROPLET_HEIGHT = 2.0;

    private static final double STIFFNESS = 0.5;
    private static final double DAMPING = 0.975;
    private static final double SOFT_BLEND = 0.02;
    private static final double MIN_HEIGHT = 0.0;
    private static final double MAX_HEIGHT = 1.75;

    private final double[][] velocity;

    public HeightFieldWater(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        velocity = new double[realSizeX][realSizeY];
        for (int iX = 0; iX < realSizeX; iX++) {
            for (int iY = 0; iY < realSizeY; iY++) {
                cells[iX][iY] = REST_HEIGHT;
            }
        }
        int[] center = {sizeX / 2, sizeY / 2};
        Tools.initializeDisk(cells, DROPLET_HEIGHT, center, Math.max(6, sizeX / 12));
    }

    @Override
    public double applyRule(int x, int y, double[] values) {
        double h = values[0];
        double neighborSum = 0.0;
        for (int i = 1; i < values.length; i++) {
            neighborSum += values[i];
        }
        int neighborCount = values.length - 1;
        double avg = neighborSum / neighborCount;

        velocity[x][y] += (avg - h) * STIFFNESS;
        velocity[x][y] *= DAMPING;

        double next = h + velocity[x][y];
        next = next * (1.0 - SOFT_BLEND) + avg * SOFT_BLEND;
        if (next < MIN_HEIGHT) {
            next = MIN_HEIGHT;
            velocity[x][y] = 0.0;
        } else if (next > MAX_HEIGHT) {
            next = MAX_HEIGHT;
            velocity[x][y] = 0.0;
        }
        return next;
    }
}

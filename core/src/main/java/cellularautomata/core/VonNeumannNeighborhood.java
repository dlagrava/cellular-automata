package cellularautomata.core;

/**
 * Standard Von Neumann neighborhood encoded as
 * <pre>
 *  2
 * 301
 *  4
 * </pre>
 * with [0]=self, [1]=east, [2]=north(+y), [3]=west, [4]=south(-y).
 *
 * <p>{@link #getNeighborIndices(int, int)} returns a reusable buffer valid only
 * until the next call on this instance.
 *
 * @author Daniel Lagrava
 */
public class VonNeumannNeighborhood implements INeighborhood {

    private static final int[][] OFFSETS = {
            {0, 0},
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
    };

    private final int[][] neighbors = new int[OFFSETS.length][2];

    public int[][] getNeighborIndices(int x, int y) {
        for (int i = 0; i < OFFSETS.length; i++) {
            neighbors[i][0] = x + OFFSETS[i][0];
            neighbors[i][1] = y + OFFSETS[i][1];
        }
        return neighbors;
    }

    public int getTotalNeighborNumber() {
        return OFFSETS.length;
    }

    public int getRadius() {
        return 1;
    }
}

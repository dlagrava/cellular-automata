/*
 * No licence
 */

package cellularautomata.core;

/**
 * @author Daniel Lagrava
 */
public interface INeighborhood {

    /**
     * Neighbors of location (i, j). Implementations may return a reused buffer
     * valid only until the next call on the same instance — consume immediately.
     *
     * @param i
     * @param j
     * @return neighbor coordinates; row {@code k} is {@code {x, y}}
     */
    int[][] getNeighborIndices(int i, int j);

    /**
     * @return number of neighbors in the neighborhood. Useful to iterate through them
     */
    int getTotalNeighborNumber();

    /**
     * @return radius of the neighborhood. Useful for boundary conditions
     */
    int getRadius();


}

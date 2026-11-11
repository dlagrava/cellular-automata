/*
 * No licence
 */

package cellularautomata.core;

/**
 * @author Daniel Lagrava
 */
public interface INeighborhood {

    /**
     * Given a neighborhood implementation, return the neighbors of location (i,j)
     *
     * @param i
     * @param j
     * @return
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

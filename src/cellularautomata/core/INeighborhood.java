/*
 * No licence
 */

package cellularautomata.core;

/**
 *
 * @author Daniel Lagrava
 */
public interface INeighborhood {
    
    /**
     * 
     * @param x
     * @param y
     * @return
     */
    int[][] getNeighborIndices(int x, int y);
    
    /**
     * 
     * @return
     */
    int getTotalNeighborNumber();
    
    /**
     * 
     * @return
     */
    int getRadius();
    
     
    
    
}

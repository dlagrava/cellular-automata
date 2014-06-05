/*
 * No licence
 */

package cellularautomata.core;

/**
 * This is an implementation of the Moore neighborhood or the D2Q9 neighborhood.
 * The convention for the neighbors is the following:
 *     615
 *     204
 *     738  
 * @author Daniel Lagrava
 */
public class MooreNeighborhood implements INeighborhood {

    int[][] neighbors;
    
    private final int NEIGHBORS = 9;
    private final int RADIUS = 1;
    
    public MooreNeighborhood(int x, int y){
        neighbors = new int[NEIGHBORS][2];
        computeNeighbors(x,y);
    }
    
    public int[][] getNeighborIndices(int x, int y) {
        return neighbors;
    }

    public int getTotalNeighborNumber() {
        return NEIGHBORS;
    }

    public int getRadius() {
        return RADIUS;
    }
    
    // The computation of the neighbors
    private void computeNeighbors(int x, int y) {
        // the N, W, S and E neighbors
        neighbors[0][0] = x;
        neighbors[0][1] = y;
        neighbors[1][0] = x+1;
        neighbors[1][1] = y;
        neighbors[2][0] = x;
        neighbors[2][1] = y+1;
        neighbors[3][0] = x-1;
        neighbors[3][1] = y;
        neighbors[4][0] = x;
        neighbors[4][1] = y-1;
        // the diagonal neighbors
        neighbors[5][0] = x+1;
        neighbors[5][1] = y+1;
        neighbors[6][0] = x-1;
        neighbors[6][1] = y+1;
        neighbors[7][0] = x-1;
        neighbors[7][1] = y-1;
        neighbors[8][0] = x+1;
        neighbors[8][1] = y-1;
    }

}

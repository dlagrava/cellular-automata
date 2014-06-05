/*
 * No licence
 */

package cellularautomata.core;

/**
 *This class implements an standard Von Neumman neighborhood encoded as   
 *      2
 *     301
 *      4 
 * @author Daniel Lagrava
 */
public class VonNeumannNeighborhood implements INeighborhood {

    int[][] neighbors;
    private final int NEIGHBORS = 5;
    private final int RADIUS = 1;
    
    public VonNeumannNeighborhood(int x, int y){
        neighbors = new int[NEIGHBORS][2];
        computeNeighbors(x,y);
    }
    
    public int[][] getNeighborIndices(int x, int y) {
        return neighbors;
    }

    public int getTotalNeighborNumber() {
        return NEIGHBORS;
    }
    
    private void computeNeighbors(int x, int y){
        
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
    }

    public int getRadius() {
        return RADIUS;
    }

}

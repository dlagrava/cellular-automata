/*
 * No licence
 */

package cellularautomata.core;

/**
 * A boundary containing a fixed value. The value can be modified by using the setValue()
 * method, so that conditions that vary with the time can be implemented.
 * @author Daniel Lagrava
 */
public class FixedBoundary implements IBoundary {

    private int value;
    private int width;
    
    public FixedBoundary(INeighborhood neighborhood, int value){
        this.value = value;
        width = neighborhood.getRadius();
    }
    
    public int getWidth() {
        return width;
    }

    /**
     * Fill the corresponding "boundary" sites in the CA.
     * @param ca
     */
    public void computeBorders(int[][] ca) {
        int sizeX = ca.length;
        int sizeY = ca[0].length;
        
        for (int iL = 0; iL < width; iL++){
            for (int iX = 0; iX < sizeX; iX++){
                ca[iX][iL] = value;
                ca[iX][sizeY-1-iL] = value;
            }
            for (int iY = 0; iY < sizeY; iY++){
                ca[iL][iY] = value;
                ca[sizeX-1-iL][iY] = value;
            }

        }
    }

    /**
     * Change the value on the boundary
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }


}

/*
 * No licence
 */

package cellularautomata.core;

/**
 *
 * @author Daniel Lagrava
 */
public class PeriodicBoundary implements IBoundary {

    int width;
    
    public PeriodicBoundary(int width_){
        width = width_;
    }
    
    public PeriodicBoundary(INeighborhood neighborhood){
        width = neighborhood.getRadius();
    } 
    
    public int getWidth() {
        return width;
    }

    public void computeBorders(int[][] ca) {
        int sizeX = ca.length;
        int sizeY = ca[0].length;
        // north and south border exchange their values
        for (int iL = 0; iL < width; iL++){
            for (int iX = 0; iX < sizeX; iX++){
                ca[iX][iL] = ca[iX][sizeY-iL-2];
                ca[iX][sizeY-1-iL] = ca[iX][iL+1];
            }
            // then western and eastern values
            for (int iY = 0; iY < sizeY; iY++){
                ca[iL][iY] = ca[sizeX-iL-2][iY];
                ca[sizeX-1-iL][iY] = ca[iL+1][iY];
            }

        }
                
    }

    public void setValue(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

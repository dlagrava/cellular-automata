/*
 * No licence
 */

package cellularautomata.core;

/**
 *
 * @author Daniel Lagrava
 */
public class ReflexiveBoundary implements IBoundary {
    
    private int width;
    
    public ReflexiveBoundary(INeighborhood neighborhood){
        width = neighborhood.getRadius();
    }
    
    public int getWidth() {
        return width;
    }

    public void computeBorders(int[][] ca) {
        
        // for each site of the border
        for (int i = 0; i < width; i++){
            for (int iX = 0; iX < ca.length; iX++){
                ca[iX][width-i-1] = ca[iX][width+i+1];
                ca[iX][ca[iX].length-i-1] = ca[iX][ca[iX].length-width-i-2];
            }
            
            for (int iY = 0; iY < ca[0].length; iY++){
                ca[width-i-1][iY] = ca[width+i+1][iY];
                ca[ca.length-i-1][iY] = ca[ca.length-i-2][iY];
            }
        }
        
        
    }

    public void setValue(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

/*
 * No licence
 */

package cellularautomata.core;

/**
 * Class that assigns a particular setup to the cellular automata. 
 * @author Daniel Lagrava
 */
public class Setup {
    
    private IBoundary boundary; // Boundary type
    private INeighborhood[][] neighborhood; // The neighbor indexes
    
    private int realSizeX; // size of the CA in x + border cells
    private int realSizeY; // size of the CA en y + border cells

    /**
     * Constructor for a Setup object.
     * @param boundaryType must be one of the available IBoundary implementing classes
     * @param n must be one of the available INeighborhood implementing classes
     * @param sizeX desired size of the automata in x
     * @param sizeY desired size of the automata in y
     * @param parameters if there is need for additional information only (example: FixedBoundary needs a value to get fixed to).
     */
    public Setup(BoundaryFactory.BoundaryType boundaryType, NeighborhoodFactory.NeighborhoodType n, int sizeX, int sizeY, double ... parameters){
      // selecting among the disponible boundaries
      switch (boundaryType){
          case PERIODIC:
              boundary = BoundaryFactory.generatePeriodicBoundary(NeighborhoodFactory.generate(n, 0, 0));
              break;
          case FIXED:
              boundary = BoundaryFactory.generateFixedBoundary(NeighborhoodFactory.generate(n, 0, 0), (int) parameters[0]);
              break;
          case REFLEXIVE:
              boundary = BoundaryFactory.generateReflexiveBoundary(NeighborhoodFactory.generate(n, 0, 0));
              break;
          default:
              throw new IllegalArgumentException("Unknown Boundary Type: "+boundaryType);
      }

      // computation of internal table CA sizes
      realSizeX = sizeX+2*boundary.getWidth();
      realSizeY = sizeY+2*boundary.getWidth();
      // For each cell we use a neighbor
      neighborhood = new INeighborhood[realSizeX][realSizeY]; 
      // computation of the indexes per cell
      for (int iX = 0; iX < realSizeX; iX++){
            for (int iY = 0; iY < realSizeY; iY++){   
                neighborhood[iX][iY] = NeighborhoodFactory.generate(n, iX, iY);
            }
      }
    }

    /**
     * Count the number of cells in the boundary (depends on the neighborhood)
     * @return
     */
    public int getBoundaryWidth() {
        return boundary.getWidth();
    }

    /**
     * Count the number of neighbors (for example Von Neumann neighborhood = 4 neighbors)
     * @return
     */
    public int getNeighborNumber() {
        return neighborhood[0][0].getTotalNeighborNumber();
    }

    /**
     * For a cell in (x,y) get the coordinates of its neighbors
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return a bidimensional table, for instance table[i][0] = coordinate x of the
     * i neighbor and table[i][1] = coordinate y of the i neighbor. i varies according
     * to the neighbor number.
     */
    public int[][] getNeighborIndices(int x, int y) {
        return neighborhood[x][y].getNeighborIndices(x, y);
    }


    public void computeBorders(int[][] ca) {
        boundary.computeBorders(ca);
    }

    /**
     * For internal iteration over the CA. Not interesting for the end-user.
     * @return
     */
    public int getX(){
        return realSizeX;
    }

    /**
     * For internal iteration over the CA. Not interesting for the end-user.
     * @return
     */
    public int getY(){
        return realSizeY;
    }

}

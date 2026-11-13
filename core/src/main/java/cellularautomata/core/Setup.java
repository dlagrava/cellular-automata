package cellularautomata.core;

/**
 * Class that assigns a particular setup to the cellular automata.
 *
 * @author Daniel Lagrava
 */
public class Setup {

    private final IBoundary boundary;
    /** Shared Moore / Von Neumann; unused for Margolus. */
    private final INeighborhood neighborhood;
    /** Per-cell Margolus instances; null for Moore / Von Neumann. */
    private final INeighborhood[][] margolusGrid;

    private final int realSizeX;
    private final int realSizeY;

    /**
     * Constructor for a Setup object.
     *
     * @param boundaryType must be one of the available IBoundary implementing classes
     * @param n            must be one of the available INeighborhood implementing classes
     * @param sizeX        desired size of the automata in x
     * @param sizeY        desired size of the automata in y
     * @param parameters   if there is need for additional information only (example: FixedBoundary needs a value to get fixed to).
     */
    public Setup(BoundaryFactory.BoundaryType boundaryType, NeighborhoodFactory.NeighborhoodType n, int sizeX, int sizeY, double... parameters) {
        INeighborhood prototype;
        if (n == NeighborhoodFactory.NeighborhoodType.MARGOLUS) {
            prototype = NeighborhoodFactory.generate(n, 0, 0);
            neighborhood = null;
        } else {
            prototype = NeighborhoodFactory.generate(n);
            neighborhood = prototype;
        }

        switch (boundaryType) {
            case PERIODIC:
                boundary = BoundaryFactory.generatePeriodicBoundary(prototype);
                break;
            case FIXED:
                boundary = BoundaryFactory.generateFixedBoundary(prototype, (int) parameters[0]);
                break;
            case REFLEXIVE:
                boundary = BoundaryFactory.generateReflexiveBoundary(prototype);
                break;
            default:
                throw new IllegalArgumentException("Unknown Boundary Type: " + boundaryType);
        }

        realSizeX = sizeX + 2 * boundary.getWidth();
        realSizeY = sizeY + 2 * boundary.getWidth();

        if (n == NeighborhoodFactory.NeighborhoodType.MARGOLUS) {
            margolusGrid = new INeighborhood[realSizeX][realSizeY];
            for (int iX = 0; iX < realSizeX; iX++) {
                for (int iY = 0; iY < realSizeY; iY++) {
                    margolusGrid[iX][iY] = NeighborhoodFactory.generate(n, iX, iY);
                }
            }
        } else {
            margolusGrid = null;
        }
    }

    /**
     * Count the number of cells in the boundary (depends on the neighborhood)
     *
     * @return
     */
    public int getBoundaryWidth() {
        return boundary.getWidth();
    }

    /**
     * Count the number of neighbors (for example Von Neumann neighborhood = 4 neighbors)
     *
     * @return
     */
    public int getNeighborNumber() {
        if (margolusGrid != null) {
            return margolusGrid[0][0].getTotalNeighborNumber();
        }
        return neighborhood.getTotalNeighborNumber();
    }

    /**
     * For a cell in (x,y) get the coordinates of its neighbors
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return a bidimensional table, for instance table[i][0] = coordinate x of the
     * i neighbor and table[i][1] = coordinate y of the i neighbor. i varies according
     * to the neighbor number. For Moore / Von Neumann the array is reused; do not
     * retain it across calls.
     */
    public int[][] getNeighborIndices(int x, int y) {
        if (margolusGrid != null) {
            return margolusGrid[x][y].getNeighborIndices(x, y);
        }
        return neighborhood.getNeighborIndices(x, y);
    }


    public void computeBorders(int[][] ca) {
        boundary.computeBorders(ca);
    }

    /**
     * For internal iteration over the CA. Not interesting for the end-user.
     *
     * @return
     */
    public int getX() {
        return realSizeX;
    }

    /**
     * For internal iteration over the CA. Not interesting for the end-user.
     *
     * @return
     */
    public int getY() {
        return realSizeY;
    }

}

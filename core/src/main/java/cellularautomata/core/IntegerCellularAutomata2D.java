/*
 *
 */

package cellularautomata.core;


/**
 * A simple implementation of an integer valued CA.
 *
 * @author Daniel Lagrava
 */
public abstract class IntegerCellularAutomata2D implements ICellularAutomata {

    // the size of the Cellular Automata
    protected int sizeX;
    protected int sizeY;

    // the size of the cellular automata including the borders
    protected int realSizeX;
    protected int realSizeY;

    // borders to iterate only inside the CA
    protected int xStart;
    protected int yStart;
    protected int xEnd;
    protected int yEnd;

    // the containers of the cell information
    protected int[][] cells;
    // will contain the neighbor information
    protected int[][][] temporaryArrays;

    // all information about the setup (i.e. Neighborhood and Boundary) are here
    protected Setup setup;

    /**
     * Contructor for an integer valued CellularAutomata
     *
     * @param sizeX_
     * @param sizeY_
     * @param setup_ a Setup object (containing the information about border and neighborhood)
     */
    public IntegerCellularAutomata2D(int sizeX_, int sizeY_, Setup setup_) {

        sizeX = sizeX_;
        sizeY = sizeY_;
        setup = setup_;
        // computing the real sizes of our tables
        realSizeX = setup_.getX();
        realSizeY = setup_.getY();
        // setting the values to iterate only inside the CA,  not the borders
        xStart = yStart = setup_.getBoundaryWidth();
        xEnd = realSizeX - xStart;
        yEnd = realSizeY - yStart;

        // allocation of memory for the CA and temporary arrays
        cells = new int[realSizeX][realSizeY];
        temporaryArrays = new int[realSizeX][realSizeY][setup.getNeighborNumber()];
    }


    public void copyNeighborValues() {

        // fill the information in the border cells
        setup.computeBorders(cells);

        // then fill the temporary arrays cell corresponding to the neighbors
        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                int[][] neighbors = setup.getNeighborIndices(iX, iY);
                propagateValues(iX, iY, neighbors);
            }
        }
    }

    public void collision() {
        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                cells[iX][iY] = applyRule(iX, iY, temporaryArrays[iX][iY]);
            }
        }
    }

    /**
     * Method to override if postprocessing is needed
     */
    public void postProcessing() {
        // NOT IMPLEMENTED
    }

    public void propagation() {
        // NOT IMPLEMENTED
    }

    /**
     * All in one CA step
     */
    public void collisionAndPropagation() {
        copyNeighborValues();
        collision();
        propagation();
        postProcessing();
    }

    /**
     * Return the x size of the CA (user)
     *
     * @return
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Return the y size of the CA (user)
     *
     * @return
     */
    public int getSizeY() {
        return sizeY;
    }

    private void propagateValues(int x, int y, int[][] neighbors) {
        for (int iK = 0; iK < temporaryArrays[x][y].length; iK++) {
            temporaryArrays[x][y][iK] = cells[neighbors[iK][0]][neighbors[iK][1]];
        }
    }

    /**
     * This method must apply the rule intended for the evolution to the (x,y) cell
     * that has the neighbor information in the values multidimensional array.
     * Coordinates are storage indices (including boundary padding), matching
     * {@link Setup#getNeighborIndices(int, int)}; use {@link #getCell}/{@link #setCell}.
     *
     * @param x storage x
     * @param y storage y
     * @param values neighbor values
     * @return next cell value
     */
    public abstract int applyRule(int x, int y, int[] values);

    /**
     * Sends values from the CA to the writer.
     *
     * @param writer a class implementing the IExporter interface
     */
    public void exportValues(IExporter writer) {
        writer.writeIntegerValues(copyValues());
    }

    /**
     * Snapshot of logical interior cells {@code [0, sizeX) × [0, sizeY)}.
     *
     * @return a new matrix; does not include boundary padding
     */
    public int[][] copyValues() {
        int[][] values = new int[sizeX][sizeY];
        int width = setup.getBoundaryWidth();
        for (int iX = width; iX < realSizeX - width; iX++) {
            for (int iY = width; iY < realSizeY - width; iY++) {
                values[iX - width][iY - width] = cells[iX][iY];
            }
        }
        return values;
    }

    /**
     * Replace logical interior cells from {@code values}.
     * Requires {@code values.length == sizeX} and each row length {@code sizeY}.
     *
     * @param values row-major logical grid
     */
    public void loadValues(int[][] values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        if (values.length != sizeX) {
            throw new IllegalArgumentException(
                    "expected " + sizeX + " rows, got " + values.length);
        }
        for (int x = 0; x < sizeX; x++) {
            if (values[x] == null || values[x].length != sizeY) {
                int len = values[x] == null ? -1 : values[x].length;
                throw new IllegalArgumentException(
                        "row " + x + ": expected " + sizeY + " cols, got " + len);
            }
            for (int y = 0; y < sizeY; y++) {
                setValue(x, y, values[x][y]);
            }
        }
    }

    /**
     * Set a cell in logical user coordinates {@code [0, sizeX) × [0, sizeY)}.
     *
     * @param x logical x
     * @param y logical y
     * @param value cell value
     */
    public void setValue(int x, int y, int value) {
        requireLogicalInBounds(x, y);
        int width = setup.getBoundaryWidth();
        cells[x + width][y + width] = value;
    }

    /**
     * Get a cell in logical user coordinates {@code [0, sizeX) × [0, sizeY)}.
     *
     * @param x logical x
     * @param y logical y
     * @return cell value
     */
    public int getValue(int x, int y) {
        requireLogicalInBounds(x, y);
        int width = setup.getBoundaryWidth();
        return cells[x + width][y + width];
    }

    /**
     * Set a cell in padded storage coordinates (for rules / propagation).
     */
    protected void setCell(int x, int y, int value) {
        cells[x][y] = value;
    }

    /**
     * Get a cell in padded storage coordinates (for rules / propagation).
     */
    protected int getCell(int x, int y) {
        return cells[x][y];
    }

    private void requireLogicalInBounds(int x, int y) {
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY) {
            throw new IndexOutOfBoundsException(
                    "logical (" + x + "," + y + ") outside [0," + sizeX + ")×[0," + sizeY + ")");
        }
    }

}

/*
 * 
 */

package cellularautomata.core;
import cellularautomata.display.*;

/**
 * A simple implementation of an integer valued CA. 
 * @author Daniel Lagrava
 */
public abstract class IntegerCellularAutomata2D implements ICellularAutomata {
        
    // the size of the Cellular Automata
    protected  int sizeX;
    protected  int sizeY;
    
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
     * @param sizeX_
     * @param sizeY_
     * @param setup_ a Setup object (containing the information about border and neighborhood)
     */
    public IntegerCellularAutomata2D(int sizeX_, int sizeY_, Setup setup_){
        
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
        for (int iX = xStart; iX < xEnd; iX++){
            for (int iY = yStart; iY < yEnd; iY++){
                int[][] neighbors = setup.getNeighborIndices(iX, iY);
                propagateValues(iX, iY, neighbors);
            }
        }
    }

    public void collision() {
        for (int iX = xStart; iX < xEnd; iX++){
            for (int iY = yStart; iY < yEnd; iY++){
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

    public void propagation(){
        // NOT IMPLEMENTED
    }

    /**
     * All in one CA step
     */
    public void collisionAndPropagation(){
        copyNeighborValues();
        collision();
        propagation();
        postProcessing();
    }
    
    /**
     * Return the x size of the CA (user)
     * @return
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Return the y size of the CA (user)
     * @return
     */
    public int getSizeY() {
        return sizeY;
    }

    private void propagateValues(int x, int y, int[][] neighbors) {
        for (int iK = 0; iK < temporaryArrays[x][y].length; iK++){
            temporaryArrays[x][y][iK] = cells[neighbors[iK][0]][neighbors[iK][1]];
        }
    }
    
    /**
     * This method must apply the rule intended for the evolution to the (x,y) cell
     * that has the neighbor information in the values multidimensional array.
     * @param x
     * @param y
     * @param values
     * @return
     */
    public abstract int applyRule(int x, int y, int[] values);

    /**
     * Sends values from the CA to the writer.
     * @param writer a class implementing the IExporter interface
     */
    public void exportValues(IExporter writer){
        // table that contains the values to be exported
        int[][] values = new int [sizeX][sizeY];
        // we only need the internal cells not the boundary-added cells
        int width = setup.getBoundaryWidth();
        // loop to retrieve the values
        for (int iX = width; iX < realSizeX-width; iX++){
            for (int iY = width; iY < realSizeY-width; iY++){
                values[iX-width][iY-width] = cells[iX][iY];
            }
        }
        // send the values to the writer
        writer.writeIntegerValues(values);
    }

    /**
     * Modify the value of the (x,y) cell of the CA.
     * @param x
     * @param y
     * @param value
     */
    public void setValue(int x, int y, int value){
        cells[x][y] = value;
    }

    /**
     * Retrieve the value of the (x,y) cell of the CA.
     * @param x
     * @param y
     * @return
     */
    public int getValue(int x, int y) {
        return cells[x][y];
    }

}

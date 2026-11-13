package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

import java.util.Random;

/**
 * Implementation of HPP gaz automata. Each site has 4 values that reflect the
 * for possible lattice directions.
 *
 * @author lagravas
 */
public class HPP extends IntegerCellularAutomata2D {

    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST = 4;
    public static final int WEST = 8;
    public static final int SOLID = 16;

    /**
     * Get the value on a given direction
     *
     * @param value
     * @param direction is either NORTH, SOUTH, EAST, WEST
     * @return
     */
    private int getDirectionValue(int value, int direction) {
        return (value & direction) / direction;
    }

    public HPP(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        //setValue(sizeX/3, sizeY/2, NORTH + SOUTH + EAST + WEST);
        int center[] = {sizeX / 2, sizeY / 2};
        Tools.initializeDisk(cells, NORTH + SOUTH + EAST + WEST, center, sizeX / 3);
        Tools.initializeEmptySquare(cells, SOLID, 2, 2, sizeX - 3);

    }

    /**
     * Implement the collision change of directions in the HPP model.
     *
     * @param x
     * @param y
     * @param values
     * @return
     */
    @Override
    public int applyRule(int x, int y, int[] values) {

        if (getCell(x, y) == NORTH + SOUTH) {
            return EAST + WEST;
        }

        if (getCell(x, y) == EAST + WEST) {
            return NORTH + SOUTH;
        }

        return getCell(x, y);
    }

    /**
     * Implements the propagation step of the particle values to the neighbors. It must
     * be used with a Von Neumann neighborhood (neighbors are stored as )
     */
    @Override
    public void propagation() {

        for (int iX = xStart; iX < xEnd; ++iX) {
            for (int iY = yStart; iY < yEnd; ++iY) {
                if (getDirectionValue(getCell(iX, iY), SOLID) == 0) {
                    int[] neighborValues = temporaryArrays[iX][iY];
                    int N = getDirectionValue(neighborValues[4], NORTH);
                    int S = getDirectionValue(neighborValues[2], SOUTH);
                    int E = getDirectionValue(neighborValues[3], EAST);
                    int W = getDirectionValue(neighborValues[1], WEST);
                    int newValue = N * NORTH + S * SOUTH + E * EAST + W * WEST;
                    setCell(iX, iY, newValue);
                }
                // case of a solid place
                else {
                    // all velocities should be inversed
                    int[] neighborValues = temporaryArrays[iX][iY];
                    int N = getDirectionValue(neighborValues[4], NORTH);
                    int S = getDirectionValue(neighborValues[2], SOUTH);
                    int E = getDirectionValue(neighborValues[3], EAST);
                    int W = getDirectionValue(neighborValues[1], WEST);
                    int newValue = N * SOUTH + S * NORTH + E * WEST + W * EAST + SOLID;
                    setCell(iX, iY, newValue);
                }
            }
        }
    }

    /*
        @Override
        public void exportValues(IExporter writer){
            // table that contains the values to be exported
            int[][] values = new int [sizeX][sizeY];
            // we only need the internal cells not the boundary-added cells
            int width = setup.getBoundaryWidth();
            // loop to retrieve the values
            for (int iX = width; iX < realSizeX-width; iX++){
                for (int iY = width; iY < realSizeY-width; iY++){
                    values[iX-width][iY-width] = cells[iX][iY] > 0 ? NORTH + SOUTH + EAST + WEST : 0;
                }
            }
            // send the values to the writer
            writer.writeIntegerValues(values);
        }
    */
    public void reverse() {
        for (int iX = xStart; iX < xEnd; ++iX) {
            for (int iY = yStart; iY < yEnd; ++iY) {
                int[] neighborValues = temporaryArrays[iX][iY];
                int N = getDirectionValue(neighborValues[0], NORTH);
                int S = getDirectionValue(neighborValues[0], SOUTH);
                int E = getDirectionValue(neighborValues[0], EAST);
                int W = getDirectionValue(neighborValues[0], WEST);
                if (getDirectionValue(getCell(iX, iY), SOLID) == 0) {
                    int newValue = N * SOUTH + S * NORTH + E * WEST + W * EAST;
                    setCell(iX, iY, newValue);
                }
                // case of a solid place
                else {
                    // all velocities should be inversed
                    int newValue = N * NORTH + S * SOUTH + E * EAST + W * WEST + SOLID;
                    setCell(iX, iY, newValue);
                }
            }
        }

    }

    /**
     * The collision and propagation is a little different than in the other CA, this
     * time we apply a collision, we copy new values to the neighbors and finally we
     * propagate the values.
     */
    @Override
    public void collisionAndPropagation() {
        collision();
        copyNeighborValues();
        propagation();
    }

    public void addParticle() {
        Random rng = new Random();
        int xCoordinate = rng.nextInt(sizeX);
        int yCoordinate = rng.nextInt(sizeY);
        int particles = SOLID - 1;

        setValue(xCoordinate, yCoordinate, particles);
        System.err.println("Particle setted at " + xCoordinate + " " + yCoordinate);

    }
}

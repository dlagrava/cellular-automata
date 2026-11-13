package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Implementation of the time tunnel rule. Each cell has a 2-bit value
 * which saves the t-1 and t values.
 *
 * @author Daniel Lagrava
 */
public class TimeTunnel extends IntegerCellularAutomata2D {

    public TimeTunnel(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        // initialize a square
        int cornerX = sizeX / 3;
        int cornerY = sizeY / 3;
        int sideLength = Math.min(sizeX / 3, sizeY / 3);
        Tools.initializeFilledSquare(cells, 1, cornerX, cornerY, sideLength);
        /*
        int[] center = {sizeX/2,sizeY/2};
        int radius = Math.min(sizeX/4,sizeY/4);
        Tools.initializeDisk(cells, 3, center,radius);*/
    }

    /**
     * Implementation of the time tunnel rule. We use 2 bits, the bit 1 for the value at time t
     * and the bit 0 for the value at time t-1
     *
     * @param x
     * @param y
     * @param values
     * @return
     */
    @Override
    public int applyRule(int x, int y, int[] values) {
        int sum = 0;
        int ct = (values[0] & 2) >> 1; // current values (time t)
        int ctminus1 = values[0] & 1; // values at time t-1

        // we sum the value at t of all neighborhood
        for (int iK = 0; iK < values.length; iK++) {
            sum += (values[iK] & 2) >> 1;
        }

        if (sum == 0 || sum == 5) // if we get 0 or 5
            // we get t-1 value and store t value
            return (ctminus1 << 1) + ct;
        else
            // otherwise
            return ((1 - ctminus1) << 1) + ct;
    }

}

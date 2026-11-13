package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * @author Daniel Lagrava
 */
public class Parity extends IntegerCellularAutomata2D {

    public Parity(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        Tools.initializeFilledSquare(cells, 1, sizeX / 3, sizeY / 3, sizeX / 3);
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        int res = 0;
        for (int iK = 1; iK < values.length; iK++) {
            res += values[iK];
        }
        return res % 2;
    }
}

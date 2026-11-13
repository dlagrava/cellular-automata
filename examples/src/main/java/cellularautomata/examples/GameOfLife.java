package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * @author Daniel Lagrava
 */
public class GameOfLife extends IntegerCellularAutomata2D {

    public GameOfLife(int sizeX, int sizeY, Setup setup, int randomCells) {
        super(sizeX, sizeY, setup);

        int[] values = {0, 0, 0, 0, 0, 1}; // approximately 1/6 cells to 1
        Tools.initializeRandom(cells, values);

    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        int sum = 0;
        for (int iK = 1; iK < values.length; iK++) {
            sum += values[iK];
        }
        if (sum < 2) return 0;
        if (sum == 3 && values[0] == 0) return 1;
        if (sum == 2 || sum == 3) return values[0];
        if (sum > 3) return 0;
        return 0;
    }
}

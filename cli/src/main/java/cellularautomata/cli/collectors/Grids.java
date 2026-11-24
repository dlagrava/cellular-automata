package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IntegerCellularAutomata2D;

/**
 * Shared cast helpers for collectors that need cell access.
 */
final class Grids {

    private Grids() {
    }

    static IntegerCellularAutomata2D requireInteger(ICellularAutomata ca) {
        if (ca instanceof IntegerCellularAutomata2D grid) {
            return grid;
        }
        throw new IllegalArgumentException(
                "Collector requires IntegerCellularAutomata2D, got " + ca.getClass().getName());
    }
}

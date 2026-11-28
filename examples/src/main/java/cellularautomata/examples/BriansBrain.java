package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;
import cellularautomata.core.Tools;

/**
 * Brian's Brain: off / on / dying. An off cell turns on when exactly two
 * neighbors are on; on cells die next step; dying cells become off.
 */
public class BriansBrain extends IntegerCellularAutomata2D {

    public static final int OFF = 0;
    public static final int ON = 1;
    public static final int DYING = 2;

    public BriansBrain(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        int[] values = {0, 0, 0, 0, 0, 1}; // approximately 1/6 cells on
        Tools.initializeRandom(cells, values);
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        if (values[0] == ON) {
            return DYING;
        }
        if (values[0] == DYING) {
            return OFF;
        }
        int on = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] == ON) {
                on++;
            }
        }
        return on == 2 ? ON : OFF;
    }
}

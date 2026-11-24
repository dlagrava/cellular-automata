package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import cellularautomata.core.IntegerCellularAutomata2D;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Per-step live-cell density. CSV: {@code step,live,total,density}.
 *
 * @author Daniel Lagrava
 */
public final class DensityCollector implements IStatsCollector {

    private static final int DEFAULT_LIVE_VALUE = 1;

    private final PrintStream out;
    private final int liveValue;
    private final List<String> rows = new ArrayList<>();

    public DensityCollector(PrintStream out) {
        this(out, DEFAULT_LIVE_VALUE);
    }

    public DensityCollector(PrintStream out, int liveValue) {
        this.out = Objects.requireNonNull(out, "out");
        this.liveValue = liveValue;
    }

    @Override
    public void onStep(int step, ICellularAutomata ca) {
        IntegerCellularAutomata2D grid = Grids.requireInteger(ca);
        int sizeX = grid.getSizeX();
        int sizeY = grid.getSizeY();
        long total = (long) sizeX * sizeY;
        long live = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (grid.getValue(x, y) == liveValue) {
                    live++;
                }
            }
        }
        double density = total == 0 ? 0.0 : (double) live / (double) total;
        rows.add(step + "," + live + "," + total + "," + density);
    }

    @Override
    public void onFinish(ICellularAutomata ca) {
        out.println("step,live,total,density");
        for (String row : rows) {
            out.println(row);
        }
    }
}

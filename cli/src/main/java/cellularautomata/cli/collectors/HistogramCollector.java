package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import cellularautomata.core.IntegerCellularAutomata2D;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Per-step value histogram over the logical grid. CSV: {@code step,value,count}.
 *
 * @author Daniel Lagrava
 */
public final class HistogramCollector implements IStatsCollector {

    private final PrintStream out;
    private final List<String> rows = new ArrayList<>();

    public HistogramCollector(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public void onStep(int step, ICellularAutomata ca) {
        IntegerCellularAutomata2D grid = Grids.requireInteger(ca);
        Map<Integer, Long> counts = new TreeMap<>();
        int sizeX = grid.getSizeX();
        int sizeY = grid.getSizeY();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                counts.merge(grid.getValue(x, y), 1L, Long::sum);
            }
        }
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            rows.add(step + "," + entry.getKey() + "," + entry.getValue());
        }
    }

    @Override
    public void onFinish(ICellularAutomata ca) {
        out.println("step,value,count");
        for (String row : rows) {
            out.println(row);
        }
    }
}

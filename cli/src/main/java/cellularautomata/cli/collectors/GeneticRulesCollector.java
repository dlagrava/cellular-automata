package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import evolutionaryAutomata.GeneticCellularAutomata;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Per-step original-rule counts for a genetic CA. CSV: {@code step,rule1,rule2}.
 * On finish, optionally writes {@link GeneticCellularAutomata#writeRulesStatistics}.
 *
 * @author Daniel Lagrava
 */
public final class GeneticRulesCollector implements IStatsCollector {

    private final PrintStream out;
    private final String histogramFile;
    private final List<String> rows = new ArrayList<>();

    /**
     * @param out            CSV destination
     * @param histogramFile  path for finish histogram, or {@code null} to skip
     */
    public GeneticRulesCollector(PrintStream out, String histogramFile) {
        this.out = Objects.requireNonNull(out, "out");
        this.histogramFile = histogramFile;
    }

    @Override
    public void onStep(int step, ICellularAutomata ca) {
        GeneticCellularAutomata genetic = requireGenetic(ca);
        int[] counts = genetic.countOriginalRules();
        rows.add(step + "," + counts[0] + "," + counts[1]);
    }

    @Override
    public void onFinish(ICellularAutomata ca) {
        out.println("step,rule1,rule2");
        for (String row : rows) {
            out.println(row);
        }
        if (histogramFile != null) {
            requireGenetic(ca).writeRulesStatistics(histogramFile);
        }
    }

    private static GeneticCellularAutomata requireGenetic(ICellularAutomata ca) {
        if (ca instanceof GeneticCellularAutomata genetic) {
            return genetic;
        }
        throw new IllegalArgumentException(
                "genetic-rules requires GeneticCellularAutomata, got " + ca.getClass().getName());
    }
}

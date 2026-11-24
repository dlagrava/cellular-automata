package cellularautomata.cli;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;

import java.util.List;
import java.util.Objects;

/**
 * Headless N-step loop: collision-and-propagation, then fan out to collectors.
 *
 * @author Daniel Lagrava
 */
public final class BatchRunner {

    /**
     * Run {@code steps} updates on {@code ca}, calling each collector after every step
     * and {@link IStatsCollector#onFinish} once at the end.
     *
     * @param ca         automata to advance
     * @param steps      number of collision-and-propagation steps (non-negative)
     * @param collectors observers; may be empty, not null
     */
    public void run(ICellularAutomata ca, int steps, List<IStatsCollector> collectors) {
        Objects.requireNonNull(ca, "ca");
        Objects.requireNonNull(collectors, "collectors");
        if (steps < 0) {
            throw new IllegalArgumentException("steps must be non-negative: " + steps);
        }

        for (int step = 0; step < steps; step++) {
            ca.collisionAndPropagation();
            for (IStatsCollector collector : collectors) {
                collector.onStep(step, ca);
            }
        }
        for (IStatsCollector collector : collectors) {
            collector.onFinish(ca);
        }
    }

}

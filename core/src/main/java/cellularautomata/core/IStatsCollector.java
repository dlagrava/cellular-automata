package cellularautomata.core;

/**
 * Hook for observing a cellular automata after each step (and optionally at finish).
 *
 * @author Daniel Lagrava
 */
public interface IStatsCollector {

    /**
     * Called after each collision-and-propagation step.
     *
     * @param step zero-based step index just completed
     * @param ca   the automata being run
     */
    void onStep(int step, ICellularAutomata ca);

    /**
     * Called once after the last step. Default: no-op.
     *
     * @param ca the automata that was run
     */
    default void onFinish(ICellularAutomata ca) {}

}

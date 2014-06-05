/*
 * No licence
 */

package cellularautomata.display;

/**
 * Interface for objects that export the information on a cellular automata state.
 * @author Daniel Lagrava
 */
public interface IExporter {

    /**
     * Receive a matrix of integer values and display them in some way.
     * @param values contains the needed information
     */
    void writeIntegerValues(int[][] values);

}

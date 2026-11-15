package cellularautomata.core;

/**
 * @author Daniel Lagrava
 */
public interface IBoundary {


    /**
     * @return the width of the border
     */
    public int getWidth();


    /**
     * Computation of the values inside the table that represents the cellular automata.
     *
     * @param ca
     */
    public abstract void computeBorders(int[][] ca);

    /**
     * Computation of the values inside the table that represents the cellular automata.
     *
     * @param ca
     */
    public abstract void computeBorders(double[][] ca);

    /**
     * @param value
     */
    public abstract void setValue(int value);

    /**
     * @param value
     */
    public abstract void setValue(double value);

}

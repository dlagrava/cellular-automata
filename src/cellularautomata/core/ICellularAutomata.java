/*
 *
 */

package cellularautomata.core;

import cellularautomata.display.IExporter;

/**
 * @author Daniel Lagrava
 */
public interface ICellularAutomata {

    /**
     *
     */
    public void copyNeighborValues();

    /**
     *
     */
    public void collision();

    /**
     *
     */
    public void propagation();

    /**
     *
     */
    public void postProcessing();

    /**
     *
     */
    public void collisionAndPropagation();

    /**
     *
     */
    int getSizeX();

    /**
     *
     */
    int getSizeY();

    /**
     * @param exporter The exporter to use to show the CA values
     */
    void exportValues(IExporter exporter);
}

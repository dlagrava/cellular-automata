/*
 * 
 */

package cellularautomata.core;

import cellularautomata.display.*;

/**
 * 
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
      * 
      * @param writer
      */
     void exportValues(IExporter exporter);
}

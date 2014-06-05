/*
 * No licence
 */

package cellularautomata.examples;

import cellularautomata.display.*;
import cellularautomata.core.*;

/**
 *
 * @author Daniel Lagrava
 */
public class West extends IntegerCellularAutomata2D {
   /**
    * Constructor of the West CA
    * @param sizeX
    * @param sizeY
    * @param setup
    */
   public West(int sizeX, int sizeY,Setup setup){
        super(sizeX, sizeY, setup);
        int[] center = {sizeX/2,sizeY/2}; 
        Tools.initializeCircle(cells, 1, center, sizeX/3);
    }

    /**
     * Rule for west automata, if the left neighbor is 1 then the cell becomes 1
     * @param x
     * @param y
     * @param values
     * @return
     */
    @Override
    public int applyRule(int x, int y, int[] values) {
        return (values[2] == 1) ? 1 : 0;
            
    }
    
    public static void main(String[] args){
    int sizeX = 127;
    int sizeY = 127;
    int iterations = 400;
    
       
    Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
    ICellularAutomata ca = new West(sizeX, sizeY, setup);
    
    ColorMap colorMap = new GrayLevelColorMap(0, 1);
    ImageWriter writer = new ImageWriter(sizeX, sizeY,colorMap);    
    
    
    
    for (int iT = 0; iT < iterations; iT++ ){
        writer.setFileName("./tmp/output"+ImageWriter.padding(iT, 6)+".gif");
        ca.exportValues(writer);
        // evolution steps
        ca.copyNeighborValues();
        ca.collision();
        ca.postProcessing();
     
    }
    
}
  

}

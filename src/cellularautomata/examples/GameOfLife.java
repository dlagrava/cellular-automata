/*
 * No licence
 */

package cellularautomata.examples;

import cellularautomata.display.*;
import cellularautomata.core.*;
import javax.swing.*;

/**
 * 
 * @author Daniel Lagrava
 */
public class GameOfLife extends IntegerCellularAutomata2D {

    public GameOfLife(int sizeX, int sizeY, Setup setup, int randomCells){
        super(sizeX,sizeY,setup);
        
        int[] values = {0,0,0,0,0,1}; // approximately 1/6 cells to 1
        Tools.initializeRandom(cells, values);
        
    }
    
    @Override
    public int applyRule(int x, int y, int[] values) {
        int sum = 0;
        for (int iK = 1; iK < values.length; iK++){
            sum += values[iK];
        }
        if (sum < 2) return 0;
        if (sum == 3 && values[0] == 0) return 1;
        if (sum == 2 || sum == 3) return values[0];
        if (sum > 3) return 0;
        return 0;
    }
    
    public static void main(String[] args){
    int sizeX = 300;
    int sizeY = 300;
    int iterations = 3000;
    
       
    Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.MOORE, sizeX, sizeY);
    ICellularAutomata ca = new GameOfLife(sizeX, sizeY, setup, 30000);

    ColorMap colorMap = new GrayLevelColorMap(0, 1);
    ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, colorMap);
    
    // To show the CA //////////////////
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new JScrollPane(panel));
    frame.setSize(sizeX,sizeY);
    frame.setLocation(50,50);
    frame.setVisible(true);
    ///////////////////////////////////
    
    for (int iT = 0; iT < iterations; iT++ ){
        
        ca.copyNeighborValues();
        ca.collision();
        ca.postProcessing();
        
        ca.exportValues(panel);
        frame.repaint();
    }
    
    System.exit(0);
    
}
  

}

/*
 * No licence
 */

package cellularautomata.examples;

import cellularautomata.core.*;
import cellularautomata.display.*;
import javax.swing.*;


/**
 *
 * @author Daniel Lagrava
 */
public class LookupTableExample {
    
    public static void main(String[] args){
        int sizeX = 125;
        int sizeY = 125;
        int iterations = 10000;
    
    
       
    Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
    ICellularAutomata ca = new LookupTableCellularAutomata(sizeX, sizeY, setup, LookupTableFactory.Type.WEST);
    
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

/*
 * No licence
 */

package cellularautomata.examples;

import cellularautomata.display.*;
import cellularautomata.core.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * Implementation of the time tunnel rule. Each cell has a 2-bit value
 * which saves the t-1 and t values. 
 * @author Daniel Lagrava
 */
public class TimeTunnel extends IntegerCellularAutomata2D {

    public TimeTunnel(int sizeX, int sizeY, Setup setup){
        super(sizeX,sizeY,setup);
        // initialize a square
        int cornerX = sizeX/3;
        int cornerY = sizeY/3;
        int sideLength = Math.min(sizeX/3,sizeY/3);
        Tools.initializeFilledSquare(cells, 1, cornerX, cornerY, sideLength);
        /*
        int[] center = {sizeX/2,sizeY/2};
        int radius = Math.min(sizeX/4,sizeY/4);
        Tools.initializeDisk(cells, 3, center,radius);*/
    }
    
    /**
     * Implementation of the time tunnel rule. We use 2 bits, the bit 1 for the value at time t
     * and the bit 0 for the value at time t-1
     * @param x
     * @param y
     * @param values
     * @return
     */
    @Override
    public int applyRule(int x, int y, int[] values) {
        int sum = 0;
        int ct = (values[0] & 2) >> 1; // current values (time t)
        int ctminus1 = values[0] & 1; // values at time t-1
        
        // we sum the value at t of all neighborhood
        for (int iK = 0; iK < values.length; iK++){
            sum += (values[iK] & 2) >> 1; 
        }

        if (sum == 0 || sum == 5) // if we get 0 or 5
            // we get t-1 value and store t value
            return (ctminus1 << 1) + ct; 
        else
            // otherwise
            return ((1 - ctminus1)<<1) + ct;
    }

     public static void main(String[] args) {
        int sizeX = 400;
        int sizeY = 400;
        int iterations = 10000;

        Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
        IntegerCellularAutomata2D ca = new TimeTunnel(sizeX, sizeY, setup);

        ColorMap colorMap = new GrayLevelColorMap(0, 3);
        ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, colorMap);

        // To show the CA //////////////////
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(panel));
        frame.setSize(sizeX, sizeY);
        frame.setLocation(50, 50);
        frame.setVisible(true);
        ///////////////////////////////////

        for (int iT = 0; iT < iterations; iT++) {
            ca.collisionAndPropagation();
            System.out.println(ca.getValue(ca.getSizeX()/2, ca.getSizeY()/2));
            ca.exportValues(panel);
            frame.repaint();
        }

        System.exit(0);

    }

}

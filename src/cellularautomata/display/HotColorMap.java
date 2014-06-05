/*
 * No licence
 */

package cellularautomata.display;

import javax.swing.*;

/**
 *
 * @author Daniel Lagrava
 */
public class HotColorMap extends ColorMap {
    
    public HotColorMap(int min, int max){
        super(min,max);
    }

    /**
     * Converts a value to a color between black through white passing by red and
     * yellow.
     * @param value
     * @return
     */
    @Override
    public int convertValue(int value) {
        if (maxValue > minValue){
           double length = (double)maxValue - (double)minValue;
           double n = 3.0/8.0*length;
           int r = ((double)value <= n)? (int) (((double)(value)/n)*255.0) : 255;
           int g = 0;
           if (value < 2*n && value >= n) g = (int) (((double)(value - n)/(n)*255.0));
           if (value >= 2*n) g = 255;
           int b = ((double)value < 2*n) ? 0 : (int) (((double) (value - 2*n)/(length-2*n))*255.0);
           return b + (g<<8) + (r<<16);
        }
        else {
           return 0;
        }
    }

    /**
     * To test the output of the color map.
     * @param args
     */
    public static void main(String[] args){
        int sizeX = 256;
        int sizeY = 256;        
        
        ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, new HotColorMap(0, sizeY-1));
        
        
        // To show the CA //////////////////
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(panel));
        frame.setSize(sizeX,sizeY);
        frame.setLocation(50,50);
        frame.setVisible(true);
        ///////////////////////////////////
        
        int[][] testMatrix = new int[sizeX][sizeY];
        
        for (int iX = 0; iX < sizeX; iX++){
            for (int iY = 0; iY < sizeY; iY++){
                testMatrix[iX][iY] = iX;
            }
        }
        
        panel.writeIntegerValues(testMatrix);
        frame.repaint();
    }

}

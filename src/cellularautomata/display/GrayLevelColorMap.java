/*
 * No licence
 */

package cellularautomata.display;

import javax.swing.*;

/**
 *
 * @author Daniel Lagrava
 */
public class GrayLevelColorMap extends ColorMap {
    final private int levels = 255;
    
    public GrayLevelColorMap(int minValue, int maxValue){
        super(minValue,maxValue);
    }

    
    @Override
    public int convertValue(int value) {
        if (maxValue != minValue){
            int greyValue = levels/(maxValue-minValue)*value;
            return greyValue + (greyValue<<8) + (greyValue<<16);
        }
        System.err.println("Merde");
        return 0;
    }
    
    public static void main(String[] args){
        int sizeX = 256;
        int sizeY = 256;        
        
        ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, new GrayLevelColorMap(0, sizeY-1));
        
        
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

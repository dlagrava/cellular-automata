/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata.display;

import javax.swing.*;

/**
 *
 * @author lagravas
 */
public class JetColorMap extends ColorMap {

    public JetColorMap(int min, int max){
        super(min,max);
    }

    @Override
    public int convertValue(int value) {
        if (maxValue > minValue){
           double length = (double)maxValue - (double)minValue;
           double interval1 = 1.0/8.0;
           double interval2 = 5.0/16.0;
           double interval3 = 11.0/16.0;
           double interval4 = 14.0/16.0;
           double normalizedValue = (double) value/length;
           int r=0,g=0,b=0;
           if (normalizedValue < interval1){
               b = (int) (0xff*(normalizedValue+interval1)/interval1/2);
           }
           else if (normalizedValue < interval2){
               b = 0xff;
               g = (int)((normalizedValue-interval1)*255.0/(interval2-interval1));
           }
           else if (normalizedValue < interval3) {
               b = (int)((-normalizedValue+interval3)*255.0/(interval3-interval2));
               r = (int)((normalizedValue-interval2)*255.0/(interval3-interval2));
               g = 0xff;
           }
           else if (normalizedValue < interval4){
               g = (int)((interval4-normalizedValue)*255.0/(interval4-interval3));
               r = 0xff;
           }
           else {
               r = (int)(0xff*(1.0-normalizedValue+interval1)/interval1/2);
           }

           return b + (g<<8) + (r<<16);
        }
        else {
           return 0xff;
        }
    }

    /**
     * To test the output of the color map.
     * @param args
     */
    public static void main(String[] args){
        int sizeX = 256;
        int sizeY = 256;

        ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, new JetColorMap(0, sizeY-1));


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

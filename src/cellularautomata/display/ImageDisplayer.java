/*
 * No licence
 */
package cellularautomata.display;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * A Component to display the values of a cellular automata in a GUI.
 * @author Daniel Lagrava
 */
public class ImageDisplayer extends JPanel implements IExporter {

    private int width;
    private int height;
    private BufferedImage image;
    private ColorMap colorMap;
    

    public ImageDisplayer(int width, int height, ColorMap color) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        colorMap = color;
    }

    public void writeIntegerValues(int[][] values) {
     
        // writing the data in the image
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, colorMap.convertValue(values[i][j]));
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    private int getMin(int[][] values) {
        int min = Integer.MAX_VALUE;
        for (int iX = 0; iX < values.length; iX++) {
            for (int iY = 0; iY < values[iX].length; iY++) {
                if (values[iX][iY] < min) {
                    min = values[iX][iY];
                }
            }
        }
        return min;
    }

    private int getMax(int[][] values) {
        int max = Integer.MIN_VALUE;
        for (int iX = 0; iX < values.length; iX++) {
            for (int iY = 0; iY < values[iX].length; iY++) {
                if (values[iX][iY] > max) {
                    max = values[iX][iY];
                }
            }
        }
        return max;
    }
}

/*
 * No licence
 */
package cellularautomata.display;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * This class takes a ColorMap object and writes an image that represents the state
 * of the cellular automata in that moment. 
 * @author Daniel Lagrava
 */
public class ImageWriter implements IExporter {

    private int width;
    private int height;
    private BufferedImage image;
    private ColorMap colorMap;
    private String fileName;
    private boolean isDynamic = false; // recompute the color map if needed

    public ImageWriter(int width, int height, ColorMap colorMap) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.colorMap = colorMap;
    }

    public void writeIntegerValues(int[][] values) {
        try {
            String outputFile = fileName;
            FileOutputStream stream = new FileOutputStream(outputFile);
            
            // if dynamic colormap, recompute the min and max
            if (isDynamic) {
                // Retrieve the min and max values to update the colorMap
                int min = getMin(values);
                int max = getMax(values);
                // set new colorMap min and max
                colorMap.setMinValue(min);
                colorMap.setMaxValue(max);
            }
            // writing the data in the image
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    image.setRGB(i, j, colorMap.convertValue(values[i][j]));
                }
            }
            ImageIO.write(image, "gif", stream);

        } catch (IOException ex) {
            Logger.getLogger(ImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String padding(int x, int length) {
        String number = Integer.toString(x);
        char[] res = new char[length];

        for (int i = 0; i < length; i++) {
            res[i] = '0';
        }

        String zeros = new String(res);
        int neededZeros = length - number.length();

        return zeros.substring(0, neededZeros) + number;

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

package cellularautomata.display;

import cellularautomata.core.Tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class takes a ColorMap object and writes an image that represents the state
 * of the cellular automata at that moment.
 *
 * @author Daniel Lagrava
 */
public class ImageWriter implements IExporter {

    private final int width;
    private final int height;
    private final BufferedImage image;
    private final ColorMap colorMap;
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
                int min = Tools.getMin(values);
                int max = Tools.getMax(values);
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

        Arrays.fill(res, '0');

        String zeros = new String(res);
        int neededZeros = length - number.length();

        return zeros.substring(0, neededZeros) + number;

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

}

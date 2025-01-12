package cellularautomata.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Component to display the values of a cellular automata in a GUI.
 *
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
}

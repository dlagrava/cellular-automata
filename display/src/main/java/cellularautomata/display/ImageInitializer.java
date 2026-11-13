package cellularautomata.display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Helpers that load cellular automata grids from images.
 */
public final class ImageInitializer {

    private ImageInitializer() {
    }

    /**
     * @param fileName path to an image readable by ImageIO
     * @param table    destination grid; filled with RGB values from the image
     */
    public static void initializeFromBinaryImage(String fileName, int[][] table) throws IOException {
        BufferedImage image = ImageIO.read(new File(fileName));
        for (int iX = 0; iX < table.length; ++iX) {
            for (int iY = 0; iY < table[iX].length; ++iY) {
                table[iX][iY] = image.getRGB(iX, iY);
            }
        }
    }
}

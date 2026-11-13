package cellularautomata.demo;

import cellularautomata.display.ColorMap;
import cellularautomata.core.IExporter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * JavaFX {@link IExporter} that paints CA cell values into a {@link WritableImage}.
 */
public class CanvasExporter implements IExporter {

    private final int width;
    private final int height;
    private final WritableImage image;
    private final ColorMap colorMap;

    public CanvasExporter(int width, int height, ColorMap colorMap) {
        this.width = width;
        this.height = height;
        this.colorMap = colorMap;
        this.image = new WritableImage(width, height);
    }

    @Override
    public void writeIntegerValues(int[][] values) {
        PixelWriter pixels = image.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = colorMap.convertValue(values[x][y]);
                pixels.setArgb(x, y, 0xFF000000 | rgb);
            }
        }
    }

    public WritableImage getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

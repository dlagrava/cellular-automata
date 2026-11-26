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
    private final int pixelScale;
    private final WritableImage image;
    private final ColorMap colorMap;

    public CanvasExporter(int width, int height, ColorMap colorMap) {
        this(width, height, colorMap, 1);
    }

    public CanvasExporter(int width, int height, ColorMap colorMap, int pixelScale) {
        if (pixelScale < 1) {
            throw new IllegalArgumentException("pixelScale must be >= 1");
        }
        this.width = width;
        this.height = height;
        this.pixelScale = pixelScale;
        this.colorMap = colorMap;
        this.image = new WritableImage(width * pixelScale, height * pixelScale);
    }

    @Override
    public void writeIntegerValues(int[][] values) {
        PixelWriter pixels = image.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = 0xFF000000 | colorMap.convertValue(values[x][y]);
                fillBlock(pixels, x, y, argb);
            }
        }
    }

    @Override
    public void writeDoubleValues(double[][] values) {
        PixelWriter pixels = image.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = 0xFF000000 | colorMap.convertValue(values[x][y]);
                fillBlock(pixels, x, y, argb);
            }
        }
    }

    private void fillBlock(PixelWriter pixels, int cellX, int cellY, int argb) {
        int x0 = cellX * pixelScale;
        int y0 = cellY * pixelScale;
        for (int dx = 0; dx < pixelScale; dx++) {
            for (int dy = 0; dy < pixelScale; dy++) {
                pixels.setArgb(x0 + dx, y0 + dy, argb);
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

    public int getPixelScale() {
        return pixelScale;
    }
}

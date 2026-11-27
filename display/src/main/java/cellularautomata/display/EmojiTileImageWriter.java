package cellularautomata.display;

import cellularautomata.core.IExporter;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes a grid of Twemoji face tiles (healthy → ill) as a GIF.
 * States {@code 0..maxState} map onto {@link #FACE_COUNT} faces.
 */
public class EmojiTileImageWriter implements IExporter {

    public static final int FACE_COUNT = 7;

    private static final String[] FACE_CODES = {
            "1f60a", "1f642", "1f610", "1f615", "1f912", "1f922", "1f92e"
    };

    private static final Color BACKGROUND = new Color(0x1a1a1a);

    private final int width;
    private final int height;
    private final int cellPx;
    private final int maxState;
    private final int imgPx;
    private final BufferedImage[] faces;
    private final BufferedImage image;
    private Path file;

    public EmojiTileImageWriter(int width, int height, int cellPx, int maxState) {
        if (cellPx < 1) {
            throw new IllegalArgumentException("cellPx must be >= 1");
        }
        if (maxState < 1) {
            throw new IllegalArgumentException("maxState must be >= 1");
        }
        this.width = width;
        this.height = height;
        this.cellPx = cellPx;
        this.maxState = maxState;
        this.imgPx = Math.max(8, (int) Math.round(cellPx * 0.85));
        this.faces = loadFaces();
        this.image = new BufferedImage(width * cellPx, height * cellPx, BufferedImage.TYPE_INT_RGB);
    }

    private static BufferedImage[] loadFaces() {
        BufferedImage[] tiles = new BufferedImage[FACE_CODES.length];
        for (int i = 0; i < FACE_CODES.length; i++) {
            String path = "/cellularautomata/display/emoji/" + FACE_CODES[i] + ".png";
            URL url = EmojiTileImageWriter.class.getResource(path);
            Objects.requireNonNull(url, "missing emoji resource: " + path);
            try {
                tiles[i] = ImageIO.read(url);
            } catch (IOException e) {
                throw new IllegalStateException("failed to load " + path, e);
            }
            Objects.requireNonNull(tiles[i], "unreadable emoji resource: " + path);
        }
        return tiles;
    }

    public static int faceIndex(int value, int maxState) {
        int clamped = Math.max(0, Math.min(value, maxState));
        return (int) Math.round((double) clamped / maxState * (FACE_COUNT - 1));
    }

    public void setFile(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public void writeIntegerValues(int[][] values) {
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(BACKGROUND);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int pad = (cellPx - imgPx) / 2;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int face = faceIndex(values[x][y], maxState);
                    int dx = x * cellPx + pad;
                    int dy = y * cellPx + pad;
                    g.drawImage(faces[face], dx, dy, imgPx, imgPx, null);
                }
            }
        } finally {
            g.dispose();
        }
        try (OutputStream stream = Files.newOutputStream(file)) {
            ImageIO.write(image, "gif", stream);
        } catch (IOException ex) {
            Logger.getLogger(EmojiTileImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void writeDoubleValues(double[][] values) {
        throw new UnsupportedOperationException("EmojiTileImageWriter is integer-only");
    }
}

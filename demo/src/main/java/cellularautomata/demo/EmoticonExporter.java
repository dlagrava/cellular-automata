package cellularautomata.demo;

import cellularautomata.core.IExporter;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.Objects;

/**
 * Renders integer CA cells as emoji faces via JavaFX {@link WebView}.
 * Uses Twemoji SVG images — JavaFX WebKit on Linux does not paint Noto Color Emoji glyphs.
 */
public class EmoticonExporter implements IExporter {

    /** Ladder labels / alt text; index matches Twemoji asset. */
    static final String[] FACES = {
            "😊", "🙂", "😐", "😕", "🤒", "🤢", "🤮"
    };

    private static final String[] FACE_CODES = {
            "1f60a", "1f642", "1f610", "1f615", "1f912", "1f922", "1f92e"
    };

    private static final String[] FACE_URLS = resolveFaceUrls();

    private final int width;
    private final int height;
    private final int cellPx;
    private final int maxState;
    private final WebView webView;
    private final int imgPx;

    public EmoticonExporter(int width, int height, int cellPx, int maxState) {
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
        double pxW = (double) width * cellPx;
        double pxH = (double) height * cellPx;
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(false);
        this.webView.setPageFill(Color.web("#1a1a1a"));
        this.webView.setPrefSize(pxW, pxH);
        this.webView.setMinSize(pxW, pxH);
        this.webView.setMaxSize(pxW, pxH);
    }

    private static String[] resolveFaceUrls() {
        String[] urls = new String[FACE_CODES.length];
        for (int i = 0; i < FACE_CODES.length; i++) {
            String path = "/cellularautomata/demo/emoji/" + FACE_CODES[i] + ".svg";
            URL url = EmoticonExporter.class.getResource(path);
            Objects.requireNonNull(url, "missing emoji resource: " + path);
            urls[i] = url.toExternalForm();
        }
        return urls;
    }

    @Override
    public void writeIntegerValues(int[][] values) {
        webView.getEngine().loadContent(buildHtml(values));
    }

    @Override
    public void writeDoubleValues(double[][] values) {
        throw new UnsupportedOperationException("EmoticonExporter is integer-only");
    }

    int faceIndex(int value) {
        int clamped = Math.max(0, Math.min(value, maxState));
        return (int) Math.round((double) clamped / maxState * (FACES.length - 1));
    }

    String faceFor(int value) {
        return FACES[faceIndex(value)];
    }

    private String buildHtml(int[][] values) {
        StringBuilder sb = new StringBuilder(512 + width * height * 96);
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><style>")
                .append("html,body{margin:0;padding:0;background:#1a1a1a;overflow:hidden;}")
                .append("::-webkit-scrollbar{display:none;}")
                .append(".g{display:grid;grid-template-columns:repeat(").append(width)
                .append(',').append(cellPx).append("px);grid-template-rows:repeat(")
                .append(height).append(',').append(cellPx).append("px);width:")
                .append(width * cellPx).append("px;height:").append(height * cellPx)
                .append("px;}")
                .append(".c{display:flex;align-items:center;justify-content:center;}")
                .append(".c img{width:").append(imgPx).append("px;height:").append(imgPx)
                .append("px;display:block;}")
                .append("</style></head><body><div class=\"g\">");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int face = faceIndex(values[x][y]);
                sb.append("<div class=\"c\"><img src=\"").append(FACE_URLS[face])
                        .append("\" alt=\"").append(FACES[face]).append("\"></div>");
            }
        }
        sb.append("</div></body></html>");
        return sb.toString();
    }

    public WebView getWebView() {
        return webView;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellPx() {
        return cellPx;
    }
}

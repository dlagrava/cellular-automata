package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import cellularautomata.display.ColorMap;
import cellularautomata.display.ImageWriter;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes one GIF frame per step via {@link ImageWriter}.
 *
 * @author Daniel Lagrava
 */
public final class ImageFramesCollector implements IStatsCollector {

    private final Path directory;
    private final ColorMap colorMap;
    private ImageWriter writer;

    public ImageFramesCollector(Path directory, ColorMap colorMap) {
        this.directory = Objects.requireNonNull(directory, "directory");
        this.colorMap = Objects.requireNonNull(colorMap, "colorMap");
    }

    @Override
    public void onStep(int step, ICellularAutomata ca) {
        if (writer == null) {
            writer = new ImageWriter(ca.getSizeX(), ca.getSizeY(), colorMap);
        }
        Path file = directory.resolve("frame" + ImageWriter.padding(step, 6) + ".gif");
        writer.setFileName(file.toString());
        ca.exportValues(writer);
    }
}

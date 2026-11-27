package cellularautomata.cli.collectors;

import cellularautomata.core.ICellularAutomata;
import cellularautomata.core.IStatsCollector;
import cellularautomata.display.EmojiTileImageWriter;
import cellularautomata.display.ImageWriter;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Writes emoji-tile GIF frames for emoticon HodgePodge (one Twemoji face per cell).
 */
public final class EmoticonFramesCollector implements IStatsCollector {

    public static final int DEFAULT_CELL_PX = 32;

    private final Path directory;
    private final int cellPx;
    private final int maxState;
    private EmojiTileImageWriter writer;

    public EmoticonFramesCollector(Path directory, int maxState) {
        this(directory, DEFAULT_CELL_PX, maxState);
    }

    public EmoticonFramesCollector(Path directory, int cellPx, int maxState) {
        this.directory = Objects.requireNonNull(directory, "directory");
        if (cellPx < 1) {
            throw new IllegalArgumentException("cellPx must be >= 1");
        }
        if (maxState < 1) {
            throw new IllegalArgumentException("maxState must be >= 1");
        }
        this.cellPx = cellPx;
        this.maxState = maxState;
    }

    @Override
    public void onStep(int step, ICellularAutomata ca) {
        if (writer == null) {
            writer = new EmojiTileImageWriter(ca.getSizeX(), ca.getSizeY(), cellPx, maxState);
        }
        Path file = directory.resolve("frame" + ImageWriter.padding(step, 6) + ".gif");
        writer.setFile(file);
        ca.exportValues(writer);
    }
}

package cellularautomata.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain-text integer grid I/O ({@code # ca-grid 1}).
 *
 * <pre>
 * # ca-grid 1
 * W H
 * v00 v01 ... v0(H-1)
 * ...
 * v(W-1)0 ...
 * </pre>
 */
public final class IntegerGridIO {

    private static final String MAGIC = "# ca-grid 1";

    private IntegerGridIO() {
    }

    /**
     * Write a logical {@code W×H} integer grid to {@code path}.
     */
    public static void write(Path path, int[][] values) throws IOException {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("values has zero rows");
        }
        int sizeX = values.length;
        int sizeY = values[0].length;
        if (sizeY == 0) {
            throw new IllegalArgumentException("values has zero columns");
        }
        for (int x = 0; x < sizeX; x++) {
            if (values[x] == null || values[x].length != sizeY) {
                throw new IllegalArgumentException("ragged or null row at index " + x);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(MAGIC);
            writer.newLine();
            writer.write(sizeX + " " + sizeY);
            writer.newLine();
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    if (y > 0) {
                        writer.write(' ');
                    }
                    writer.write(Integer.toString(values[x][y]));
                }
                writer.newLine();
            }
        }
    }

    /**
     * Read a {@code # ca-grid 1} file into a new {@code W×H} matrix.
     */
    public static int[][] read(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = nextDataLine(reader);
            if (line == null || !MAGIC.equals(line)) {
                throw new IllegalArgumentException(
                        "expected header \"" + MAGIC + "\", got: " + line);
            }

            line = nextDataLine(reader);
            if (line == null) {
                throw new IllegalArgumentException("missing size line");
            }
            String[] sizeTokens = line.trim().split("\\s+");
            if (sizeTokens.length != 2) {
                throw new IllegalArgumentException("size line must be \"W H\", got: " + line);
            }
            int sizeX = parseIntToken(sizeTokens[0], "sizeX");
            int sizeY = parseIntToken(sizeTokens[1], "sizeY");
            if (sizeX <= 0 || sizeY <= 0) {
                throw new IllegalArgumentException(
                        "size must be positive, got " + sizeX + "×" + sizeY);
            }

            int[][] values = new int[sizeX][sizeY];
            for (int x = 0; x < sizeX; x++) {
                line = nextDataLine(reader);
                if (line == null) {
                    throw new IllegalArgumentException(
                            "expected " + sizeX + " data rows, got " + x);
                }
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length != sizeY) {
                    throw new IllegalArgumentException(
                            "row " + x + ": expected " + sizeY + " values, got " + tokens.length);
                }
                for (int y = 0; y < sizeY; y++) {
                    values[x][y] = parseIntToken(tokens[y], "row " + x + " col " + y);
                }
            }

            line = nextDataLine(reader);
            if (line != null) {
                throw new IllegalArgumentException("extra data after grid: " + line);
            }
            return values;
        }
    }

    /** Skip blank lines and non-magic {@code #} comments; return next content line or null. */
    private static String nextDataLine(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith("#") && !MAGIC.equals(trimmed)) {
                continue;
            }
            return trimmed;
        }
        return null;
    }

    private static int parseIntToken(String token, String label) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("non-integer " + label + ": " + token, e);
        }
    }
}

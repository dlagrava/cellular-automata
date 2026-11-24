package cellularautomata.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntegerGridIOTest {

    @TempDir
    Path tempDir;

    private IntegerCellularAutomata2D ca;

    @BeforeEach
    void setUp() {
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                3,
                2);
        ca = new IntegerCellularAutomata2D(3, 2, setup) {
            @Override
            public int applyRule(int x, int y, int[] values) {
                return values[0];
            }
        };
    }

    @Test
    void copyLoadValuesRoundTrip() {
        ca.setValue(0, 0, 1);
        ca.setValue(0, 1, 2);
        ca.setValue(1, 0, 3);
        ca.setValue(1, 1, 4);
        ca.setValue(2, 0, 5);
        ca.setValue(2, 1, 6);

        int[][] snapshot = ca.copyValues();
        assertArrayEquals(new int[]{1, 2}, snapshot[0]);
        assertArrayEquals(new int[]{3, 4}, snapshot[1]);
        assertArrayEquals(new int[]{5, 6}, snapshot[2]);

        IntegerCellularAutomata2D other = newBlankCa(3, 2);
        other.loadValues(snapshot);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                assertEquals(ca.getValue(x, y), other.getValue(x, y));
            }
        }
    }

    @Test
    void fileRoundTrip() throws IOException {
        ca.setValue(0, 0, 7);
        ca.setValue(0, 1, -1);
        ca.setValue(1, 0, 0);
        ca.setValue(1, 1, 42);
        ca.setValue(2, 0, 99);
        ca.setValue(2, 1, 8);

        Path file = tempDir.resolve("grid.ca-grid");
        IntegerGridIO.write(file, ca.copyValues());

        IntegerCellularAutomata2D other = newBlankCa(3, 2);
        other.loadValues(IntegerGridIO.read(file));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                assertEquals(ca.getValue(x, y), other.getValue(x, y));
            }
        }
    }

    @Test
    void loadValuesRejectsWrongDimensions() {
        assertThrows(IllegalArgumentException.class, () -> ca.loadValues(new int[2][2]));
        assertThrows(IllegalArgumentException.class, () -> ca.loadValues(new int[3][3]));
        assertThrows(IllegalArgumentException.class, () -> ca.loadValues(new int[][]{
                {1, 2},
                {3},
                {5, 6}
        }));
        assertThrows(IllegalArgumentException.class, () -> ca.loadValues(null));
    }

    @Test
    void readRejectsBadHeader() throws IOException {
        Path file = tempDir.resolve("bad-header.ca-grid");
        Files.writeString(file, "not a header\n3 2\n1 2\n3 4\n5 6\n", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> IntegerGridIO.read(file));
    }

    @Test
    void readRejectsWrongTokenCount() throws IOException {
        Path file = tempDir.resolve("bad-row.ca-grid");
        Files.writeString(file, "# ca-grid 1\n3 2\n1 2\n3 4 5\n5 6\n", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> IntegerGridIO.read(file));
    }

    @Test
    void readRejectsNonInteger() throws IOException {
        Path file = tempDir.resolve("bad-int.ca-grid");
        Files.writeString(file, "# ca-grid 1\n2 2\n1 2\n3 x\n", StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> IntegerGridIO.read(file));
    }

    @Test
    void readIgnoresNonMagicComments() throws IOException {
        Path file = tempDir.resolve("comments.ca-grid");
        Files.writeString(file, """
                # note
                # ca-grid 1
                # another note
                2 2
                1 2
                3 4
                """, StandardCharsets.UTF_8);
        int[][] values = IntegerGridIO.read(file);
        assertArrayEquals(new int[]{1, 2}, values[0]);
        assertArrayEquals(new int[]{3, 4}, values[1]);
    }

    private static IntegerCellularAutomata2D newBlankCa(int sizeX, int sizeY) {
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                sizeX,
                sizeY);
        return new IntegerCellularAutomata2D(sizeX, sizeY, setup) {
            @Override
            public int applyRule(int x, int y, int[] values) {
                return values[0];
            }
        };
    }
}

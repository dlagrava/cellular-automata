package cellularautomata.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetupTest {

    private static final int SIZE_X = 10;
    private static final int SIZE_Y = 8;

    @ParameterizedTest
    @EnumSource(BoundaryFactory.BoundaryType.class)
    void realSizesAndNeighborCount_vonNeumann(BoundaryFactory.BoundaryType boundary) {
        Setup setup = create(boundary, NeighborhoodFactory.NeighborhoodType.VONNEUMANN);
        assertEquals(1, setup.getBoundaryWidth());
        assertEquals(SIZE_X + 2, setup.getX());
        assertEquals(SIZE_Y + 2, setup.getY());
        assertEquals(5, setup.getNeighborNumber());
    }

    @ParameterizedTest
    @EnumSource(BoundaryFactory.BoundaryType.class)
    void realSizesAndNeighborCount_moore(BoundaryFactory.BoundaryType boundary) {
        Setup setup = create(boundary, NeighborhoodFactory.NeighborhoodType.MOORE);
        assertEquals(1, setup.getBoundaryWidth());
        assertEquals(SIZE_X + 2, setup.getX());
        assertEquals(SIZE_Y + 2, setup.getY());
        assertEquals(9, setup.getNeighborNumber());
    }

    @Test
    void fixedBoundaryRequiresValueParameter() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                new Setup(
                        BoundaryFactory.BoundaryType.FIXED,
                        NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                        SIZE_X,
                        SIZE_Y));
    }

    @Test
    void vonNeumannNeighborCoordsMatchOffsets() {
        Setup setup = create(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN);
        int x = 4;
        int y = 3;
        int[][] n = setup.getNeighborIndices(x, y);
        assertEquals(5, n.length);
        assertNeighbor(n[0], x, y);
        assertNeighbor(n[1], x + 1, y);
        assertNeighbor(n[2], x, y + 1);
        assertNeighbor(n[3], x - 1, y);
        assertNeighbor(n[4], x, y - 1);
    }

    @Test
    void mooreNeighborCoordsMatchOffsets() {
        Setup setup = create(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.MOORE);
        int x = 5;
        int y = 4;
        int[][] n = setup.getNeighborIndices(x, y);
        assertEquals(9, n.length);
        assertNeighbor(n[0], x, y);
        assertNeighbor(n[1], x + 1, y);
        assertNeighbor(n[2], x, y + 1);
        assertNeighbor(n[3], x - 1, y);
        assertNeighbor(n[4], x, y - 1);
        assertNeighbor(n[5], x + 1, y + 1);
        assertNeighbor(n[6], x - 1, y + 1);
        assertNeighbor(n[7], x - 1, y - 1);
        assertNeighbor(n[8], x + 1, y - 1);
    }

    private static void assertNeighbor(int[] pair, int expectedX, int expectedY) {
        assertEquals(expectedX, pair[0]);
        assertEquals(expectedY, pair[1]);
    }

    private static Setup create(
            BoundaryFactory.BoundaryType boundary,
            NeighborhoodFactory.NeighborhoodType neighborhood) {
        if (boundary == BoundaryFactory.BoundaryType.FIXED) {
            return new Setup(boundary, neighborhood, SIZE_X, SIZE_Y, 0);
        }
        return new Setup(boundary, neighborhood, SIZE_X, SIZE_Y);
    }
}

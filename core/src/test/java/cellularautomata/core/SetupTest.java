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

    private static Setup create(
            BoundaryFactory.BoundaryType boundary,
            NeighborhoodFactory.NeighborhoodType neighborhood) {
        if (boundary == BoundaryFactory.BoundaryType.FIXED) {
            return new Setup(boundary, neighborhood, SIZE_X, SIZE_Y, 0);
        }
        return new Setup(boundary, neighborhood, SIZE_X, SIZE_Y);
    }
}

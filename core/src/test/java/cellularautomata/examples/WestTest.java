package cellularautomata.examples;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WestTest {

    @Test
    void singleOneMovesWestEachStep() {
        int size = 8;
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                size,
                size);
        West ca = new West(size, size, setup);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                ca.setValue(x, y, 0);
            }
        }
        int x = 5;
        int y = 3;
        ca.setValue(x, y, 1);

        ca.collisionAndPropagation();

        assertEquals(0, ca.getValue(x, y));
        assertEquals(1, ca.getValue(x - 1, y));
        assertEquals(0, ca.getValue(x, y - 1));
        assertEquals(0, ca.getValue(x, y + 1));

        ca.collisionAndPropagation();

        assertEquals(1, ca.getValue(x - 2, y));
        assertEquals(0, ca.getValue(x - 1, y));
    }
}

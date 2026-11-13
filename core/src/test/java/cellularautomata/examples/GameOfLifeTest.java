package cellularautomata.examples;

import cellularautomata.core.BoundaryFactory;
import cellularautomata.core.NeighborhoodFactory;
import cellularautomata.core.Setup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameOfLifeTest {

    @Test
    void blockStillLifePersistsOneStep() {
        GameOfLife ca = blank(6, 6);
        // 2×2 block
        ca.setValue(2, 2, 1);
        ca.setValue(3, 2, 1);
        ca.setValue(2, 3, 1);
        ca.setValue(3, 3, 1);

        ca.collisionAndPropagation();

        assertEquals(1, ca.getValue(2, 2));
        assertEquals(1, ca.getValue(3, 2));
        assertEquals(1, ca.getValue(2, 3));
        assertEquals(1, ca.getValue(3, 3));
        assertEquals(0, ca.getValue(1, 1));
        assertEquals(0, ca.getValue(4, 4));
    }

    @Test
    void blinkerOscillatesOneStep() {
        GameOfLife ca = blank(5, 5);
        // horizontal blinker
        ca.setValue(1, 2, 1);
        ca.setValue(2, 2, 1);
        ca.setValue(3, 2, 1);

        ca.collisionAndPropagation();

        assertEquals(0, ca.getValue(1, 2));
        assertEquals(0, ca.getValue(3, 2));
        assertEquals(1, ca.getValue(2, 1));
        assertEquals(1, ca.getValue(2, 2));
        assertEquals(1, ca.getValue(2, 3));
    }

    private static GameOfLife blank(int sizeX, int sizeY) {
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.MOORE,
                sizeX,
                sizeY);
        GameOfLife ca = new GameOfLife(sizeX, sizeY, setup, 0);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                ca.setValue(x, y, 0);
            }
        }
        return ca;
    }
}

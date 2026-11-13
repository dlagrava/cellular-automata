package cellularautomata.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicalCellAccessTest {

    private IntegerCellularAutomata2D ca;

    @BeforeEach
    void setUp() {
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                5,
                4);
        ca = new IntegerCellularAutomata2D(5, 4, setup) {
            @Override
            public int applyRule(int x, int y, int[] values) {
                return values[0];
            }
        };
    }

    @Test
    void getSetValueRoundTrip() {
        ca.setValue(0, 0, 7);
        ca.setValue(4, 3, 9);
        assertEquals(7, ca.getValue(0, 0));
        assertEquals(9, ca.getValue(4, 3));
        assertEquals(0, ca.getValue(2, 1));
    }

    @Test
    void getValueOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> ca.getValue(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ca.getValue(5, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ca.getValue(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> ca.getValue(0, 4));
    }

    @Test
    void setValueOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> ca.setValue(-1, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> ca.setValue(5, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> ca.setValue(0, 4, 1));
    }
}

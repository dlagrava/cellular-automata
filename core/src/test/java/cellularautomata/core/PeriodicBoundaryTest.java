package cellularautomata.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeriodicBoundaryTest {

    @Test
    void computeBordersCopiesInteriorEdgesIntoHalo() {
        // 4×4 storage: 1-cell halo around 2×2 interior
        int[][] ca = new int[4][4];
        ca[1][1] = 11;
        ca[2][1] = 21;
        ca[1][2] = 12;
        ca[2][2] = 22;

        new PeriodicBoundary(1).computeBorders(ca);

        // north / south
        assertEquals(12, ca[1][0]);
        assertEquals(22, ca[2][0]);
        assertEquals(11, ca[1][3]);
        assertEquals(21, ca[2][3]);
        // west / east
        assertEquals(21, ca[0][1]);
        assertEquals(22, ca[0][2]);
        assertEquals(11, ca[3][1]);
        assertEquals(12, ca[3][2]);
    }
}

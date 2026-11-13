package cellularautomata.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LookupTableTest {

    @Test
    void parityLutIndexMatchesBitSumModTwo() {
        LookupTable lut = LookupTableFactory.createLookupTable(LookupTableFactory.Type.PARITY, 5);
        // index bits: self + 4 neighbors; parity = popcount % 2
        assertEquals(0, lut.getValue(0b00000));
        assertEquals(1, lut.getValue(0b00001)); // self only
        assertEquals(0, lut.getValue(0b00011)); // self + east
        assertEquals(1, lut.getValue(0b11111));
    }

    @Test
    void westLutReturnsEastNeighborBit() {
        LookupTable lut = LookupTableFactory.createLookupTable(LookupTableFactory.Type.WEST, 5);
        // east is bit 1 (j >> 1) & 1
        assertEquals(0, lut.getValue(0b00001)); // self only
        assertEquals(1, lut.getValue(0b00010)); // east only
        assertEquals(1, lut.getValue(0b00011)); // self + east
    }

    @Test
    void lookupTableCellularAutomataUsesParityIndex() {
        Setup setup = new Setup(
                BoundaryFactory.BoundaryType.PERIODIC,
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN,
                3,
                3);
        LookupTable lut = LookupTableFactory.createLookupTable(LookupTableFactory.Type.PARITY, 5);
        LookupTableCellularAutomata ca = new LookupTableCellularAutomata(3, 3, setup, lut);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ca.setValue(x, y, 0);
            }
        }
        ca.setValue(1, 1, 1);

        ca.collisionAndPropagation();

        // lone 1: each of its four VN neighbors sees one neighbor bit → odd → 1;
        // center sees only itself → odd → 1; corners see nothing → 0
        assertEquals(1, ca.getValue(1, 1));
        assertEquals(1, ca.getValue(2, 1));
        assertEquals(1, ca.getValue(0, 1));
        assertEquals(1, ca.getValue(1, 2));
        assertEquals(1, ca.getValue(1, 0));
        assertEquals(0, ca.getValue(0, 0));
    }
}

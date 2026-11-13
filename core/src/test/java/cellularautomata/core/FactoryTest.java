package cellularautomata.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactoryTest {

    @Test
    void neighborhoodFactoryProducesKnownTypes() {
        assertEquals(5, NeighborhoodFactory.generate(
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN).getTotalNeighborNumber());
        assertEquals(9, NeighborhoodFactory.generate(
                NeighborhoodFactory.NeighborhoodType.MOORE).getTotalNeighborNumber());
        assertEquals(5, NeighborhoodFactory.generate(
                NeighborhoodFactory.NeighborhoodType.MARGOLUS, 0, 0).getTotalNeighborNumber());
    }

    @Test
    void margolusRequiresCoordinates() {
        assertThrows(IllegalArgumentException.class, () ->
                NeighborhoodFactory.generate(NeighborhoodFactory.NeighborhoodType.MARGOLUS));
    }

    @Test
    void lookupTableFactoryProducesKnownTypes() {
        assertNotNull(LookupTableFactory.createLookupTable(LookupTableFactory.Type.PARITY, 5));
        assertNotNull(LookupTableFactory.createLookupTable(LookupTableFactory.Type.WEST, 5));
        assertNotNull(LookupTableFactory.createLookupTable(LookupTableFactory.Type.NULL, 5));
    }

    @Test
    void boundaryFactoryProducesKnownTypes() {
        INeighborhood n = NeighborhoodFactory.generate(
                NeighborhoodFactory.NeighborhoodType.VONNEUMANN);
        assertEquals(1, BoundaryFactory.generatePeriodicBoundary(n).getWidth());
        assertEquals(1, BoundaryFactory.generateReflexiveBoundary(n).getWidth());
        assertEquals(1, BoundaryFactory.generateFixedBoundary(n, 0).getWidth());
    }

    @Test
    void neighborhoodFactoryRejectsNullType() {
        assertThrows(NullPointerException.class, () ->
                NeighborhoodFactory.generate(null));
    }

    @Test
    void lookupTableFactoryRejectsNullType() {
        assertThrows(NullPointerException.class, () ->
                LookupTableFactory.createLookupTable(null, 5));
    }
}

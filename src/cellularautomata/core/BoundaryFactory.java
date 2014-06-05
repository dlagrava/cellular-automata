/*
 * No licence
 */

package cellularautomata.core;

/**
 * Factory that provides all the boundary conditions implemented in the
 * current version of the code
 * @author Daniel Lagrava
 */
public class BoundaryFactory {
    // types of boundary condition
    public enum BoundaryType {PERIODIC, FIXED, REFLEXIVE, ADIABATIC};

    /**
     * Create an adiabatic boundary condition
     * @return
     */
    public static IBoundary generateAdiabaticBoundary(){
        return null;
    }

    /**
     * Create a reflexive boundary condition for a given neighborhood
     * @param neighborhood
     * @return
     */
    public static IBoundary generateReflexiveBoundary(INeighborhood neighborhood){
        return new ReflexiveBoundary(neighborhood);
    }

    /**
     * Generate a periodic boundary condition for a given neighborhood
     * @param neighborhood
     * @return
     */
    public static IBoundary generatePeriodicBoundary(INeighborhood neighborhood){
        return new PeriodicBoundary(neighborhood);
    }

    /**
     * Generate a boundary condition that fixes a constant value
     * @param neighborhood
     * @param value
     * @return
     */
    public static IBoundary generateFixedBoundary(INeighborhood neighborhood, int value){
        return new FixedBoundary(neighborhood, value);
    }
}

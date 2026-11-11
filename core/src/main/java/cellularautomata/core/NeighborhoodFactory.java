package cellularautomata.core;

/**
 * @author Daniel Lagrava
 */
public class NeighborhoodFactory {

    public enum NeighborhoodType {VONNEUMANN, MOORE, MARGOLUS}

    public static INeighborhood generate(NeighborhoodType type, int x, int y) {
        switch (type) {
            case VONNEUMANN:
                return new VonNeumannNeighborhood(x, y);
            case MOORE:
                return new MooreNeighborhood(x, y);
            case MARGOLUS:
                return new MargolusNeighborhood(x, y);
            default:
                throw new IllegalArgumentException("Unknown neighborhood type: " + type);
        }
    }

}

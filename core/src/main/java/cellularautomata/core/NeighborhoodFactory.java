package cellularautomata.core;

/**
 * @author Daniel Lagrava
 */
public class NeighborhoodFactory {

    public enum NeighborhoodType {VONNEUMANN, MOORE, MARGOLUS}

    /**
     * Shared offset-based neighborhood (Moore / Von Neumann). Margolus still needs
     * per-cell instances — use {@link #generate(NeighborhoodType, int, int)}.
     */
    public static INeighborhood generate(NeighborhoodType type) {
        switch (type) {
            case VONNEUMANN:
                return new VonNeumannNeighborhood();
            case MOORE:
                return new MooreNeighborhood();
            case MARGOLUS:
                throw new IllegalArgumentException(
                        "Margolus requires cell coordinates; use generate(type, x, y)");
            default:
                throw new IllegalArgumentException("Unknown neighborhood type: " + type);
        }
    }

    /**
     * For Margolus (per-cell). Moore / Von Neumann ignore {@code x}/{@code y} and
     * return a shared-offset instance.
     */
    public static INeighborhood generate(NeighborhoodType type, int x, int y) {
        if (type == NeighborhoodType.MARGOLUS) {
            return new MargolusNeighborhood(x, y);
        }
        return generate(type);
    }
}

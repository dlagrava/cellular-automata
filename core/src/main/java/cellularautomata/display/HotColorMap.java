package cellularautomata.display;

/**
 * @author Daniel Lagrava
 */
public class HotColorMap extends ColorMap {

    public HotColorMap(int min, int max) {
        super(min, max);
    }

    /**
     * Converts a value to a color between black through white passing by red and
     * yellow.
     *
     * @param value
     * @return
     */
    @Override
    public int convertValue(int value) {
        if (maxValue > minValue) {
            double length = (double) maxValue - (double) minValue;
            double n = 3.0 / 8.0 * length;
            int r = ((double) value <= n) ? (int) (((double) (value) / n) * 255.0) : 255;
            int g = 0;
            if (value < 2 * n && value >= n) g = (int) (((double) (value - n) / (n) * 255.0));
            if (value >= 2 * n) g = 255;
            int b = ((double) value < 2 * n) ? 0 : (int) (((double) (value - 2 * n) / (length - 2 * n)) * 255.0);
            return b + (g << 8) + (r << 16);
        } else {
            return 0;
        }
    }

}

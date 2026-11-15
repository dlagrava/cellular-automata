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
    public int convertValue(double value) {
        if (maxValue > minValue) {
            double length = maxValue - minValue;
            double shifted = value - minValue;
            double n = 3.0 / 8.0 * length;
            int r = (shifted <= n) ? (int) ((shifted / n) * 255.0) : 255;
            int g = 0;
            if (shifted < 2 * n && shifted >= n) g = (int) (((shifted - n) / n) * 255.0);
            if (shifted >= 2 * n) g = 255;
            int b = (shifted < 2 * n) ? 0 : (int) (((shifted - 2 * n) / (length - 2 * n)) * 255.0);
            return b + (g << 8) + (r << 16);
        } else {
            return 0;
        }
    }

}

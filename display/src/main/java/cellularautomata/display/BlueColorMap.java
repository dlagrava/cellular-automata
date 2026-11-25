package cellularautomata.display;

/**
 * Linear ramp dark navy → mid blue → cyan/white over {@code [minValue, maxValue]}.
 *
 * @author Daniel Lagrava
 */
public class BlueColorMap extends ColorMap {

    public BlueColorMap(int min, int max) {
        super(min, max);
    }

    @Override
    public int convertValue(double value) {
        if (maxValue <= minValue) {
            return 0x000080;
        }
        double t = (value - minValue) / (maxValue - minValue);
        if (t < 0.0) {
            t = 0.0;
        } else if (t > 1.0) {
            t = 1.0;
        }

        int r;
        int g;
        int b;
        if (t < 0.5) {
            // dark navy (0,0,64) → mid blue (0,100,220)
            double u = t / 0.5;
            r = 0;
            g = (int) (u * 100.0);
            b = (int) (64.0 + u * (220.0 - 64.0));
        } else {
            // mid blue (0,100,220) → cyan/white (220,245,255)
            double u = (t - 0.5) / 0.5;
            r = (int) (u * 220.0);
            g = (int) (100.0 + u * (245.0 - 100.0));
            b = (int) (220.0 + u * (255.0 - 220.0));
        }
        return b + (g << 8) + (r << 16);
    }
}

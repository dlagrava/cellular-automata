package cellularautomata.display;

/**
 * Maps {@code [minValue, maxValue]} across a full HSV hue cycle (S=1, V=1).
 * Endpoints meet: {@code max} maps to the same color as {@code min}.
 *
 * @author Daniel Lagrava
 */
public class HueColorMap extends ColorMap {

    public HueColorMap(int min, int max) {
        super(min, max);
    }

    @Override
    public int convertValue(double value) {
        if (maxValue <= minValue) {
            return 0xff0000;
        }
        double t = (value - minValue) / (maxValue - minValue);
        if (t < 0.0) {
            t = 0.0;
        } else if (t > 1.0) {
            t = 1.0;
        }

        double h = t * 6.0;
        if (h >= 6.0) {
            h = 0.0;
        }
        int sector = (int) h;
        double f = h - sector;

        int r;
        int g;
        int b;
        switch (sector) {
            case 0 -> {
                r = 255;
                g = (int) (255.0 * f);
                b = 0;
            }
            case 1 -> {
                r = (int) (255.0 * (1.0 - f));
                g = 255;
                b = 0;
            }
            case 2 -> {
                r = 0;
                g = 255;
                b = (int) (255.0 * f);
            }
            case 3 -> {
                r = 0;
                g = (int) (255.0 * (1.0 - f));
                b = 255;
            }
            case 4 -> {
                r = (int) (255.0 * f);
                g = 0;
                b = 255;
            }
            default -> {
                r = 255;
                g = 0;
                b = (int) (255.0 * (1.0 - f));
            }
        }
        return b + (g << 8) + (r << 16);
    }
}

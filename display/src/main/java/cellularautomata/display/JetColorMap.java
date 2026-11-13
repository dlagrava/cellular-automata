package cellularautomata.display;

/**
 * @author lagravas
 */
public class JetColorMap extends ColorMap {

    public JetColorMap(int min, int max) {
        super(min, max);
    }

    @Override
    public int convertValue(int value) {
        if (maxValue > minValue) {
            double length = (double) maxValue - (double) minValue;
            double interval1 = 1.0 / 8.0;
            double interval2 = 5.0 / 16.0;
            double interval3 = 11.0 / 16.0;
            double interval4 = 14.0 / 16.0;
            double normalizedValue = (double) value / length;
            int r = 0, g = 0, b = 0;
            if (normalizedValue < interval1) {
                b = (int) (0xff * (normalizedValue + interval1) / interval1 / 2);
            } else if (normalizedValue < interval2) {
                b = 0xff;
                g = (int) ((normalizedValue - interval1) * 255.0 / (interval2 - interval1));
            } else if (normalizedValue < interval3) {
                b = (int) ((-normalizedValue + interval3) * 255.0 / (interval3 - interval2));
                r = (int) ((normalizedValue - interval2) * 255.0 / (interval3 - interval2));
                g = 0xff;
            } else if (normalizedValue < interval4) {
                g = (int) ((interval4 - normalizedValue) * 255.0 / (interval4 - interval3));
                r = 0xff;
            } else {
                r = (int) (0xff * (1.0 - normalizedValue + interval1) / interval1 / 2);
            }

            return b + (g << 8) + (r << 16);
        } else {
            return 0xff;
        }
    }

}

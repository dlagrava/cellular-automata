package cellularautomata.display;

/**
 * @author Daniel Lagrava
 */
public class GrayLevelColorMap extends ColorMap {
    private final int levels = 255;

    public GrayLevelColorMap(int minValue, int maxValue) {
        super(minValue, maxValue);
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue is greater than maxValue");
        }
    }


    @Override
    public int convertValue(double value) {
        double span = maxValue - minValue;
        if (span <= 0.0) {
            return 0;
        }
        int greyValue = (int) (levels * (value - minValue) / span);
        return greyValue + (greyValue << 8) + (greyValue << 16);
    }
}

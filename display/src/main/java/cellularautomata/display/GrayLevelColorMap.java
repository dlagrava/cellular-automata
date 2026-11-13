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
    public int convertValue(int value) {
        int greyValue = levels / (maxValue - minValue) * value;
        return greyValue + (greyValue << 8) + (greyValue << 16);
    }
}

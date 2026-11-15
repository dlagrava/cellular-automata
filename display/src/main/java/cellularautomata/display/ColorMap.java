package cellularautomata.display;

/**
 * @author Daniel Lagrava
 */
public abstract class ColorMap {

    protected double minValue;
    protected double maxValue;

    public ColorMap(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int convertValue(int value) {
        return convertValue((double) value);
    }

    public abstract int convertValue(double value);

    public void setMinValue(double value) {
        this.minValue = value;
    }

    public void setMaxValue(double value) {
        this.maxValue = value;
    }

}

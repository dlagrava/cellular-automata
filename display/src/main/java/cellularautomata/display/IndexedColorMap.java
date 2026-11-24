package cellularautomata.display;

/**
 * Maps integer cell states to fixed RGB colors (0xRRGGBB).
 */
public class IndexedColorMap extends ColorMap {

    private final int[] colors;

    public IndexedColorMap(int... colors) {
        super(0, Math.max(0, colors.length - 1));
        if (colors.length == 0) {
            throw new IllegalArgumentException("colors must not be empty");
        }
        this.colors = colors.clone();
    }

    @Override
    public int convertValue(double value) {
        int index = (int) Math.round(value);
        if (index < 0) {
            index = 0;
        } else if (index >= colors.length) {
            index = colors.length - 1;
        }
        return colors[index];
    }
}

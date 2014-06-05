/*
 * No licence
 */

package cellularautomata.display;

/**
 *
 * @author Daniel Lagrava
 */
public abstract class ColorMap {
    
    protected int minValue;
    protected int maxValue;

    public ColorMap(int minValue, int maxValue){
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public abstract int convertValue(int value);
   
    public void setMinValue(int value){
        this.minValue = value;
    }
    
    public void setMaxValue(int value){
        this.maxValue = value;
    }

}

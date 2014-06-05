/*
 * No licence
 */

package cellularautomata.core;

/**
 *
 * @author Daniel Lagrava
 */
public class LookupTableCellularAutomata extends IntegerCellularAutomata2D {
    
    LookupTable lookupTable;
    
    public LookupTableCellularAutomata(int sizeX, int sizeY,Setup setup, LookupTable lut){
        super(sizeX, sizeY, setup);
        lookupTable = lut;
    }
    
    public LookupTableCellularAutomata(int sizeX, int sizeY,Setup setup, LookupTableFactory.Type t){
        super(sizeX, sizeY, setup);
        lookupTable = LookupTableFactory.createLookupTable(t, setup.getNeighborNumber());
        for (int iX = sizeX/2-sizeX/7; iX < sizeX/2+sizeX/7; iX++){
            for (int iY = sizeY/2-sizeY/7; iY < sizeY/2+sizeY/7; iY++){
                setValue(iX, iY, 1);
            }
        }
        
    }
    
    
    
    @Override
    public int applyRule(int x, int y, int[] values) {
        
        int LUTIndex = 0;
        for (int iK = 0; iK < values.length; iK++) {
            int powerOfTwo = (int) Math.pow(2, iK);
            LUTIndex += powerOfTwo * values[iK];
        }
        return lookupTable.getValue(LUTIndex);
    }



}

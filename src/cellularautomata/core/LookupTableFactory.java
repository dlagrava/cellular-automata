/*
 * No licence
 */

package cellularautomata.core;

/**
 *
 * @author Daniel Lagrava
 */
public class LookupTableFactory {
    
    public enum Type {PARITY, WEST, NULL,UNKNOWN};

    /**
     * Create the parity rule for a given neighbor number.
     * @param neighborNumber
     * @return
     */
    private static LookupTable createParityRule(int neighborNumber){
        int length = (int) Math.pow(2, neighborNumber);
        int[] lookupTable = new int[length];
        for (int j = 0; j < length; j++){
            int sum = 0;
            for (int k = 0; k < 5; k++)
                sum += (j >> k) & 1;
            lookupTable[j] = sum % 2;
        }
        return new LookupTable(lookupTable);
    }

    /**
     * create the West rule (the 1's travel left) for a given neighbor number
     * @param neighborNumber
     * @return
     */
    private static LookupTable createWestRule(int neighborNumber){
        int length = (int) Math.pow(2, neighborNumber);
        int[] lookupTable = new int[length];
        for (int j = 0; j < length; j++){
            lookupTable[j] = (j >> 1) & 1; 
        }
        return new LookupTable(lookupTable);
    }
    
    /**
     * Create the trivial rule that sets every value of the neighborhood to 0.
     * @param neighborNumber
     * @return
     */
    public static LookupTable createNullRule(int neighborNumber){
        int length = (int) Math.pow(2, neighborNumber);
        int[] lookupTable = new int[length];
        for (int j = 0; j < length; j++){
            lookupTable[j] = 0;
        }
        return new LookupTable(lookupTable);
    }

    /**
     * 
     * @param t
     * @param neighborNumber
     * @return
     */
    public static LookupTable createLookupTable(Type t, int neighborNumber){
        switch (t){
            case PARITY:
                return createParityRule(neighborNumber);
            case WEST:
                return createWestRule(neighborNumber);
            case NULL:
                return createNullRule(neighborNumber);
            default:
                return null;
        }
    }
    

}

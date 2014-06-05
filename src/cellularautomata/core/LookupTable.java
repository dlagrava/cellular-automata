/*
 * 
 */

package cellularautomata.core;

/**
 * A wrapper class for an integer array that represents a lookup table adding useful
 * methodes.
 * @author Daniel Lagrava
 */
public class LookupTable {
    
    private int[] table;
    private int length;

    
    public LookupTable(int[] table_){
        length = table_.length;
        table = new int[length];
        System.arraycopy(table_, 0, table, 0,length);
    }

    /**
     * Return the value stored in the code position
     * @param code
     * @return
     */
    public int getValue(int code){
        return table[code];
    }

    /**
     * Compute the Hamming distance between this lookup table a lt.
     * @param lt the other lookup table
     * @return an integer between 1 and 2^(tableLength)
     */
    public int hammingDistance(LookupTable lt){
        int distance = 0;
        for (int i = 0; i < length; i++){
            if (table[i] != lt.getValue(i)) distance++;
        }
        return distance;
    }

    /**
     * Returns the length of the lookup table.
     * @return
     */
    public int getLength(){
        return length;
    }

    /**
     * Modify the internal integer lookup table with a new one.
     * @param table
     */
    public void setTable(int[] table) {
        System.arraycopy(table, 0, this.table, 0, length);
    }

    /**
     * Convert the internal table into an integer. If the table is bigger than the
     * size of an integer the result is not correct.
     * @return
     */
    public int toInteger(){
        int result = 0;
        for (int i = 0; i < length; ++i){
            result += table[i]*(1<<i);
        }
        return result;
    }

    /**
     * Copy a lookup table using the constructor. It is safe because the constructor
     * itself creates a real copy of the integer array.
     * @return
     */
    @Override
    public LookupTable clone(){
        LookupTable lut = new LookupTable(table);
        return lut;
    }

    /**
     * Convert the lookup table to a string of 0 and 1. 
     * @return
     */
    @Override
    public String toString(){
        String s = "";
        for (int i:table){
            s += i;
        }
        return s;
    }

    /**
     * Compare two lookup tables by using their string representation
     * @param lut
     * @return
     */
    public boolean equals(LookupTable lut){
        return this.toString().equals(lut.toString());
    }

    /**
     * Modify a single line of the lookup table by inserting value
     * @param index
     * @param value
     */
    public void setNewValue(int index, int value){
        table[index] = value;
    }
}

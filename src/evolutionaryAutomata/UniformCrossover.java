/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evolutionaryAutomata;

import cellularautomata.core.LookupTable;
import java.util.Random;

/**
 *
 * @author lagravas
 */
public class UniformCrossover implements ICrossoverEngine {

    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double ... parameters) {

        double mixingRatio = parameters[0];
        // Pseudo-random number generator
        Random rng = new Random();
        int[] newLookupTable = new int[parent1.getLength()];
        // for each element in the lookuptable
        for (int i = 0; i < parent1.getLength(); i++){
            // we either take the parent2 value
            if (rng.nextDouble() > mixingRatio){
                newLookupTable[i] = parent2.getValue(i);
            }
            // or we keep the one of parent1
            else newLookupTable[i] = parent1.getValue(i);
        }

        return new LookupTable(newLookupTable);
    }

}

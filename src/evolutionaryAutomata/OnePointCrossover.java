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


public class OnePointCrossover implements ICrossoverEngine {



    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double ... parameters) {
        
        int length = parent1.getLength();
        Random rng = new Random();
        int cutPoint = rng.nextInt(length);
        // the two offsprings
        int[] offspringTable1 = new int[length];
        int[] offspringTable2 = new int[length];

        for (int i = 0; i < cutPoint; i++){
            offspringTable1[i] = parent1.getValue(i);
            offspringTable2[i] = parent2.getValue(i);
        }

        for (int i = cutPoint; i < length; i++){
            offspringTable1[i] = parent2.getValue(i);
            offspringTable2[i] = parent1.getValue(i);
        }
        // 50% chance to choose any of the offsprings
        if (rng.nextFloat() > 0.5d)
            return new LookupTable(offspringTable1);
        else
            return new LookupTable(offspringTable2);

    }
    

}

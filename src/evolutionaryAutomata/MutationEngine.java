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
public class MutationEngine {

    // probability to have a mutation
    private double mutationProbability;
    // lookuptable size
    private int tableSize;
    // the random number generator
    private Random rng;
    
    public MutationEngine(double mutationProbability, int tableSize){
        this.mutationProbability = mutationProbability;
        this.tableSize = tableSize;
        rng = new Random();
    }

    /**
     * Ask whether an individual will mutate or not
     * @return an integer representing the individual component to mutate or -1 if no mutation
     * shall take place.
     */
    public int isGoingToMutate(){
        if (rng.nextDouble() > mutationProbability){
            return rng.nextInt(tableSize);
        }
        else {
            return -1;
        }
    }

    /**
     * Each individual will have a certain number of mutations.
     * @param lut
     */
    public void lookUpTableMutation(LookupTable lut){
        
    }

}

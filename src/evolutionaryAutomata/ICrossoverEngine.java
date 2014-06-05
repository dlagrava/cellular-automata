/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

/**
 * Interface for a Crossover engine. The principal method of implementing classes
 * should be 
 *
 * @author lagravas
 */
public interface ICrossoverEngine {


    /**
     * Apply a crossover between parent1 and parent2 to obtain an offspring that
     * is supposed to be better than both of them.
     * Mutation and other operations should be implemented in this method as well.
     * @param parent1
     * @param parent2
     * @return
     */
    public LookupTable applyCrossover(LookupTable parent1, LookupTable parent2, double ... parameters);


}

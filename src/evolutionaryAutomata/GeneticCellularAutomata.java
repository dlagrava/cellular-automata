/*
 * No licence
 */

package evolutionaryAutomata;

import cellularautomata.core.*;
import cellularautomata.display.IExporter;
import java.util.Properties;
import java.util.Random;

/**
 *  
 * @author Daniel Lagrava
 */
public class GeneticCellularAutomata extends IntegerCellularAutomata2D {

    private LookupTable[][] lookupTables; // every cell has its own lookuptable
    private double[][] fitness; // to keep track of the fitness of each cell

    // show the value of a cell (display only)
    private boolean showCellValue;
    // make mutations after crossover step?
    private boolean performMutation;


    // to keep track of the global time
    private int globalTimer;
    // the number of steps before a crossover operation
    private int evolutionStep;
    // object that computes all kind of statistics
    private Statistics internalStatistics;

    // object charged of the crossover step
    private ICrossoverEngine crossOverEngine;

    // array that contains the crossover parameters
    private double[] crossoverParameters;

    // types of rule distributions, crossover and initial 1's
    public enum RuleDistribution {HORIZONTALSPLIT,VERTICALSPLIT,RANDOM};
    public enum CrossoverType {UNIFORM,ONEPOINT,TWOPOINT};
    public enum StartingPattern {SQUARE,CIRCLE,RANDOM};
    
    public GeneticCellularAutomata(int sizeX, int sizeY, Setup setup, int evolutionStep, boolean cellStatus){
        super(sizeX,sizeY,setup);
        
        // evolution step marks when we will perform the crossover   
        this.evolutionStep = evolutionStep;
        // internal counter to see if we approach the crossover step
        globalTimer = 1;
        
        // allocate the lookup tables
        lookupTables = new LookupTable[realSizeX][realSizeY];
        // Initialise the fitness table
        fitness = new double[realSizeX][realSizeY];

        showCellValue = cellStatus;

        // Initialize a centered square to start evolution
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
            int powerOfTwo = 1 << iK; //2^iK
            LUTIndex += powerOfTwo * values[iK];
        }
        
        return lookupTables[x][y].getValue(LUTIndex);
    }
    
    @Override
    public void postProcessing(){
        // computation of the fitness of every cell
        computeFitness();
        if (globalTimer % evolutionStep == 0){
            int crossOverNumber = 0;
            Double meanFitness = internalStatistics.meanFitness(fitness, xStart, xEnd, xStart, xEnd);
            System.out.println("Crossover Time!");
            System.out.println("Mean fitness of the system = " + meanFitness);
            for (int iX = xStart; iX < xEnd; iX++){
                for (int iY = yStart; iY < yEnd; iY++){
                    // choose unfit individuals to make crossover
                    if (fitness[iX][iY] < meanFitness){
                        // for each cell choose the fittest neighbor
                        int[] fittest = chooseFittestNeighbor(iX, iY);
                        // avoid a crossover with itself
                        if (!(fittest[0] == iX && fittest[1] == iY)){

                            // test if the chosen neighbor does not have the same rule
                            if (lookupTables[iX][iY].equals(lookupTables[fittest[0]][fittest[1]]))
                                continue;

                            // apply crossover using the crossover engine
                            double biais = Math.abs(fitness[iX][iY]-fitness[fittest[0]][fittest[1]])/(double)(evolutionStep/2);
                            lookupTables[iX][iY] = crossOverEngine.applyCrossover(lookupTables[iX][iY],lookupTables[fittest[0]][fittest[1]],crossoverParameters);
                            crossOverNumber++;
                        }
                    }
                }
            }
            System.out.println("A total of "+ crossOverNumber + " crossovers were made.");
            System.out.println("-------------------------------------------------------");
            // if crossover then we reset the fitness
            resetFitness();
            internalStatistics.rulesHistogram(lookupTables, xStart, xEnd, yStart, yEnd);
        }
    }
         
    /**
     * 
     */
    private void computeFitness(){
    // we compute the fitness for each cell
       for (int iX = xStart; iX < xEnd; iX++){
            for (int iY = yStart; iY < yEnd; iY++){
                // we comparte the old value
                int oldValue = temporaryArrays[iX][iY][0];
                // and the new value of the CA
                int newValue = getValue(iX, iY);
                // we say that if a resource is consumed the fitness is better
                if (oldValue == 1 && newValue == 0) {
                    fitness[iX][iY] += 1.0;
                }
            }

       }

    }

    /**
     *
     *
     */
    private void resetFitness(){
        for (int iX = xStart; iX < xEnd; iX++){
            for (int iY = yStart; iY < yEnd; iY++){
                fitness[iX][iY] = 0.0;
            }
        }

    }

    @Override
    public void collisionAndPropagation() {
        globalTimer++;
        super.collisionAndPropagation();
    }
        
    /**
     * Choose the neighbor that has the best fitness
     * @param x
     * @param y
     * @return
     */
    private int[] chooseFittestNeighbor(int x, int y){
        int[][] neighbors = setup.getNeighborIndices(x, y);
        int chosen = 0;
        for (int i = 0; i < neighbors.length; i++){
            if (fitness[neighbors[i][0]][neighbors[i][1]] > fitness[neighbors[chosen][0]][neighbors[chosen][1]])
                chosen = i;
        }
        int[] result = {neighbors[chosen][0],neighbors[chosen][1]};
        return result;
    }

    /**
     * Instead of passing the values of the cells, we are going to export the values
     * of the conversion of the LUT into a 32-bits integer value. This allows to visually
     * verify the generation of new different rules.
     * @param writer
     */
    @Override
    public void exportValues(IExporter writer){
        int[][] values = new int [sizeX][sizeY];
        int width = setup.getBoundaryWidth();
        for (int iX = xStart; iX < xEnd; iX++){
            for (int iY = yStart; iY < yEnd; iY++){
                if (showCellValue){
                    values[iX-width][iY-width] = lookupTables[iX][iY].toInteger()*cells[iX][iY];
                }
                else {
                    values[iX-width][iY-width] = lookupTables[iX][iY].toInteger();
                }
            }
        }
        writer.writeIntegerValues(values);
    }

    /**
     * Initialization method for the crossover. MUST be called before using the CA.
     * @param type 
     * @param parameters several real valued parameters that depend on the crossover type.
     */

    public void selectCrossoverType(CrossoverType type, double ... parameters){
        switch (type){
            case UNIFORM:
                // if uniform crossover selected we need the probability
                crossoverParameters = new double[1];
                crossoverParameters[0] = parameters[0];
                crossOverEngine = new UniformCrossover();
                break;
            case ONEPOINT:
                crossOverEngine = new OnePointCrossover();
                break;
            case TWOPOINT:
                break;
            default:
                System.exit(1);

        }
    }

    /**
     * Initialize the rules according to the rule distribution type selected
     * @param type
     * @param rules
     */
    public void initializeRules(RuleDistribution type, LookupTable rule1, LookupTable rule2){
        // start the statistics object
        internalStatistics = new Statistics(rule1, rule2);
        // choose the layout of rules
        switch (type){
            // one rule above the other
            case HORIZONTALSPLIT:
                for (int iX = 0; iX < realSizeX; iX++){
                    for (int iY = 0; iY < realSizeY; iY++){
                        if (iX < realSizeX/2) lookupTables[iX][iY] = rule1.clone();
                        else lookupTables[iX][iY] = rule2.clone();
                    }
                }
                break;
            // one rule to the left and the other to the right
            case VERTICALSPLIT:
                for (int iX = 0; iX < realSizeX; iX++){
                    for (int iY = 0; iY < realSizeY; iY++){
                        if (iY < realSizeY/2) lookupTables[iX][iY] = rule1.clone();
                        else lookupTables[iX][iY] = rule2.clone();
                    }
                }
                break;
            // random distribution of the rules
            case RANDOM:
                Random rng = new Random();
                for (int iX = 0; iX < realSizeX; iX++){
                    for (int iY = 0; iY < realSizeY; iY++){
                        if (rng.nextDouble() > 0.5) lookupTables[iX][iY] = rule1.clone();
                        else lookupTables[iX][iY] = rule2.clone();
                    }
                }
                break;
            default:
                System.exit(1);

        }
    }

    /**
     * Use the internal Statistics object to return the original + mutated rules on
     * the system
     */
    public void countOriginalRules(){
        internalStatistics.countOriginalRules(lookupTables, xStart, xEnd, xStart, xEnd);
    }

    public void writeRulesStatistics(String fileName){
        System.out.println("Writing the histogram to "+fileName);
        internalStatistics.writeHistogram(fileName);
    }

}

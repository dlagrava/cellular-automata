package evolutionaryAutomata;

import cellularautomata.core.IExporter;
import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.LookupTable;
import cellularautomata.core.Setup;

import java.util.Arrays;
import java.util.Random;

/**
 * Integer CA where every cell carries its own lookup-table genome.
 *
 * <p>Fitness accumulates whenever a cell consumes a resource ({@code 1 → 0}).
 * Every {@code evolutionStep} ticks, cells below mean fitness adopt a crossover
 * offspring with their fittest neighbor (optional mutation afterward).
 *
 * <p>Call {@link #initializeRules} then {@link #selectCrossoverType} before
 * evolving; {@link #selectMutation} is optional (off by default).
 *
 * @author Daniel Lagrava
 */
public class GeneticCellularAutomata extends IntegerCellularAutomata2D {

    private final LookupTable[][] lookupTables;
    private final double[][] fitness;
    private final boolean showCellValue;
    private final int evolutionStep;
    private final Random rng;

    private boolean performMutation;
    private int globalTimer;
    private Statistics internalStatistics;
    private ICrossoverEngine crossOverEngine;
    private double[] crossoverParameters;
    private MutationEngine mutationEngine;
    private boolean rulesInitialized;

    /** How the two seed rules are laid out on the grid. */
    public enum RuleDistribution {HORIZONTALSPLIT, VERTICALSPLIT, RANDOM}

    /** Crossover strategy used during evolution. */
    public enum CrossoverType {UNIFORM, ONEPOINT, TWOPOINT}

    /** Initial pattern of resource cells ({@code 1}s). */
    public enum StartingPattern {SQUARE, CIRCLE, RANDOM}

    /**
     * Construct with a centered square starting pattern.
     *
     * @param sizeX         interior width
     * @param sizeY         interior height
     * @param setup         neighborhood / boundary setup
     * @param evolutionStep ticks between crossover generations
     * @param cellStatus    if true, export LUT×cell; else LUT only
     */
    public GeneticCellularAutomata(int sizeX, int sizeY, Setup setup, int evolutionStep, boolean cellStatus) {
        this(sizeX, sizeY, setup, evolutionStep, cellStatus, StartingPattern.SQUARE);
    }

    /**
     * @param sizeX         interior width
     * @param sizeY         interior height
     * @param setup         neighborhood / boundary setup
     * @param evolutionStep ticks between crossover generations
     * @param cellStatus    if true, export LUT×cell; else LUT only
     * @param pattern       initial placement of {@code 1}s
     */
    public GeneticCellularAutomata(int sizeX, int sizeY, Setup setup, int evolutionStep,
                                   boolean cellStatus, StartingPattern pattern) {
        super(sizeX, sizeY, setup);
        this.evolutionStep = evolutionStep;
        this.globalTimer = 1;
        this.lookupTables = new LookupTable[realSizeX][realSizeY];
        this.fitness = new double[realSizeX][realSizeY];
        this.showCellValue = cellStatus;
        this.rng = new Random();
        this.performMutation = false;
        this.rulesInitialized = false;
        initializePattern(pattern);
    }

    private void initializePattern(StartingPattern pattern) {
        int cx = sizeX / 2;
        int cy = sizeY / 2;
        int radius = Math.max(1, sizeX / 7);
        switch (pattern) {
            case SQUARE -> {
                for (int iX = cx - radius; iX < cx + radius; iX++) {
                    for (int iY = cy - radius; iY < cy + radius; iY++) {
                        setValue(iX, iY, 1);
                    }
                }
            }
            case CIRCLE -> {
                int r2 = radius * radius;
                for (int iX = cx - radius; iX <= cx + radius; iX++) {
                    for (int iY = cy - radius; iY <= cy + radius; iY++) {
                        int dx = iX - cx;
                        int dy = iY - cy;
                        if (dx * dx + dy * dy <= r2) {
                            setValue(iX, iY, 1);
                        }
                    }
                }
            }
            case RANDOM -> {
                double density = 0.08;
                for (int iX = 0; iX < sizeX; iX++) {
                    for (int iY = 0; iY < sizeY; iY++) {
                        if (rng.nextDouble() < density) {
                            setValue(iX, iY, 1);
                        }
                    }
                }
            }
            default -> throw new IllegalArgumentException("Unknown starting pattern: " + pattern);
        }
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        int LUTIndex = 0;
        for (int iK = 0; iK < values.length; iK++) {
            int powerOfTwo = 1 << iK;
            LUTIndex += powerOfTwo * values[iK];
        }
        return lookupTables[x][y].getValue(LUTIndex);
    }

    @Override
    public void postProcessing() {
        ensureReady();
        computeFitness();
        if (globalTimer % evolutionStep == 0) {
            Double meanFitness = internalStatistics.meanFitness(fitness, xStart, xEnd, yStart, yEnd);
            for (int iX = xStart; iX < xEnd; iX++) {
                for (int iY = yStart; iY < yEnd; iY++) {
                    if (fitness[iX][iY] < meanFitness) {
                        int[] fittest = chooseFittestNeighbor(iX, iY);
                        if (fittest[0] == iX && fittest[1] == iY) {
                            continue;
                        }
                        if (lookupTables[iX][iY].equals(lookupTables[fittest[0]][fittest[1]])) {
                            continue;
                        }
                        double bias = Math.abs(fitness[iX][iY] - fitness[fittest[0]][fittest[1]])
                                / (double) (evolutionStep / 2);
                        lookupTables[iX][iY] = crossOverEngine.applyCrossover(
                                lookupTables[iX][iY],
                                lookupTables[fittest[0]][fittest[1]],
                                crossoverParametersWithBias(bias));
                        if (performMutation) {
                            mutationEngine.lookUpTableMutation(lookupTables[iX][iY]);
                        }
                    }
                }
            }
            resetFitness();
            internalStatistics.rulesHistogram(lookupTables, xStart, xEnd, yStart, yEnd);
        }
    }

    private double[] crossoverParametersWithBias(double bias) {
        if (crossoverParameters == null || crossoverParameters.length == 0) {
            return new double[]{bias};
        }
        double[] params = Arrays.copyOf(crossoverParameters, crossoverParameters.length + 1);
        params[params.length - 1] = bias;
        return params;
    }

    private void ensureReady() {
        if (!rulesInitialized) {
            throw new IllegalStateException("Call initializeRules before evolving");
        }
        if (crossOverEngine == null) {
            throw new IllegalStateException("Call selectCrossoverType before evolving");
        }
    }

    private void computeFitness() {
        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                int oldValue = temporaryArrays[iX][iY][0];
                int newValue = getCell(iX, iY);
                if (oldValue == 1 && newValue == 0) {
                    fitness[iX][iY] += 1.0;
                }
            }
        }
    }

    private void resetFitness() {
        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
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
     * Neighbor with the highest fitness (may be {@code (x,y)} if it is best).
     */
    private int[] chooseFittestNeighbor(int x, int y) {
        int[][] neighbors = setup.getNeighborIndices(x, y);
        int chosen = 0;
        for (int i = 0; i < neighbors.length; i++) {
            if (fitness[neighbors[i][0]][neighbors[i][1]]
                    > fitness[neighbors[chosen][0]][neighbors[chosen][1]]) {
                chosen = i;
            }
        }
        return new int[]{neighbors[chosen][0], neighbors[chosen][1]};
    }

    /**
     * Export LUT-as-int (optionally multiplied by cell value) for visualization.
     */
    @Override
    public void exportValues(IExporter writer) {
        int[][] values = new int[sizeX][sizeY];
        int width = setup.getBoundaryWidth();
        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                if (showCellValue) {
                    values[iX - width][iY - width] = lookupTables[iX][iY].toInteger() * cells[iX][iY];
                } else {
                    values[iX - width][iY - width] = lookupTables[iX][iY].toInteger();
                }
            }
        }
        writer.writeIntegerValues(values);
    }

    /**
     * Select the crossover engine. Must be called before evolving.
     *
     * @param type       crossover kind
     * @param parameters e.g. mixing ratio for {@link CrossoverType#UNIFORM}
     */
    public void selectCrossoverType(CrossoverType type, double... parameters) {
        switch (type) {
            case UNIFORM -> {
                crossoverParameters = new double[]{parameters[0]};
                crossOverEngine = new UniformCrossover(rng);
            }
            case ONEPOINT -> {
                crossoverParameters = null;
                crossOverEngine = new OnePointCrossover(rng);
            }
            case TWOPOINT -> {
                crossoverParameters = null;
                crossOverEngine = new TwoPointCrossover(rng);
            }
            default -> throw new IllegalArgumentException("Unknown crossover type: " + type);
        }
    }

    /**
     * Enable post-crossover LUT mutation with the given probability (default off).
     * Call after {@link #initializeRules}.
     *
     * @param probability mutation probability in {@code [0, 1]}
     */
    public void selectMutation(double probability) {
        if (!rulesInitialized) {
            throw new IllegalStateException("Call initializeRules before selectMutation");
        }
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("mutation probability must be in [0, 1]");
        }
        int tableSize = lookupTables[xStart][yStart].getLength();
        mutationEngine = new MutationEngine(probability, tableSize, rng);
        performMutation = probability > 0.0;
    }

    /**
     * Place the two seed rules according to {@code type}. Must be called before evolving.
     */
    public void initializeRules(RuleDistribution type, LookupTable rule1, LookupTable rule2) {
        internalStatistics = new Statistics(rule1, rule2);
        switch (type) {
            case HORIZONTALSPLIT -> {
                for (int iX = 0; iX < realSizeX; iX++) {
                    for (int iY = 0; iY < realSizeY; iY++) {
                        if (iX < realSizeX / 2) {
                            lookupTables[iX][iY] = rule1.clone();
                        } else {
                            lookupTables[iX][iY] = rule2.clone();
                        }
                    }
                }
            }
            case VERTICALSPLIT -> {
                for (int iX = 0; iX < realSizeX; iX++) {
                    for (int iY = 0; iY < realSizeY; iY++) {
                        if (iY < realSizeY / 2) {
                            lookupTables[iX][iY] = rule1.clone();
                        } else {
                            lookupTables[iX][iY] = rule2.clone();
                        }
                    }
                }
            }
            case RANDOM -> {
                for (int iX = 0; iX < realSizeX; iX++) {
                    for (int iY = 0; iY < realSizeY; iY++) {
                        if (rng.nextDouble() > 0.5) {
                            lookupTables[iX][iY] = rule1.clone();
                        } else {
                            lookupTables[iX][iY] = rule2.clone();
                        }
                    }
                }
            }
            default -> throw new IllegalArgumentException("Unknown rule distribution: " + type);
        }
        rulesInitialized = true;
    }

    /**
     * @return {@code {countRule1, countRule2}} for cells still on the seed LUTs
     */
    public int[] countOriginalRules() {
        if (!rulesInitialized) {
            throw new IllegalStateException("Call initializeRules before countOriginalRules");
        }
        return internalStatistics.countOriginalRules(lookupTables, xStart, xEnd, yStart, yEnd);
    }

    /**
     * Persist the last rule histogram built during a crossover generation.
     */
    public void writeRulesStatistics(String fileName) {
        if (internalStatistics == null) {
            throw new IllegalStateException("Call initializeRules before writeRulesStatistics");
        }
        internalStatistics.writeHistogram(fileName);
    }
}

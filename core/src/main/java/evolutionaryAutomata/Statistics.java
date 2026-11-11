/*
 *
 */

package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;


/**
 * Facilitate the analysis of results by mesuring different statistics
 * from an evolutionary CA.
 *
 * @author lagravas
 */
public class Statistics {

    // the two original rules
    private LookupTable originalRule1;
    private LookupTable originalRule2;
    HashMap<String, Integer> histogram;

    public Statistics(LookupTable rule1, LookupTable rule2) {
        originalRule1 = rule1.clone();
        originalRule2 = rule2.clone();
    }

    public void rulesHistogram(LookupTable[][] rules, int xStart, int xEnd, int yStart, int yEnd) {
        histogram = new HashMap<String, Integer>();
        histogram.put("OriginalRule1", 0);
        histogram.put("OriginalRule2", 0);


        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                if (rules[iX][iY].equals(originalRule1))
                    histogram.put("OriginalRule1", histogram.get("OriginalRule1").intValue() + 1);
                else {
                    if (rules[iX][iY].equals(originalRule2))
                        histogram.put("OriginalRule2", histogram.get("OriginalRule2").intValue() + 1);
                    else {
                        if (histogram.containsKey(rules[iX][iY].toString())) {
                            histogram.put(rules[iX][iY].toString(), histogram.get(rules[iX][iY].toString()).intValue() + 1);
                        } else
                            histogram.put(rules[iX][iY].toString(), 1);
                    }
                }


            }
        }
    }

    public void countOriginalRules(LookupTable[][] luts, int startX, int endX, int startY, int endY) {
        // Formerly printed rule counts to stdout; no library-side console output.
    }

    /**
     * Compute the mean fitness of the whole system.
     *
     * @param fitness
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    public double meanFitness(double[][] fitness, int startX, int endX, int startY, int endY) {
        double mean = 0.0;
        for (int iX = startX; iX < endX; iX++) {
            for (int iY = startY; iY < endY; iY++) {
                mean += fitness[iX][iY];
            }
        }
        return mean / (double) ((endX - startX) * (endY - startY));
    }

    public void writeHistogram(String fileName) {
        Writer output = null;
        File file = new File(fileName);
        try {
            output = new BufferedWriter(new FileWriter(file));

            for (String rule : histogram.keySet()) {
                output.write(rule + " " + histogram.get(rule) + "\n");
            }
            output.close();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write histogram to " + fileName, e);
        }
    }

}

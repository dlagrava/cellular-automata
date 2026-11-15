package evolutionaryAutomata;

import cellularautomata.core.LookupTable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Fitness and rule-distribution helpers for an evolutionary CA.
 *
 * @author Daniel Lagrava
 */
public class Statistics {

    private final LookupTable originalRule1;
    private final LookupTable originalRule2;
    private Map<String, Integer> histogram;

    public Statistics(LookupTable rule1, LookupTable rule2) {
        originalRule1 = rule1.clone();
        originalRule2 = rule2.clone();
    }

    /**
     * Rebuild the rule-frequency histogram over the given LUT grid region.
     */
    public void rulesHistogram(LookupTable[][] rules, int xStart, int xEnd, int yStart, int yEnd) {
        histogram = new HashMap<>();
        histogram.put("OriginalRule1", 0);
        histogram.put("OriginalRule2", 0);

        for (int iX = xStart; iX < xEnd; iX++) {
            for (int iY = yStart; iY < yEnd; iY++) {
                if (rules[iX][iY].equals(originalRule1)) {
                    histogram.put("OriginalRule1", histogram.get("OriginalRule1") + 1);
                } else if (rules[iX][iY].equals(originalRule2)) {
                    histogram.put("OriginalRule2", histogram.get("OriginalRule2") + 1);
                } else {
                    String key = rules[iX][iY].toString();
                    histogram.put(key, histogram.getOrDefault(key, 0) + 1);
                }
            }
        }
    }

    /**
     * Count cells still carrying the two seed rules.
     *
     * @return {@code {countRule1, countRule2}}
     */
    public int[] countOriginalRules(LookupTable[][] luts, int startX, int endX, int startY, int endY) {
        int count1 = 0;
        int count2 = 0;
        for (int iX = startX; iX < endX; iX++) {
            for (int iY = startY; iY < endY; iY++) {
                if (luts[iX][iY].equals(originalRule1)) {
                    count1++;
                } else if (luts[iX][iY].equals(originalRule2)) {
                    count2++;
                }
            }
        }
        return new int[]{count1, count2};
    }

    /**
     * Mean fitness over the given region.
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

    /**
     * Write the last {@link #rulesHistogram} result as {@code rule count} lines.
     */
    public void writeHistogram(String fileName) {
        if (histogram == null) {
            throw new IllegalStateException("No histogram; call rulesHistogram first");
        }
        Path path = Path.of(fileName);
        try (BufferedWriter output = Files.newBufferedWriter(path)) {
            for (Map.Entry<String, Integer> entry : histogram.entrySet()) {
                output.write(entry.getKey() + " " + entry.getValue());
                output.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write histogram to " + fileName, e);
        }
    }
}

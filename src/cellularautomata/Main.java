/*
 *  Example class
 */
package cellularautomata;

import evolutionaryAutomata.GeneticCADisplayer;
import cellularautomata.core.*;
import evolutionaryAutomata.GeneticCellularAutomata;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Daniel Lagrava
 */
public class Main {

    public static void main(String[] args) {
        int sizeX = 200;
        int sizeY = 200;
        int iterations = 20000;
        int itCrossover = 30;
        int itStats = 2000;

        Setup setup = new Setup(BoundaryFactory.BoundaryType.REFLEXIVE, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
        boolean showCellValue = false;
        GeneticCellularAutomata ca = new GeneticCellularAutomata(sizeX, sizeY, setup, itCrossover, showCellValue);

        // initialization of the genetic CA
        LookupTable rule1 = LookupTableFactory.createLookupTable(LookupTableFactory.Type.WEST, setup.getNeighborNumber());
        LookupTable rule2 = LookupTableFactory.createLookupTable(LookupTableFactory.Type.PARITY, setup.getNeighborNumber());
        ca.initializeRules(GeneticCellularAutomata.RuleDistribution.HORIZONTALSPLIT, rule1, rule2);
        // creating crossover parameters
        double mixingRate = 0.5;
        double[] crossoverParameters = {mixingRate};
        // choice of the crossover type
        ca.selectCrossoverType(GeneticCellularAutomata.CrossoverType.UNIFORM, crossoverParameters);

        int xScale = 2;
        int yScale = 2;
        GeneticCADisplayer panel = new GeneticCADisplayer(sizeX, sizeY, xScale, yScale);
        //NoDisplayer panel = new NoDisplayer();


        // To show the CA //////////////////
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(panel));
        frame.setSize(sizeX*xScale, sizeY*yScale);
        frame.setLocation(50, 50);
        frame.setVisible(true);
        ///////////////////////////////////

        for (int iT = 0; iT < iterations; iT++) {
            ca.collisionAndPropagation();


            if ( iT % itCrossover == 0 ) {
                ca.exportValues(panel);
                frame.repaint();

            }
            if ( iT%itStats == 0 ){
                ca.countOriginalRules();
            }
        }

        ca.writeRulesStatistics("histogram.dat");

        System.exit(0);
    }
}

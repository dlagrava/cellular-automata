/*
 * No licence
 */
package cellularautomata.examples;

import cellularautomata.display.*;
import cellularautomata.core.*;
import javax.swing.*;

/**
 *
 * @author Daniel Lagrava
 */
public class Parity extends IntegerCellularAutomata2D {

    public Parity(int sizeX, int sizeY, Setup setup) {
        super(sizeX, sizeY, setup);
        Tools.initializeFilledSquare(cells, 1, sizeX/3, sizeY/3, sizeX/3);
    }

    @Override
    public int applyRule(int x, int y, int[] values) {
        int res = 0;
        for (int iK = 1; iK < values.length; iK++) {
            res += values[iK];
        }
        return res % 2;
    }

    public static void main(String[] args) {
        int sizeX = 200;
        int sizeY = 200;
        int iterations = 2000;


        Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
        ICellularAutomata ca = new Parity(sizeX, sizeY, setup);

        ColorMap colorMap = new HotColorMap(0, 1);
        ImageDisplayer panel = new ImageDisplayer(sizeX, sizeY, colorMap);

        // To show the CA //////////////////
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(panel));
        frame.setSize(sizeX, sizeY);
        frame.setLocation(50, 50);
        frame.setVisible(true);
        ///////////////////////////////////

        for (int iT = 0; iT < iterations; iT++) {
            ca.exportValues(panel);
            frame.repaint();

            ca.copyNeighborValues();
            ca.collision();
            ca.postProcessing();

        }

        System.exit(0);

    }
}

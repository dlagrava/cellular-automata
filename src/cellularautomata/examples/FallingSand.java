/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata.examples;

import cellularautomata.core.*;
import cellularautomata.display.*;
import javax.swing.*;

/**
 * Implementation of the Sand clock CA. It contains 3 states, 0 = void, 1 = sand, 2 = glass.
 * @author lagravas
 */
public class FallingSand extends IntegerCellularAutomata2D {

    public final int VOID = 0;
    public final int SAND = 1;
    public final int GLASS = 2;
    

    /**
     * 
     * @param nx
     * @param ny
     * @param setup
     */
    public FallingSand(int nx, int ny, Setup setup){
        super(nx,ny,setup);
        initializeClock();

    }

    /**
     * Fill the outside of the clock with GLASS
     */
    public void initializeClock(){
        int[] center = {sizeX/3,sizeY/7};
        Tools.initializeDisk(cells, SAND, center, sizeY/8);

        for (int iX = 1; iX < realSizeX/2-6; iX++){
            int iY = realSizeY/2;
            setValue(iX, iY+4, GLASS);
            setValue(iX, iY+5, GLASS);
        }
        
        for (int iX = realSizeX/2+1; iX < realSizeX-1; iX++){
            int iY = realSizeY/2;
            setValue(iX, iY, GLASS);
            setValue(iX, iY+1, GLASS);
        }

        for (int iX = 1; iX < realSizeX-1; iX++){
            int iY = 9*realSizeY/10;
            setValue(iX, iY, GLASS);
        }

    }

    /**
     *
     * @param x
     * @param y
     * @param values
     * @return
     */
    @Override
    public int applyRule(int x, int y, int[] values) {
        // GLASS valued cells do not move
        if ((getValue(x, y) & GLASS) == 2) return getValue(x, y);
        
        // retrieving neighbors twice to get same status
        int[][] neighbors = setup.getNeighborIndices(x, y);
        neighbors = setup.getNeighborIndices(x, y);

        // find if the cell is in position 0,1,2 or 3
        int position = (x-neighbors[0][0]) + (y-neighbors[0][1])*2;

        int gul = (GLASS & values[0]) >> 1;
        int gur = (GLASS & values[1]) >> 1;
        int gll = (GLASS & values[2]) >> 1;
        int glr = (GLASS & values[3]) >> 1;

        int sul = (SAND & values[0]);
        int sur = (SAND & values[1]);
        int sll = (SAND & values[2]);
        int slr = (SAND & values[3]);

        switch (position) {
            case 0:
                return gll*sul+(1-gul)*sul*sll*(slr+(1-slr)*sur);
                //return values[0]*values[2]*(values[3]+(1-values[3])*values[1]);
            case 1:
                return glr*sur+(1-gur)*sur*slr*(sll+(1-sll)*sul);
                //return values[1]*values[3]*(values[2]+(1-values[2])*values[0]);
            case 2:
                return sll+(1-sll)*(sul*(1-gul)+(1-sul)*sur*(1-gur)*slr);
                //return values[2]+(1-values[2])*(values[0]+(1-values[0])*values[1]*values[3]);
            case 3:
                return slr+(1-slr)*(sur*(1-gur)+(1-sur)*sul*(1-gul)*sll);
                //return values[3]+(1-values[3])*(values[1]+(1-values[1])*values[0]*values[2]);
            default:
                System.out.println("Unknown value. Exiting. Check the rule.");
                System.exit(1);
                return -1;
        }
    }


    public int computeDensity(){
        int res = 0;
        for (int iX = 0; iX < sizeX; iX++) {
            for (int iY = 0; iY < sizeY; iY++) {
                if (getValue(iX, iY) == SAND) res++;
            }
        }
        return res;
    }

    public static void main(String[] args){
        int sizeX = 301;
        int sizeY = 301;
        int iterations = 10000;

        Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.MARGOLUS, sizeX, sizeY);
        ICellularAutomata ca = new FallingSand(sizeY, sizeY, setup);

        ColorMap colorMap = new JetColorMap(0, 2);
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
            ca.collisionAndPropagation();
            ca.exportValues(panel);
            frame.repaint();
        }
        System.exit(0);
    }

}

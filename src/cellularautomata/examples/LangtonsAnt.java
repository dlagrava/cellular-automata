/*
 * No licence
 */

package cellularautomata.examples;

import cellularautomata.display.*;
import cellularautomata.core.*;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Daniel Lagrava
 */
public class LangtonsAnt extends IntegerCellularAutomata2D {

    int[][] antPositions;
    int antNumber;
    
    public LangtonsAnt(int sizeX, int sizeY, Setup setup, int antNumber){
        super(sizeX,sizeY,setup);
        this.antNumber = antNumber;
        randomInit(antNumber, sizeX, sizeY);
    }
    
    private void randomInit(int n, int sx, int sy) {
        Random rnd = new Random();
        antPositions = new int[n][3];
        
        for (int i = 0; i < n; i++) {
            antPositions[i][0] = rnd.nextInt(sx);
            antPositions[i][1] = rnd.nextInt(sy);
            antPositions[i][2] = rnd.nextInt(4) + 1;
        }
    }
    
    @Override
    public int applyRule(int x, int y, int[] values) {
        int ant = hasAnt(x, y);
        // if there is an ant here
        if (ant != -1){
                 
            // if we are on a black square          
            if (values[0] == 0){                
                // the third value marks the direction
                switch (antPositions[ant][2]){
                    case 1:
                        antPositions[ant][0]--;
                        antPositions[ant][2] = 2;
                        break;
                    case 2:
                        antPositions[ant][1]--;
                        antPositions[ant][2] = 3;
                        break;
                    case 3:
                        antPositions[ant][0]++;
                        antPositions[ant][2] = 4;
                        break;
                    default:
                        antPositions[ant][1]++;
                        antPositions[ant][2] = 1;
                }
                return ant+1;
            }
            else {
                // the third value marks the direction
                switch (antPositions[ant][2]){
                    case 1:
                        antPositions[ant][0]++;
                        antPositions[ant][2] = 4;
                        break;
                    case 2:
                        antPositions[ant][1]++;
                        antPositions[ant][2] = 1;
                        break;
                    case 3:
                        antPositions[ant][0]--;
                        antPositions[ant][2] = 2;
                        break;
                    default:
                        antPositions[ant][1]--;
                        antPositions[ant][2] = 3;
                }
                return 0;
            }
        }
        return values[0];
    }
    
    private int hasAnt(int x, int y){
        for (int i = 0; i < antPositions.length; i++){
            if (x == antPositions[i][0] && y == antPositions[i][1])
                return i;
        }
        return -1;
    }

     public static void main(String[] args) {
        int sizeX = 200;
        int sizeY = 200;
        int iterations = 10000;
        int antNumber = 50;
        Setup setup = new Setup(BoundaryFactory.BoundaryType.PERIODIC, NeighborhoodFactory.NeighborhoodType.VONNEUMANN, sizeX, sizeY);
        ICellularAutomata ca = new LangtonsAnt(sizeX, sizeY, setup, antNumber);

        ColorMap colorMap = new HotColorMap(0, antNumber);
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

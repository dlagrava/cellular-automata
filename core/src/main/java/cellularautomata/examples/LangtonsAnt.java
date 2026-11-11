package cellularautomata.examples;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.core.Setup;

import java.util.Random;

/**
 * @author Daniel Lagrava
 */
public class LangtonsAnt extends IntegerCellularAutomata2D {

    int[][] antPositions;
    int antNumber;

    public LangtonsAnt(int sizeX, int sizeY, Setup setup, int antNumber) {
        super(sizeX, sizeY, setup);
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
        if (ant != -1) {

            // if we are on a black square          
            if (values[0] == 0) {
                // the third value marks the direction
                switch (antPositions[ant][2]) {
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
                return ant + 1;
            } else {
                // the third value marks the direction
                switch (antPositions[ant][2]) {
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

    private int hasAnt(int x, int y) {
        for (int i = 0; i < antPositions.length; i++) {
            if (x == antPositions[i][0] && y == antPositions[i][1])
                return i;
        }
        return -1;
    }
}

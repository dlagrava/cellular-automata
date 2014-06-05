/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata.core;

/**
 * A margolus neighborhood
 * @author lagravas
 */
public class MargolusNeighborhood implements INeighborhood {

    private int iteration;
    private int[][] oddIndexes;
    private int[][] evenIndexes;

    private final int NEIGHBORS = 5; // 4 neighbors + itself
    private final int RADIUS = 1;

    public MargolusNeighborhood(int x, int y){
        this.iteration = 0;

        oddIndexes = new int[NEIGHBORS][2];
        evenIndexes = new int[NEIGHBORS][2];

        // filling the x
        if (x % 2 == 0) {
            evenIndexes[0][0] = x;
            evenIndexes[1][0] = x+1;
            evenIndexes[2][0] = x;
            evenIndexes[3][0] = x+1;
            oddIndexes[0][0] = x-1;
            oddIndexes[1][0] = x;
            oddIndexes[2][0] = x-1;
            oddIndexes[3][0] = x;
        }
        if (x % 2 == 1) {
            evenIndexes[0][0] = x-1;
            evenIndexes[1][0] = x;
            evenIndexes[2][0] = x-1;
            evenIndexes[3][0] = x;
            oddIndexes[0][0] = x;
            oddIndexes[1][0] = x+1;
            oddIndexes[2][0] = x;
            oddIndexes[3][0] = x+1;
        }

        if (y % 2 == 0) {
            evenIndexes[0][1] = y;
            evenIndexes[1][1] = y;
            evenIndexes[2][1] = y+1;
            evenIndexes[3][1] = y+1;
            oddIndexes[0][1] = y-1;
            oddIndexes[1][1] = y-1;
            oddIndexes[2][1] = y;
            oddIndexes[3][1] = y;
        }
        if (y % 2 == 1) {
            evenIndexes[0][1] = y-1;
            evenIndexes[1][1] = y-1;
            evenIndexes[2][1] = y;
            evenIndexes[3][1] = y;
            oddIndexes[0][1] = y;
            oddIndexes[1][1] = y;
            oddIndexes[2][1] = y+1;
            oddIndexes[3][1] = y+1;
        }

        // the last neighbor is always de point itself
        evenIndexes[4][0] = oddIndexes[4][0] = x;
        evenIndexes[4][1] = oddIndexes[4][1] = y;

    }

    public int[][] getNeighborIndices(int x, int y) {
        if (iteration % 2 == 0){
            iteration++;
            return evenIndexes;
        }
        else {
            iteration++;
            return oddIndexes;
        }
    }

    public int getTotalNeighborNumber() {
        return NEIGHBORS;
    }

    public int getRadius() {
        return RADIUS;
    }

}

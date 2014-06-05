/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hpp;

import cellularautomata.core.IntegerCellularAutomata2D;
import cellularautomata.display.ImageDisplayer;
import cellularautomata.examples.*;

/**
 *
 * @author lagravas
 */
public class RunableIterations extends Thread {
    public boolean pause = false;
    public boolean reverse = false;

    private int maxIt = 0;

    private ImageDisplayer displayer;
    private IntegerCellularAutomata2D internalCA;

    public RunableIterations(ImageDisplayer CA, IntegerCellularAutomata2D p) {
        this.displayer = CA;
        this.internalCA = p;
    }
    
    @Override
    public void run() {
        System.err.println("Run for " + maxIt + " iterations");
        for (int i = 0; i < maxIt; i++) {
            
            if ( ! reverse ) internalCA.collisionAndPropagation();
            else {
                ((HPP) internalCA).reverse();
                reverse = false;
            }

            internalCA.exportValues(displayer);
            displayer.repaint();            
            
            synchronized (this) {
                while ( pause ) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        System.err.println("Thread interrupted !");
                    }
                }
            }
        }
        try {
            join();
        } catch (InterruptedException ex) {
            System.err.println("Thread interrupted !");
        }
    }

    public void addParticle() {
        ((HPP) internalCA).addParticle();
    }

    public void setMaxIt(int it) {
        maxIt = it;
    }
    
}

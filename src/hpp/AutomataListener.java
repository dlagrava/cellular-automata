/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hpp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author lagravas
 */
public class AutomataListener implements ActionListener {

    private final JButton sim;
    private final JButton reverse;
    private final JButton addParticle;

    private final JTextField nIter;

    private final static String RUN = "Simulate";
    private final static String PAUSE = "Pause";

    private final RunableIterations iteration;

    public AutomataListener(JButton sim, JButton reverse, JButton addParticle, JTextField nIter, RunableIterations iteration) {
        this.sim = sim;
        this.reverse = reverse;
        this.addParticle = addParticle;
        this.nIter = nIter;
        this.iteration = iteration;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sim) {
            if (!iteration.isAlive()) {
                iteration.start();
                iteration.setMaxIt( Integer.valueOf( nIter.getText() ) );
                sim.setText(PAUSE);
            } else {
                if (iteration.pause) {
                    synchronized (iteration) {
                        iteration.pause = false;
                        iteration.notify();
                    }
                    sim.setText(PAUSE);
                } else {
                    synchronized (iteration) {
                        iteration.pause = true;
                    }
                    sim.setText(RUN);
                }
            }
        }

        if (event.getSource() == reverse) {
            synchronized (iteration) {
                iteration.reverse = !iteration.reverse;
            }
        }

        if (event.getSource() == addParticle) {
            iteration.addParticle();
        }

        if (event.getSource() == null) {
            iteration.addParticle();
        }
    }
}

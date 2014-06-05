/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hpp;

import cellularautomata.core.IntegerCellularAutomata2D;
import static cellularautomata.core.BoundaryFactory.BoundaryType.*;
import static cellularautomata.core.NeighborhoodFactory.NeighborhoodType.*;

import cellularautomata.core.Setup;
import cellularautomata.display.*;
import cellularautomata.display.ImageDisplayer;
import cellularautomata.examples.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author lagravas
 */
public class Gui extends JFrame {

    private final static String RUN = "Simulate";
    private final static String PAUSE = "Pause";
    private ImageDisplayer displayer;
    private IntegerCellularAutomata2D internalAutomata;
    private JPanel mainPanel = new JPanel();
    private final JButton sim = new JButton(RUN);
    private final JButton reverse;
    private final JButton addParticle;
    private final RunableIterations iteration;

    public Gui(int W, int H, IntegerCellularAutomata2D internalAutomata) {
        this.internalAutomata = internalAutomata;
        displayer = new ImageDisplayer(W, H, new GrayLevelColorMap(0, HPP.SOLID - 1));
        iteration = new RunableIterations(displayer, internalAutomata);

        mainPanel.setLayout(new GridLayout(0, 2));
        setPreferredSize(new Dimension(2 * W, H));
        mainPanel.add(displayer);

        JPanel buttonPanel = new JPanel();
        GridLayout layout = new GridLayout(3, 2, 50, 50);
        buttonPanel.setLayout(layout);

        reverse = new JButton("Reverse");
        addParticle = new JButton("Add Particle");

        JLabel empty = new JLabel(" ");
        JTextField textIter = new JTextField("100");
        ActionListener listener = new AutomataListener( sim, reverse, addParticle, textIter, iteration );

        sim.addActionListener( listener );
        
        
        
        reverse.addActionListener( listener );        
        addParticle.addActionListener( listener );

        buttonPanel.add(sim);
        buttonPanel.add(textIter);
        buttonPanel.add(reverse);
        buttonPanel.add(empty);
        buttonPanel.add(addParticle);

        mainPanel.add(buttonPanel);
    }

    public void display() {
        add(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        int W = 300;
        int H = 300;
        Setup setup = new Setup(PERIODIC, VONNEUMANN, W, H);
        IntegerCellularAutomata2D ca = new HPP(H, W, setup);

        Gui g = new Gui(H, W, ca);
        g.display();
    }

    private class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == sim) {
                if (!iteration.isAlive()) {
                    iteration.start();
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
        }
    };
}


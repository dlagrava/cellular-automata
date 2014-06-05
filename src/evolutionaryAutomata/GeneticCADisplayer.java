/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evolutionaryAutomata;
import cellularautomata.display.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author lagravas
 */
public class GeneticCADisplayer extends JPanel implements IExporter  {

    private int width;
    private int height;
    private int xScale;
    private int yScale;

    private BufferedImage image;


    public GeneticCADisplayer(int width, int height){
        this.width = width;
        this.height = height;

        this.xScale = 1;
        this.yScale = 1;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public GeneticCADisplayer(int width, int height, int xScale, int yScale){
        this.width = width;
        this.height = height;
        this.xScale = xScale;
        this.yScale = yScale;
        image = new BufferedImage(this.width*xScale, this.height*yScale, BufferedImage.TYPE_INT_RGB);
    }

    public void writeIntegerValues(int[][] values) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = values[i][j];
                if (xScale == 1 && yScale == 1){
                    image.setRGB(i, j, value);
                }
                else {
                    for (int iX = xScale*i; iX < xScale*(i+1);iX++){
                        for (int iY = yScale*j; iY < yScale*(j+1);iY++){
                            image.setRGB(iX, iY, value);
                        }
                    }

                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cellularautomata.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author lagravas
 */
public class Tools {
    
    /**
     * 
     * @param fileName
     * @param table
     */
    public static void initializeFromBinaryImage(String fileName, int[][] table){
        try {
            BufferedImage image =  ImageIO.read(new File(fileName));
            table = new int[image.getHeight()][image.getWidth()];
            for (int iX = 0; iX < table.length; ++iX){
                for (int iY = 0; iY < table[iX].length; ++iY){
                    table[iX][iY] = image.getRGB(iX, iY);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param table
     * @param value
     */
    public static void initializeHalfWithValue(int[][] table, int value){
        for (int iX = 0; iX < table.length; ++iX){
            for (int iY = 0; iY < table[iX].length/2; ++iY){
                table[iX][iY] = value;
            }
        }
    }

    /**
     *
     * @param table
     * @param value
     * @param radius
     */
    public static void initializeCircle(int[][] table, int value, int[] center, int radius){
        for (int iX = 0; iX < table.length; ++iX){
            for (int iY = 0; iY < table[iX].length; ++iY){
                if ((iX-center[0])*(iX-center[0])+(iY-center[1])*(iY-center[1]) == radius*radius){
                    table[iX][iY] = value;
                }
            }
        }

    }


    public static void initializeDisk(int[][] table, int value, int[] center, int radius){
        for (int iX = 0; iX < table.length; ++iX){
            for (int iY = 0; iY < table[iX].length; ++iY){
                if ((iX-center[0])*(iX-center[0])+(iY-center[1])*(iY-center[1]) <= radius*radius){
                    table[iX][iY] = value;
                }
            }
        }

    }


    /**
     * 
     * @param table
     * @param value
     * @param cornerX
     * @param cornerY
     */
    public static void initializeFilledSquare(int[][] table, int value, int cornerX, int cornerY, int side){
        for (int iX = cornerX; iX < cornerX + side; ++iX){
            for (int iY = cornerY; iY < cornerY + side; ++iY){
                table[iX][iY] = value;
            }
        }
    }

    public static void initializeEmptySquare(int[][] table, int value, int cornerX, int cornerY, int side){
        
        for (int iX = cornerX; iX < cornerX + side; ++iX){
            table[iX][cornerY] = value;
            table[iX][cornerY+side-1] = value;
        }

        for (int iY = cornerY; iY < cornerY+side; ++iY){
            table[cornerX][iY] = value;
            table[cornerX+side-1][iY] = value;
        }

    }

    /**
     * 
     * @param table
     * @param values
     * @param probability
     */
    public static void initializeRandom(int[][] table, int[] values){
        Random rng = new Random();
        for (int iX = 0; iX < table.length; ++iX){
            for (int iY = 0; iY < table[iX].length; ++iY){
                int index = (int)(rng.nextDouble()*values.length);
                table[iX][iY] = values[index];
            }
        }
    }
}

package Utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Mike on 10/11/2016.
 */
public class ImageToTextFile {

    public static void main(String[] args)
    {
        BufferedImage img = null;
        String fileLoc = "C:\\Users\\Mike\\Desktop\\data.txt";


        try {
            img = ImageIO.read(new File("C:\\Users\\Mike\\Pictures\\agario1.png"));
            Color[][] colorArray = ImageProcessor.loadBufferedImage(img);
            ImageProcessor imageProcessor = new ImageProcessor();
            Color[][] downScaled = imageProcessor.downScale(colorArray, 8);
            imageProcessor.pixelCheck(downScaled);
            String data =  ImageProcessor.colorArrayToString(downScaled);
            System.out.println("Data gathered.");

            try {
                PrintWriter out = new PrintWriter( fileLoc );
                out.println( data );
                out.close();
            } catch (IOException e) {}

        } catch (IOException e) {
        }
    }

}

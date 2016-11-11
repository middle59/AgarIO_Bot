package Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 8/25/2016.
 */
public class ImageProcessor
{
    boolean VERBOSE = false;

    GraphicsDevice gd;
    int screen_max_x, screen_max_y;
    double IMAGE_SCALE;

    public ImageProcessor()
    {
        this.initialize();
    }

    private void initialize()
    {
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screen_max_x = gd.getDisplayMode().getWidth();
        screen_max_y = gd.getDisplayMode().getHeight();

        IMAGE_SCALE = 1;
    }

    /**
     * Down scale by downScaleRatio average colors in a sub-square
     * 1:ScreenSize/1
     * 2:ScreenSize/4
     * 3:ScreenSize/9
     * @param colorArray
     * @return
     */
    public Color[][] downScale(Color[][] colorArray, int downScaleRatio)
    {
        int pixelsHeight = colorArray[0].length;
        int pixelsWidth = colorArray.length;

        IMAGE_SCALE = IMAGE_SCALE/downScaleRatio;
        int modifier = downScaleRatio;

        int scaledHeight = (int)pixelsHeight/modifier;
        int scaledWidth = (int)pixelsWidth/modifier;
        print("Downscaling IMG to: " + scaledWidth + ", " +scaledHeight);

        Color[][] result = new Color[scaledWidth][scaledHeight];
        //Average every downScaleRatio pixels and insert to result

        int red, green, blue, y;
        int x = 0;
        int pixelsProcessed = 0;

        for(int i=0; (i+modifier)<=colorArray.length; i+=modifier)
        {
            y = 0;

            for(int j=0; (j+modifier)<=colorArray[i].length; j+=modifier)
            {
                red = 0;
                green = 0;
                blue = 0;

                for(int k=0; k<modifier; k++)
                {
                    for(int l=0; l<modifier; l++)
                    {
                        if (i+k < colorArray.length && j+l < colorArray[i+k].length)
                        {
                            Color colorPointer = colorArray[i+k][j+l];
                            red += colorPointer.getRed();
                            green += colorPointer.getGreen();
                            blue += colorPointer.getBlue();

                            pixelsProcessed++;
                        }
                    }

                }

                int averageRed = (int)(red/Math.pow(modifier,2));
                int averageGreen = (int)(green/Math.pow(modifier,2));
                int averageBlue = (int)(blue/Math.pow(modifier,2));

                Color averagedColor = new Color(averageRed, averageGreen, averageBlue);
                result[x][y] = averagedColor;

                y++;
            }
            x++;
        }

        print(pixelsProcessed+" colors processed.");
        return result;
    }

    public Color[][] upScale(Color[][] colorArray, int upScaleRatio)
    {
        int pixelsHeight = colorArray[0].length;
        int pixelsWidth = colorArray.length;
        IMAGE_SCALE = IMAGE_SCALE*upScaleRatio;
        int modifier = upScaleRatio;

        int scaledHeight = (int)pixelsHeight*modifier;
        int scaledWidth = (int)pixelsWidth*modifier;
        print("Upscaling IMG to: " + scaledWidth + ", " +scaledHeight);

        Color[][] result = new Color[scaledWidth][scaledHeight];
        //Average every downScaleRatio pixels and insert to result

        int pixelsProcessed = 0;

        for(int i=0; i<colorArray.length; i++)
        {
            for(int j=0; j<colorArray[i].length; j++)
            {
                for(int k=0; k<modifier; k++)
                {
                    for(int l=0; l<modifier; l++)
                    {
                        Color colorPointer = colorArray[i][j];
                        result[(i*modifier)+k][(j*modifier)+l] = colorPointer;
                    }

                }
                pixelsProcessed++;
            }
        }

        print(pixelsProcessed+" colors processed.");
        return result;
    }

    public void pixelCheck(Color[][] colorArray)
    {
        boolean err = false;

        for(int i=0; i<colorArray.length; i++)
        {
            for(int j=0; j<colorArray[i].length; j++)
            {
                Color colorPointer = colorArray[i][j];
                if ( colorPointer == null)
                {
                    System.err.println("Null color pointer found at: " + i + ", " + j);
                    err = true;
                }
            }
        }

        if (!err)
            System.out.println("Pixel Check OK.");
    }

    public Color[][] filterRGBColors(Color[][] colorArray)
    {
        for(int i=0; i<colorArray.length; i++)
        {
            for(int j=0; j<colorArray[i].length; j++)
            {
                Color curr = colorArray[i][j];
                if(curr.getRed() == curr.getGreen() && curr.getRed() == curr.getBlue() && curr.getGreen() == curr.getBlue())
                {
                    colorArray[i][j] = new Color(0,0,0);
                }else
                {
                    colorArray[i][j] = new Color(255,255,255);
                }
            }
        }

        return colorArray;
    }

    public void print(String s)
    {
        if(VERBOSE)
            System.out.println(s);
    }

//  ##################
//  ## STATIC CALLS ##
//  ##################

    public static BufferedImage getImageFromArray(Color[][] colorArray)
    {
        BufferedImage bufferedImage = new BufferedImage(colorArray.length, colorArray[0].length, BufferedImage.TYPE_INT_RGB);

        for(int i =0; i<colorArray.length; i++)
        {
            for(int j =0; j<colorArray[i].length; j++)
            {
                Color curr = colorArray[i][j];

                int b = curr.getBlue();
                int g = curr.getGreen()<<8;
                int r = curr.getRed()<<16;
                int rgb = r + g + b;
                bufferedImage.setRGB(i, j, rgb);
            }
        }

        return bufferedImage;
    }

    public static Color[][] loadBufferedImage(BufferedImage bmp)
    {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Color[][] colorArray = new Color[width][height];
        for(int h=0; h < height; h++)
        {
            for(int w=0; w < width; w++)
            {
                int color_l = bmp.getRGB(w,h);
                int b = color_l & 0xFF;
                int g = (color_l >> 8) & 0xFF;
                int r = (color_l >> 16) & 0xFF;
                colorArray[w][h] = new Color(r,g,b);
            }
        }

        return colorArray;
    }

    public static String colorArrayToString(Color[][] colorArray)
    {
        String out = "[";

        for(int i=0; i<colorArray.length; i++)
        {
            if(i!=0)
            {
                out+= '\n';
            }

            for(int j=0; j<colorArray[i].length; j++)
            {
                Color curr = colorArray[i][j];
                out+="{" + curr.getRed() + ", " + curr.getGreen() + ", " + curr.getBlue() + "}, ";
            }
        }

        out += "]";

        return out;
    }

    public static void printColorArray(Color[][] colorArray)
    {
        System.out.println(colorArrayToString(colorArray));
    }
}

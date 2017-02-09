package AgarIO;

import Utilities.ImageProcessor;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 8/25/2016.
 */
public class AgarIOController {

    Robot bot;
    int screen_max_x, screen_max_y;

    public AgarIOController()
    {

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screen_max_x = gd.getDisplayMode().getWidth();
        screen_max_y = gd.getDisplayMode().getHeight();

        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public Point getCurrentMousePos()
    {
       return MouseInfo.getPointerInfo().getLocation();
    }

    public void moveMouse(int x, int y)
    {
        bot.mouseMove(x,y);
    }

    public void moveMouseRandom()
    {
        moveMouse( (int)(Math.random()*800)+1, (int)(Math.random()*800)+1 );
    }

    public void keyPressOnDelay(int keyEvent, int delay)
    {
        bot.delay(delay);
        bot.keyPress(keyEvent);
        bot.keyRelease(keyEvent);
    }
}

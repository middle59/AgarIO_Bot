package AgarIO;

import Utilities.ImageProcessor;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 8/25/2016.
 */
public class Controller {

    Robot bot;
    int screen_max_x, screen_max_y;

    public Controller()
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

    public void moveMouse(int x, int y)
    {
        bot.mouseMove(x,y);
    }

    public void play()
    {
        enterUserInfo();
        gatherMyColorData();
    }

    public void enterUserInfo()
    {
        //Username Focus
        bot.mouseMove(812,364);
        bot.mousePress(InputEvent.BUTTON1_MASK);

        //Clear Any Data Inside
        clearAllText();

        bot.keyPress(KeyEvent.VK_SHIFT);
        keyPressOnDelay(KeyEvent.VK_A, 250);
        bot.keyRelease(KeyEvent.VK_SHIFT);
        keyPressOnDelay(KeyEvent.VK_B, 250);
        keyPressOnDelay(KeyEvent.VK_I, 250);

        bot.delay(250);
        keyPressOnDelay(KeyEvent.VK_TAB, 500);
        keyPressOnDelay(KeyEvent.VK_TAB, 500);
        keyPressOnDelay(KeyEvent.VK_TAB, 500);
        keyPressOnDelay(KeyEvent.VK_ENTER, 500);

    }

    public void gatherMyColorData()
    {
        Point blobStarting = new Point(917, 519);

        Rectangle area = new Rectangle(blobStarting.x, blobStarting.y, 80, 80);
        BufferedImage screenCap = bot.createScreenCapture(area);
        Color[][] colorArray = ImageProcessor.loadBufferedImage(screenCap);
        ImageProcessor.printColorArray(colorArray);
    }


    public void clearAllText()
    {
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_A);
        bot.keyRelease(KeyEvent.VK_CONTROL);
        bot.keyRelease(KeyEvent.VK_A);

        keyPressOnDelay(KeyEvent.VK_DELETE, 100);
    }


    public void keyPressOnDelay(int keyEvent, int delay)
    {
        bot.delay(delay);
        bot.keyPress(keyEvent);
        bot.keyRelease(keyEvent);
    }
}

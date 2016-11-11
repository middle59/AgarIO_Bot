package Utilities;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 9/27/2016.
 */
public class ScreenCap {

    int screen_max_x, screen_max_y;
    GraphicsDevice gd;
    Robot robot;

    private static ScreenCap instance = null;
    protected ScreenCap()
    {
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screen_max_x = gd.getDisplayMode().getWidth();
        screen_max_y = gd.getDisplayMode().getHeight();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static ScreenCap getInstance() {
        if(instance == null) {
            instance = new ScreenCap();
        }
        return instance;
    }

    public static int getScreenMaxX() { return ScreenCap.getInstance().screen_max_x; }
    public static int getScreenMaxY() { return ScreenCap.getInstance().screen_max_y; }
    public static Robot getScreenRobot() { return ScreenCap.getInstance().robot; }

    public static BufferedImage getFullScreenCap()
    {
        return getAreaScreenCap(0, 0, ScreenCap.getScreenMaxX(), ScreenCap.getScreenMaxY());
    }

    public static BufferedImage getAreaScreenCap(int x, int y, int width, int height)
    {
        Rectangle area = new Rectangle(x, y, width, height);
        BufferedImage screenCap = ScreenCap.getScreenRobot().createScreenCapture(area);

        return screenCap;
    }

    public static Color getPointRGB(Point point)
    {
        Rectangle area = new Rectangle(point.x, point.y, 1, 1);
        BufferedImage screenCap = ScreenCap.getScreenRobot().createScreenCapture(area);
        Color[][] colors = ImageProcessor.loadBufferedImage(screenCap);
        return colors[0][0];
    }
}

package AgarIO.Grid;

import Utilities.ScreenCap;

/**
 * Created by Mike on 11/6/2016.
 */
public class ScreenConfiguration {

    public int x,y,width,height;

    public ScreenConfiguration(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static ScreenConfiguration getPhotoViewerConfig()
    {
        return new ScreenConfiguration(128, 92, 1662, 838);
    }

    public static ScreenConfiguration getAgarioWebConfig()
    {
        return new ScreenConfiguration(0, 70, ScreenCap.getScreenMaxX(), ScreenCap.getScreenMaxY() - 110);
    }
}

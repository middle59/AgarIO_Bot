package AgarIO.Grid;

import Utilities.ScreenCap;

/**
 * Created by Mike on 11/6/2016.
 */
public class ScreenConfiguration {

    //x and y are offset values to the AgarIO window
    public int x,y,width,height;
    private String configurationName;

    public ScreenConfiguration(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setConfigurationName(String configurationName) { this.configurationName = configurationName; }
    public String getConfigurationName() { return configurationName; }

    public static ScreenConfiguration getPhotoViewerConfig()
    {
        ScreenConfiguration screenConfiguration = new ScreenConfiguration(128, 92, 1662, 803);
        screenConfiguration.setConfigurationName("Photo Viewer Config");
        return screenConfiguration;
    }

    public static ScreenConfiguration getAgarioWebConfig()
    {
        ScreenConfiguration screenConfiguration = new ScreenConfiguration(0, 70, ScreenCap.getScreenMaxX(), ScreenCap.getScreenMaxY() - 180);
        screenConfiguration.setConfigurationName("Web Config");
        return screenConfiguration;
    }
}

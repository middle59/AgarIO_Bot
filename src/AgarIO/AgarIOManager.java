package AgarIO;

import AgarIO.Grid.ScreenConfiguration;
import AgarIO.JavaFX.MainView;
import UI.DisplayUI;
import Utilities.ImageProcessor;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 9/27/2016.
 */
public class AgarIOManager
{
    public static boolean ENABLE_DISPLAY;
    public static List<ScreenConfiguration> configurationList;

    private static DisplayUI displayUI;
    private static AgarIOController agarIOController;
    private static MainView mainView;
    private static SnapshotDecisionAid agarIODecisionAid;
    ImageProcessor imageProcessor;

    public static boolean active;
    public static ScreenConfiguration screenConfiguration;
    public static double scale = 0.125; //move to screenConfiguration

    public AgarIOManager() {
        active = false;

        //Config for either parsing from windows photo viewer or from agar.io
        configurationList = new ArrayList<>();
        configurationList.add(ScreenConfiguration.getAgarioWebConfig());
        configurationList.add(ScreenConfiguration.getPhotoViewerConfig());
        screenConfiguration = configurationList.get(1);

        agarIOController = AgarIOManager.getAgarIOController();
        imageProcessor = new ImageProcessor();

        AgarIOManager.ENABLE_DISPLAY = false;

        showDisplayCheck();
    }

    public static DisplayUI getDisplayUI() {
        if (displayUI == null) {
            displayUI = new DisplayUI();
        }
        return displayUI;
    }

    public static AgarIOController getAgarIOController() {
        if (agarIOController == null) {
            agarIOController = new AgarIOController();
        }
        return agarIOController;
    }

    public static SnapshotDecisionAid getSnapshotDecisionAid() {
        if (agarIODecisionAid == null) {
            agarIODecisionAid = new SnapshotDecisionAid();
        }
        return agarIODecisionAid;
    }

    public static void showDisplayCheck()
    {
        if(ENABLE_DISPLAY)
        {
            if(displayUI == null) {
                displayUI = AgarIOManager.getDisplayUI();
                displayUI.setLocation(-305, 375);
            }
        }else
        {
            if(displayUI != null)
            {
                displayUI.dispose();
                displayUI = null;
            }
        }
    }

    public static void setConfigurationByName(String name)
    {
        for(ScreenConfiguration sConfig : AgarIOManager.configurationList) {
            if(sConfig.getConfigurationName().equals(name))
            {
                AgarIOManager.screenConfiguration = sConfig;
            }
        }
    }

    public void run()
    {
        try {
            AgarIOProcessor.runImageHandling();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        AgarIOManager agarIOManager = new AgarIOManager();
        agarIOManager.run();
        Application.launch(MainView.class);
    }
}

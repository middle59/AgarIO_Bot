package AgarIO;

import AgarIO.Grid.Coordinate;
import AgarIO.Grid.ScreenConfiguration;
import UI.DisplayUI;
import UI.Logger;
import Utilities.ImageProcessor;
import Utilities.ScreenCap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 9/27/2016.
 */
public class AgarIOManager implements ActionListener {

    public JFrame frame;
    public JButton start;
    public boolean active;
    public JButton stop;
    public JLabel statusLabel;
    public JLabel statusStringLabel;
    public static ScreenConfiguration screenConfiguration;
    public static double scale = 1; //move to screenConfiguration

    public AgarIOManager() {
        frame = new JFrame("Agar IO Bot Manager");
        active = false;
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        start = new JButton("Start");
        start.addActionListener(this);
        stop = new JButton("Stop");
        stop.addActionListener(this);
        stop.setEnabled(false);
        statusLabel = new JLabel("Status: ");
        statusStringLabel = new JLabel("Inactive");

        GridLayout experimentLayout = new GridLayout(2,4);
        frame.setLayout(experimentLayout);
        //Row 1
        frame.add(new JLabel(""));
        frame.add(statusLabel);
        frame.add(statusStringLabel);
        frame.add(new JLabel(""));

        //Row 2
        frame.add(new JLabel(""));
        frame.add(start);
        frame.add(stop);
        frame.add(new JLabel(""));


        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    public void setActive(boolean enable)
    {
        if(enable)
        {
            active = true;
            start.setEnabled(false);
            stop.setEnabled(true);
        }else
        {
            active = false;
            start.setEnabled(true);
            stop.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Start"))
        {
            setActive(true);
        }else if (e.getActionCommand().equals("Stop"))
        {
            setActive(false);
        }
    }

    public static void main(String[] args)
    {
        //Config for either parsing from windows photo viewer or from agar.io
        //screenConfiguration = ScreenConfiguration.getAgarioWebConfig();
       screenConfiguration = ScreenConfiguration.getPhotoViewerConfig();

        AgarIOManager agarIOManager = new AgarIOManager();
        agarIOManager.frame.setLocation(-1000, 500);
        //DisplayUI displayUI = new DisplayUI();
        //displayUI.setLocation(-1000,300);
        //Logger.displayUI();
        //Logger.getFrame().setLocation(-1500, 300);
        Controller controller = new Controller(); //JavaFx
        ImageProcessor imageProcessor = new ImageProcessor();

        while(true) {
            if(agarIOManager.active) {
                //the offsets are to cut off the top of the browser and the OS taskbar
                BufferedImage screenCap = ScreenCap.getAreaScreenCap(screenConfiguration.x, screenConfiguration.y, screenConfiguration.width, screenConfiguration.height);
                //BufferedImage scaledImage = imageProcessor.scale(screenCap, scale);
                Color[][] colorArray = ImageProcessor.loadBufferedImage(screenCap);
                //Color[][] colorArray = ImageProcessor.loadBufferedImage(scaledImage);

                Color[][] downScale = imageProcessor.downScale(colorArray, ((int)(1/scale)) );
                Color[][] colorMod = imageProcessor.filterRGBColors(downScale);

                AgarIODataSnapshot agarIODataSnapshot = SnapshotFactory.analyzeImage(colorMod);
                //System.out.println("Enemies Found: " + agarIODataSnapshot.enemyList.size());
                Coordinate decision = SnapshotDecisionAid.makeDecision(agarIODataSnapshot);
                //colorMod[decision.getX()][decision.getY()] = new Color(255,0,0);

                decision.setX((int)(decision.getX()/scale)+screenConfiguration.x);
                decision.setY((int)(decision.getY()/scale)+screenConfiguration.y);
                System.out.println("Moving to: "+decision);
                //controller.moveMouse(decision.getX(), decision.getY());

                //displayUI.setPicturelabel(ImageProcessor.getImageFromArray(colorMod));
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

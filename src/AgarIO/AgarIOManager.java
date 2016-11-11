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

    AgarIOManager() {
        frame = new JFrame("Manager");
        active = false;
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        start = new JButton("Start");
        start.addActionListener(this);
        stop = new JButton("Stop");
        stop.addActionListener(this);

        FlowLayout experimentLayout = new FlowLayout();
        frame.setLayout(experimentLayout);
        frame.add(start);
        frame.add(stop);

        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Start"))
        {
            active = true;
        }else if (e.getActionCommand().equals("Stop"))
        {
            active = false;
        }
    }

    public static void main(String[] args)
    {
        //Config for either parsing from windows photo viewer or from agar.io
        ScreenConfiguration screenConfiguration = ScreenConfiguration.getAgarioWebConfig();
        //ScreenConfiguration screenConfiguration = ScreenConfiguration.getPhotoViewerConfig();

        AgarIOManager agarIOManager = new AgarIOManager();
        agarIOManager.frame.setLocation(-1000, 500);
        DisplayUI displayUI = new DisplayUI();
        displayUI.setLocation(-1000,300);
        //Logger.displayUI();
        //Logger.getFrame().setLocation(-1500, 300);
        Controller controller = new Controller();

        while(true) {
            if(agarIOManager.active) {
                //the offsets are to cut off the top of the browser and the OS taskbar
                BufferedImage screenCap = ScreenCap.getAreaScreenCap(screenConfiguration.x, screenConfiguration.y, screenConfiguration.width, screenConfiguration.height);
                Color[][] colorArray = ImageProcessor.loadBufferedImage(screenCap);
                ImageProcessor imageProcessor = new ImageProcessor();
                Color[][] downScale = imageProcessor.downScale(colorArray, 8);
                Color[][] colorMod = imageProcessor.filterRGBColors(downScale);

                AgarIODataSnapshot agarIODataSnapshot = SnapshotFactory.analyzeImage(colorMod);
                //System.out.println("Enemies Found: " + agarIODataSnapshot.enemyList.size());
                Coordinate decision = SnapshotDecisionAid.makeDecision(agarIODataSnapshot);
                //colorMod[decision.getX()][decision.getY()] = new Color(255,0,0);

                decision.setX(decision.getX()*8+screenConfiguration.x);
                decision.setY(decision.getY()*8+screenConfiguration.y);
                //System.out.println("Moving to: "+decision);
                controller.moveMouse(decision.getX(), decision.getY());

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

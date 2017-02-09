package AgarIO;

import AgarIO.Grid.ScreenConfiguration;
import UI.DisplayUI;
import Utilities.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Mike on 9/27/2016.
 */
public class AgarIOManager implements ActionListener, KeyListener
{
    public static boolean ENABLE_DISPLAY;

    public JFrame frame;
    public JButton start;
    public JButton stop;
    public JLabel statusLabel;
    public JLabel statusStringLabel;

    private static DisplayUI displayUI;
    private static AgarIOController agarIOController;
    ImageProcessor imageProcessor;

    public static boolean active;
    public static ScreenConfiguration screenConfiguration;
    public static double scale = 0.125; //move to screenConfiguration

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
        setActive(false);

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

        frame.addKeyListener(this);

        frame.setSize(300, 100);
        frame.setVisible(true);

        frame.setFocusable(true);

        frame.setLocation(-305, 254);

        //Config for either parsing from windows photo viewer or from agar.io
        //screenConfiguration = ScreenConfiguration.getAgarioWebConfig();
        screenConfiguration = ScreenConfiguration.getPhotoViewerConfig();
        agarIOController = AgarIOManager.getAgarIOController();
        imageProcessor = new ImageProcessor();

        AgarIOManager.ENABLE_DISPLAY = true;

        if(ENABLE_DISPLAY) {
            displayUI = AgarIOManager.getDisplayUI();
            displayUI.setLocation(-305, 375);
        }
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

    public void setActive(boolean enable)
    {
        if(enable)
        {
            statusStringLabel.setText("Active");
            statusStringLabel.setForeground(Color.GREEN);
            statusStringLabel.repaint();
            active = true;
            start.setEnabled(false);
            stop.setEnabled(true);
        }else
        {
            statusStringLabel.setText("Inactive");
            statusStringLabel.setForeground(Color.RED);
            statusStringLabel.repaint();
            active = false;
            start.setEnabled(true);
            stop.setEnabled(false);
            frame.repaint();
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

        //Logger.displayUI();
        //Logger.getFrame().setLocation(-1500, 300);
        /*
        while(true) {
            if(this.active) {
                //the offsets are to cut off the top of the browser and the OS taskbar

                RunTimer.startTimer();
                BufferedImage screenCap = ScreenCap.getAreaScreenCap(screenConfiguration.x, screenConfiguration.y, screenConfiguration.width, screenConfiguration.height);
                System.out.println("Cap Image: " + RunTimer.endTimer() + "ms");

                RunTimer.startTimer();
                Color[][] colorArray = ImageProcessor.loadBufferedImage(screenCap);
                System.out.println("Load Image: " + RunTimer.endTimer() + "ms");

                //Color modding before downscaling retains a much better image
                RunTimer.startTimer();
                Color[][] colorMod = ImageProcessor.filterRGBColors(colorArray);
                System.out.println("ColorMod: " + RunTimer.endTimer() + "ms");

                RunTimer.startTimer();
                Color[][] downScale = imageProcessor.downScale(colorMod, ((int) (1 / scale)));
                System.out.println("Downscale: " + RunTimer.endTimer() + "ms");

                //RunTimer.startTimer();
                AgarIODataSnapshot agarIODataSnapshot = SnapshotFactory.analyzeImage(downScale);
                //System.out.println("Analyze: " + RunTimer.endTimer() + "ms");
                //System.out.println("Enemies Found: " + agarIODataSnapshot.enemyList.size());

                //RunTimer.startTimer();
                Coordinate decision = SnapshotDecisionAid.makeDecision(agarIODataSnapshot);
                //System.out.println("Decision: " + RunTimer.endTimer() + "ms\n");

                //Draw the result if we need to
                if(AgarIOManager.ENABLE_DISPLAY)
                {
                    downScale = imageProcessor.drawCircle(downScale, new Point(decision.getX(), decision.getY()), (int) (24 * scale), decision.getThreat().getThreatColor());
                    displayUI.setPicturelabel(ImageProcessor.getImageFromArray(downScale));
                }

                decision.setX((int) (decision.getX() / scale) + screenConfiguration.x);
                decision.setY((int) (decision.getY() / scale) + screenConfiguration.y);

                //System.out.println("Moving to: "+decision);
                //Point currentPoint = agarIOController.getCurrentMousePos();
                //System.out.println("Current Position: "+ currentPoint.getX()+","+currentPoint.getY());
                agarIOController.moveMouse(decision.getX(), decision.getY());
                //System.out.println("Calculation time: " + RunTimer.endTimer() + "ms");

            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            if(active)
            {
                setActive(false);
            }else
            {
                setActive(true);
            }
        }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(1);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args)
    {
        AgarIOManager agarIOManager = new AgarIOManager();
        agarIOManager.run();
    }
}

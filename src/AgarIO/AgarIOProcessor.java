package AgarIO;

import AgarIO.Grid.Coordinate;
import Utilities.ImageProcessor;
import Utilities.ScreenCap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * Created by Mike on 2/6/2017.
 */
public class AgarIOProcessor {

    public static void runImageHandling() throws InterruptedException {

        final PCWorker pcWorker = new PCWorker();

        Thread loadAndProduce = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pcWorker.produce();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        Thread filterAndConsume = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pcWorker.consume();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        loadAndProduce.start();
        filterAndConsume.start();

        //loadAndProduce.join();
        //filterAndConsume.join();
    }

    public static class PCWorker
    {
        Stack<Color[][]> imageStack = new Stack<>();
        int stackLimit = 1;

        public void produce() throws InterruptedException
        {
            while (true)
            {
                if(AgarIOManager.active)
                {
                    synchronized (this)
                    {
                        while (imageStack.size() >= stackLimit) {
                            this.wait();
                        }
                        //RunTimer.startTimer();
                        BufferedImage screenCap = ScreenCap.getAreaScreenCap(AgarIOManager.screenConfiguration.x, AgarIOManager.screenConfiguration.y, AgarIOManager.screenConfiguration.width, AgarIOManager.screenConfiguration.height);
                        //System.out.println("Cap Image: " + RunTimer.endTimer() + "ms");
                        //RunTimer.startTimer();
                        Color[][] colorArray = ImageProcessor.loadBufferedImage(screenCap);
                        //System.out.println("Load Image: " + RunTimer.endTimer() + "ms");
                        imageStack.push(colorArray);
                        //Notify the consumer to begin processing the top image, the stack may be flushed at that point
                        this.notifyAll();
                        // makes the working of program easier
                    }
                }else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void consume() throws InterruptedException
        {
            while (true)
            {
                if(AgarIOManager.active)
                {
                    synchronized (this)
                    {
                        while (imageStack.empty()) { // Wait for something to work on..
                                this.wait();
                        }
                        //RunTimer.startTimer();
                        Color[][] colorArray = imageStack.pop();
                        Color[][] colorMod = ImageProcessor.filterRGBColors(colorArray);
                        //System.out.println("ColorMod: " + RunTimer.endTimer() + "ms");

                        //RunTimer.startTimer();
                        Color[][] downScale = ImageProcessor.downScale(colorMod, ((int) (1 / AgarIOManager.scale)));
                        //System.out.println("Downscale: " + RunTimer.endTimer() + "ms\n");

                        //RunTimer.startTimer();
                        AgarIODataSnapshot agarIODataSnapshot = SnapshotFactory.analyzeImage(downScale);
                        //System.out.println("Analyze: " + RunTimer.endTimer() + "ms");
                        //System.out.println("Enemies Found: " + agarIODataSnapshot.enemyList.size());

                        //RunTimer.startTimer();
                        Coordinate decision = SnapshotDecisionAid.makeDecision(agarIODataSnapshot);
                        //System.out.println("Decision: " + RunTimer.endTimer() + "ms\n");

                        //Draw the result if we need to
                        if (AgarIOManager.ENABLE_DISPLAY) {
                            downScale = ImageProcessor.drawCircle(downScale, new Point(decision.getX(), decision.getY()), (int) (24 * AgarIOManager.scale), decision.getThreat().getThreatColor());
                            AgarIOManager.getDisplayUI().setPicturelabel(ImageProcessor.getImageFromArray(downScale));
                        }

                        decision.setX((int) (decision.getX() / AgarIOManager.scale) + AgarIOManager.screenConfiguration.x);
                        decision.setY((int) (decision.getY() / AgarIOManager.scale) + AgarIOManager.screenConfiguration.y);

                        //System.out.println("Moving to: "+decision);
                        //Point currentPoint = controller.getCurrentMousePos();
                        //System.out.println("Current Position: "+ currentPoint.getX()+","+currentPoint.getY());
                        AgarIOManager.getAgarIOController().moveMouse(decision.getX(), decision.getY());
                        //System.out.println("Calculation time: " + RunTimer.endTimer() + "ms");

                        this.notifyAll();

                        // and sleep
                        //Thread.sleep(1000);
                    } // END SYNCHRONIZE
                }else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

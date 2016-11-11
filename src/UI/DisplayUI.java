package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Mike on 9/27/2016.
 */
public class DisplayUI extends JFrame implements Runnable {

    boolean RUN_INSTANCE = true;
    int REFRESH_RATE = 100; //in MS

    Thread thread;

    JLabel MAIN_IMAGE_DISPLAY;

    // constructor
    public DisplayUI()
    {
        super("FPS: ");

        setSize( 150, 100 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setLayout( new FlowLayout() );
        MAIN_IMAGE_DISPLAY = new JLabel("");

        add(MAIN_IMAGE_DISPLAY);
        this.setTitle("FPS: " + 1000/REFRESH_RATE);

        this.setVisible(true);

        thread = new Thread(this);
        thread.start();
    }

    public Thread getThread() { return thread; }

    public void setPicturelabel(Image image) {
        MAIN_IMAGE_DISPLAY.setIcon(new ImageIcon(image));
    }

    @Override
    public void run() {
        int prevDisplay = 0;
        Icon mainImg;

        while (RUN_INSTANCE) {

            mainImg = MAIN_IMAGE_DISPLAY.getIcon();

            if(mainImg != null) {
                //Check if the main display needs to be updated
                if (mainImg.hashCode() != prevDisplay) {
                    MAIN_IMAGE_DISPLAY.repaint();
                    MAIN_IMAGE_DISPLAY.setVisible(true);
                    this.setSize( mainImg.getIconWidth() + 50, mainImg.getIconHeight() + 50);
                    this.repaint();

                    prevDisplay = mainImg.hashCode();
                }
            }

            try {
                Thread.sleep(REFRESH_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

package UI;

import sun.rmi.runtime.Log;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mike on 9/30/2016.
 */
public class Logger implements Runnable {

    static boolean LOGGER_RUNNING = false;
    static boolean RECORD_TIMESTAMP = true;

    static JFrame LOGGER_DISPLAY;
    static JTextArea LOGGER_TEXT;
    static int LOGGER_WIDTH = 500;
    static int LOGGER_HEIGHT = 600;

    public static void displayUI(){

        JFrame display = new JFrame("Logger");

        display.setSize( Logger.LOGGER_WIDTH,  Logger.LOGGER_HEIGHT);
        display.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        Logger.LOGGER_TEXT = new JTextArea();
        Logger.LOGGER_TEXT.setEditable(false);
        Logger.LOGGER_TEXT.setLineWrap(true);
        Border border = BorderFactory.createLineBorder(Color.white);
        Border margin = new EmptyBorder(10,10,10,10);
        Logger.LOGGER_TEXT.setBorder(new CompoundBorder(border, margin));
        Logger.LOGGER_TEXT.setSize(LOGGER_WIDTH - 100, LOGGER_HEIGHT - 100);

        display.add(Logger.LOGGER_TEXT);
        display.setTitle("Logger");

        display.setVisible(true);

        Logger.LOGGER_DISPLAY = display;
        Logger.LOGGER_RUNNING = true;

        Logger.log("Logging started...");
    }

    public static JFrame getFrame(){ return LOGGER_DISPLAY; }

    public static void log(String s)
    {
        if(Logger.LOGGER_RUNNING)
        {
            String log = "";

            if(RECORD_TIMESTAMP) {
                log += "[" + Logger.getTimeStamp() + "]: ";
            }
            log += s + '\n' + Logger.LOGGER_TEXT.getText();

            Logger.LOGGER_TEXT.setText(log);
        }
    }

    public static String getTimeStamp()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    @Override
    public void run() {}

    public static void main(String[] args)
    {
       Logger.displayUI();
    }
}


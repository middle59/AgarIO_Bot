package TestDrivers;

import AgarIO.AgarIOController;
import Utilities.RunTimer;

import java.awt.*;

/**
 * Created by Mike on 1/29/2017.
 */
public class MouseReposSpeed {

    public static void main(String[] args)
    {
        RunTimer.startTimer();
        int count = 100;
        for(int i=0; i < count; i++)
        {
            AgarIOController agarIOController = new AgarIOController();
            agarIOController.moveMouseRandom();
            Point currentPoint = agarIOController.getCurrentMousePos();
            System.out.println("Current Position: "+ currentPoint.getX()+","+currentPoint.getY());
        }
        long timeDuration = RunTimer.endTimer();
        System.out.println("Calculation time: " + timeDuration + "ms");
        //System.out.println("Moves Per Second: " + (count/(Math.ceil(timeDuration/1000))) + "ms");
        /*RunTimer.startTimer();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Calculation time: " + RunTimer.endTimer() + "ms");*/
    }
}

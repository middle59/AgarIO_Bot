package Utilities;

/**
 * Created by Mike on 9/30/2016.
 */
public class RunTimer {

    protected static long TIME;
    protected static boolean TIMER_RUNNING = false;

    protected RunTimer(){}

    public static long getTime() { return RunTimer.TIME; }
    public static boolean isTimerRunning() { return RunTimer.TIMER_RUNNING; }

    public static void startTimer()
    {
        if (!RunTimer.TIMER_RUNNING) {
            TIME = System.currentTimeMillis();
            RunTimer.TIMER_RUNNING = true;
        }
        else
        {
            System.err.println("Cannot start a new timer. RunTimer is already running.");
        }
    }

    public static long endTimer()
    {
        if(RunTimer.TIMER_RUNNING)
        {
            RunTimer.TIMER_RUNNING = false;
            return System.currentTimeMillis() - RunTimer.getTime();
        }
        else
        {
            System.err.println("RunTimer was not running.");
            return 0;
        }
    }
}

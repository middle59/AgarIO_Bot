package Utilities;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Mike on 1/4/2017.
 */
public class MathUtils {

    /*
     * Uses the Pythagorean Theorem to calculate the distance between two points
     */
    public static double getPointDistance(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /*
     * Calculates the angle between two points according to the Y-Axis moving Clockwise
     *          0
     *      270   90
     *         180
     */
    public static double getPointAngle(int x1, int y1, int x2, int y2)
    {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        //Returns the angle in the 0-360 degree range
        return angle + Math.ceil( -angle / 360 ) * 360;
    }

    /**
     * Returns a point containing the x and y based on theta and C
     * @param angle
     * @param distance
     * @return
     */
    public static Point2D.Double getPointFromAngleDistance(double angle, double distance)
    {
        return new Point2D.Double(Math.sin(Math.toRadians(angle))*distance, Math.cos(Math.toRadians(angle))*distance);
    }
}

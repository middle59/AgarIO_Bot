package AgarIO;

import AgarIO.Grid.Coordinate;
import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;

/**
 * Uses a snapshot to make a decision for the Controller
 * Created by Mike on 10/13/2016.
 */
public class SnapshotDecisionAid {

    public SnapshotDecisionAid(){}

    public static Coordinate makeDecision(AgarIODataSnapshot agarIODataSnapshot)
    {
        return runAwayClosest(agarIODataSnapshot);
    }

    /**
     * Returns the point to the closest object which is smaller than the player
     * Completely ignores objects which are bigger than the player
     * Highest Mass: -
     * Time Alive: -
     * @param agarIODataSnapshot
     * @return
     */
    public static Coordinate closestSmallerObject(AgarIODataSnapshot agarIODataSnapshot)
    {
        BufferedImage rawImage = agarIODataSnapshot.rawImage;
        //center on player as default
        Coordinate coordinate = new Coordinate(rawImage.getWidth(), rawImage.getHeight());
        Coordinate playerCenter = null;
        try {
            playerCenter = agarIODataSnapshot.player.approximateCenter();
            coordinate.setX(playerCenter.getX());
            coordinate.setY(playerCenter.getY());
        }catch (NullPointerException e)
        {
            System.err.println("Player was not found. Exiting.");
            System.exit(0);
        }

        //go for closest object thats smaller than us
        int objCount = 0;
        double closestDistance = -1;
        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            objCount++;
            if(object.getPixelCount() < agarIODataSnapshot.player.getPixelCount())
            {
                Coordinate objectCenter = object.approximateCenter();
                int objectX = objectCenter.getX();
                int objectY = objectCenter.getY();
                //a^2 + b^2 = c^2
                double distance = Math.sqrt(Math.pow(playerCenter.getX() - objectX, 2) + Math.pow(playerCenter.getY() - objectY, 2));
                //System.out.println("Object " + objCount + " Distance:" + distance*8);

                if(distance < closestDistance || closestDistance == -1) {
                    closestDistance = distance;
                    coordinate.setX(objectCenter.getX());
                    coordinate.setY(objectCenter.getY());
                }
            }
        }
        //System.out.println("Closest Distance was: " +closestDistance*8);

        return coordinate;
    }

    /**
     * Just runs in the opposite direction of the closest enemy in bigger than the player
     * Highest Mass: 24
     * Time Alive: 1:34
     * @param agarIODataSnapshot
     * @return
     */
    public static Coordinate runAwayClosest(AgarIODataSnapshot agarIODataSnapshot)
    {
        BufferedImage rawImage = agarIODataSnapshot.rawImage;
        //center on player as default
        Coordinate coordinate = new Coordinate(rawImage.getWidth(), rawImage.getHeight());
        Coordinate playerCenter = null;
        try {
            playerCenter = agarIODataSnapshot.player.approximateCenter();
            coordinate.setX(playerCenter.getX());
            coordinate.setY(playerCenter.getY());
        }catch (NullPointerException e)
        {
            System.err.println("Player was not found. Exiting.");
            System.exit(0);
        }

        int objCount = 0;
        double closestDistance = -1;
        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            objCount++;
            if(object.getPixelCount() > agarIODataSnapshot.player.getPixelCount())
            {
                Coordinate objectCenter = object.approximateCenter();
                int objectX = objectCenter.getX();
                int objectY = objectCenter.getY();
                //a^2 + b^2 = c^2
                double distance = Math.sqrt(Math.pow(playerCenter.getX() - objectX, 2) + Math.pow(playerCenter.getY() - objectY, 2));
                //System.out.println("Object " + objCount + " Distance:" + distance*8);

                if(distance < closestDistance || closestDistance == -1) {
                    closestDistance = distance;
                    coordinate.setX(objectCenter.getX());
                    coordinate.setY(objectCenter.getY());
                }
            }
        }

        //Now we have the coord of the biggest enemy closest to us, mirror that coord to get the run away position

        //Mirror X
        int midX = (rawImage.getWidth()/2);
        int newX = Math.abs(midX - coordinate.getX());
        newX = (coordinate.getX() > midX) ? midX - newX : midX + newX;

        //Mirror Y
        int midY = (rawImage.getHeight()/2);
        int newY = Math.abs(midY - coordinate.getY());
        newY = (coordinate.getY() > midY) ? midY - newY : midY + newY;

        coordinate.setX(newX);
        coordinate.setY(newY);

        return coordinate;
    }

    /**
     * Makes a decision based on the closest object, if its bigger, run away, otherwise eat
     * Highest Mass: -
     * Time Alive: -
     * @param agarIODataSnapshot
     * @return
     */
    public static Coordinate decideFromClosest(AgarIODataSnapshot agarIODataSnapshot)
    {
        BufferedImage rawImage = agarIODataSnapshot.rawImage;
        //center on player as default
        Coordinate coordinate = new Coordinate(rawImage.getWidth(), rawImage.getHeight());
        Coordinate playerCenter = null;
        try {
            playerCenter = agarIODataSnapshot.player.approximateCenter();
            coordinate.setX(playerCenter.getX());
            coordinate.setY(playerCenter.getY());
        }catch (NullPointerException e)
        {
            System.err.println("Player was not found. Exiting.");
            System.exit(0);
        }

        int objCount = 0;
        double closestDistance = -1;
        boolean closestIsFood = false;

        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            objCount++;

            Coordinate objectCenter = object.approximateCenter();
            int objectX = objectCenter.getX();
            int objectY = objectCenter.getY();
            //a^2 + b^2 = c^2
            double distance = Math.sqrt(Math.pow(playerCenter.getX() - objectX, 2) + Math.pow(playerCenter.getY() - objectY, 2));
            //System.out.println("Object " + objCount + " Distance:" + distance*8);

            if(distance < closestDistance || closestDistance == -1) {
                closestIsFood = (object.getPixelCount() < agarIODataSnapshot.player.getPixelCount()) ? true : false;
                closestDistance = distance;
                coordinate.setX(objectCenter.getX());
                coordinate.setY(objectCenter.getY());
            }

        }

        if(!closestIsFood) {
            //Mirror X
            int midX = (rawImage.getWidth() / 2);
            int newX = Math.abs(midX - coordinate.getX());
            newX = (coordinate.getX() > midX) ? midX - newX : midX + newX;

            //Mirror Y
            int midY = (rawImage.getHeight() / 2);
            int newY = Math.abs(midY - coordinate.getY());
            newY = (coordinate.getY() > midY) ? midY - newY : midY + newY;

            coordinate.setX(newX);
            coordinate.setY(newY);
        }

        return coordinate;
    }
}

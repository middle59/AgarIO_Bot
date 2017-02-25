package AgarIO.AI;

import AgarIO.AgarIODataSnapshot;
import AgarIO.Grid.Coordinate;
import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;

/**
 * Created by Mike on 2/20/2017.
 */
public class DecideByClosest_AI extends AbstractAI{

    public DecideByClosest_AI()
    {
        AI_Name = "Decide By Closest Simple";
        description = "Combines the elements of Eat Closest Food and Run Away to make either decision based on the closest object.";
    }

    @Override
    public Coordinate makeDecision(AgarIODataSnapshot agarIODataSnapshot) {
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

            //Coordinate objectCenter = object.approximateCenter();
            //Its better to calculate based on the closest object pixel not the center
            Coordinate objectCenter = object.approximateClosest(playerCenter);
            int objectX = objectCenter.getX();
            int objectY = objectCenter.getY();
            //a^2 + b^2 = c^2
            double distance = Math.sqrt(Math.pow(playerCenter.getX() - objectX, 2) + Math.pow(playerCenter.getY() - objectY, 2));
            //System.out.println("Object " + objCount + " Distance:" + distance*8);

            if(distance < closestDistance || closestDistance == -1) {
                closestIsFood = (object.approximateArea() < agarIODataSnapshot.player.approximateArea()) ? true : false;
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

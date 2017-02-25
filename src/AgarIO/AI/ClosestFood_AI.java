package AgarIO.AI;

import AgarIO.AgarIODataSnapshot;
import AgarIO.Grid.Coordinate;
import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;

/**
 * Created by Mike on 2/20/2017.
 */
public class ClosestFood_AI extends AbstractAI{

    public ClosestFood_AI()
    {
        AI_Name = "Eat Closest Food";
        description = "Moves towards a point on the closest object which is smaller than the player. " +
                "This AI completely ignores objects which are bigger than the player.";
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

        //go for closest object thats smaller than us
        int objCount = 0;
        double closestDistance = -1;
        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            objCount++;
            if(object.approximateArea() < agarIODataSnapshot.player.approximateArea())
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

        //System.out.println("Closest Distance was: " +closestDistance*2);
        //System.out.println("Object Count: " +objCount);
        return coordinate;
    }
}

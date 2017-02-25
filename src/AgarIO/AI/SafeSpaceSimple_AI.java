package AgarIO.AI;

import AgarIO.AgarIODataSnapshot;
import AgarIO.Grid.Coordinate;
import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;

/**
 * Created by Mike on 2/20/2017.
 */
public class SafeSpaceSimple_AI extends AbstractAI{

    public SafeSpaceSimple_AI()
    {
        AI_Name = "Safe Space Simple";
        description = "Makes sure the player is a certain distance away from all enemies before deciding to eat.";
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

        //*NOTE* Safe distance is relative to the scaled image received
        int safeDistance = 40;
        double closestFood = -1;
        double closestEnemy = -1;
        boolean comparisonIsFood = false;
        boolean isSafe = true;
        Coordinate enemyCoord = new Coordinate(0,0);

        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            Coordinate objectCenter = object.approximateClosest(playerCenter);

            int objectX = objectCenter.getX();
            int objectY = objectCenter.getY();
            double distance = Math.sqrt(Math.pow(playerCenter.getX() - objectX, 2) + Math.pow(playerCenter.getY() - objectY, 2));
            comparisonIsFood = (object.approximateArea() < agarIODataSnapshot.player.approximateArea()) ? true : false;
            if (!comparisonIsFood)
            {
                //just kind of ignore enemies furthur than the safe distance
                if(distance < safeDistance) {
                    isSafe = false;
                    if (closestEnemy == -1 || distance < closestEnemy) {
                        closestEnemy = distance;
                        enemyCoord.setX(objectCenter.getX());
                        enemyCoord.setY(objectCenter.getY());
                    }
                }
            }else
            {
                if (closestFood == -1 || distance < closestFood) {
                    closestFood = distance;
                    coordinate.setX(objectCenter.getX());
                    coordinate.setY(objectCenter.getY());
                }
            }
        }

        if(!isSafe) {
            //Mirror X
            int midX = (rawImage.getWidth() / 2);
            int newX = Math.abs(midX - enemyCoord.getX());
            newX = (enemyCoord.getX() > midX) ? midX - newX : midX + newX;

            //Mirror Y
            int midY = (rawImage.getHeight() / 2);
            int newY = Math.abs(midY - enemyCoord.getY());
            newY = (enemyCoord.getY() > midY) ? midY - newY : midY + newY;

            coordinate.setX(newX);
            coordinate.setY(newY);
        }

        return coordinate;
    }
}

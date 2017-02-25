package AgarIO.AI;

import AgarIO.AgarIODataSnapshot;
import AgarIO.Grid.Coordinate;
import AgarIO.Grid.Threat;
import AgarIO.Objects.AbstractObject;
import Utilities.MathUtils;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Mike on 2/20/2017.
 */
public class SafeSpaceWeighted_AI extends AbstractAI{

    public SafeSpaceWeighted_AI()
    {
        AI_Name = "Safe Space Weighted";
        description = "Uses the elements in Safe Space Simple, but makes more intelligent decisions on running away. "+
            "The object now weights the distance of enemies in a certain range to decide what angle to run away.";
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

        //*NOTE* Safe distance is relative to the scale of the image in the snapshot received
        HashSet<AbstractObject> enemiesInSafe = new HashSet<>();
        //The front of the list is the highest priority food object
        //In this implementation it will be the closest 'safe' food object
        List<AbstractObject> foodPriorityList = new ArrayList<>();
        // Scale with image size ~ 25% of image width
        int safeDistance = (int) (rawImage.getWidth()*0.25);

        double totalEnemyDistanceInSafe = 0;
        double closestFood = -1;
        double closestEnemy = -1;
        boolean comparisonIsFood = false;
        Coordinate enemyCoord = new Coordinate(0,0);

        for (AbstractObject object : agarIODataSnapshot.enemyList)
        {
            Coordinate objectCenter = object.approximateCenter();
            Coordinate objectClosest = object.approximateClosest(playerCenter);

            int objectCenterX = objectCenter.getX();
            int objectCenterY = objectCenter.getY();

            int objectClosestX = objectClosest.getX();
            int objectClosestY = objectClosest.getY();

            //double distance = MathUtils.getPointDistance(playerCenter.getX(), playerCenter.getY(), objectX, objectY);
            double distance = Math.sqrt(Math.pow( playerCenter.getX() - objectClosestX, 2) + Math.pow(playerCenter.getY() - objectClosestY, 2));
            object.distanceFromPlayer = distance;
            //System.out.println("Object Distance: "+distance);
            //System.out.println("Object Center:"+ (object.approximateCenter().getX()+128)+","+(object.approximateCenter().getY()+92));
            //System.out.println("Object Center:"+ ((object.approximateCenter().getX()*4)+128)+","+((object.approximateCenter().getY()*4)+92));
            comparisonIsFood = (object.approximateArea() < agarIODataSnapshot.player.approximateArea()) ? true : false;
            if (!comparisonIsFood)
            {
                //just ignore enemies furthur than the safe distance
                if(distance < safeDistance) {
                    totalEnemyDistanceInSafe += distance;
                    //huge work around here.. normal y increases as you go north but going north of the player decreases the Y value..
                    //we swap the player Y and the object Y to resolve this
                    //System.out.println("Player: " + ((coordinate.getX() / AgarIOManager.scale)+AgarIOManager.screenConfiguration.x) + "," + ((coordinate.getY() / AgarIOManager.scale)+AgarIOManager.screenConfiguration.y));
                    //System.out.println("Pixel: " + ((objectCenterX / AgarIOManager.scale)+AgarIOManager.screenConfiguration.x) + "," + ((objectCenterY / AgarIOManager.scale)+AgarIOManager.screenConfiguration.y));
                    object.angleTowardsPlayer = MathUtils.getPointAngle(playerCenter.getX(), objectCenterY, objectCenterX, playerCenter.getY());
                    //System.out.println("Enemies Angle To Player: " + object.angleTowardsPlayer);
                    enemiesInSafe.add(object);

                    if (closestEnemy == -1 || distance < closestEnemy) {
                        closestEnemy = distance;
                        enemyCoord.setX(objectClosest.getX());
                        enemyCoord.setY(objectClosest.getY());
                    }
                }
            }else //Is Food
            {
                //check if the food is nearby an enemy -- using safe distance
                //this whole thing becomes n^2...
                boolean foodIsSafe = true;
                // System.out.println("Food Size: "+object.approximateArea());

                for (AbstractObject comparisonObject : agarIODataSnapshot.enemyList) {
                    if(comparisonObject != object)
                    {
                        Coordinate comparisonObjectCenter = comparisonObject.approximateClosest(playerCenter);

                        int comparisonObjectX = comparisonObjectCenter.getX();
                        int comparisonObjectY = comparisonObjectCenter.getY();

                        double comparisonDistance = Math.sqrt(Math.pow( objectClosestX - comparisonObjectX, 2) + Math.pow(objectClosestY - comparisonObjectY, 2));

                        comparisonIsFood = (comparisonObject.approximateArea() < agarIODataSnapshot.player.approximateArea()) ? true : false;
                        if (!comparisonIsFood && comparisonDistance < safeDistance) {
                            //unsafe food
                            foodIsSafe = false;
                            //System.out.println("Found Unsafe Food.");
                            break;
                        }
                    }
                }

                //If food is safe, add to list ordered by distance
                if(foodIsSafe) {
                    int listSize = foodPriorityList.size();
                    for (int i = 0; i <= listSize; i++) {
                        if (i == listSize || distance < foodPriorityList.get(i).distanceFromPlayer) {
                            foodPriorityList.add(i, object);
                            break;
                        }
                    }
                }
            }
        }

        //System.out.println("Safe Food Counted: "+foodPriorityList.size());
        if(enemiesInSafe.size() > 0) {
            int enemyCount = enemiesInSafe.size();
            //System.out.println("Enemies in Safe Space: " + enemyCount);
            //Basically we sum up the angles from the player to the object and weigh them based on how close they are to the player
            //Closer objects are weighed more heavily
            double totalAngleInfluence = 0;

            //TODO: this algo needs to be ordered better with the enemyCount check
            for (AbstractObject enemy : enemiesInSafe)
            {
                if(enemy.distanceFromPlayer != -1 && enemy.angleTowardsPlayer != -1) {
                    //System.out.println("Object Distance: "+enemy.distanceFromPlayer+", Total Distances: "+totalEnemyDistanceFromPlayer);
                    double percentInfluence = 1 - (enemy.distanceFromPlayer / totalEnemyDistanceInSafe);
                    //Anthony Damato's Contribution
                    percentInfluence = percentInfluence/(enemyCount-1);

                    if(enemyCount == 1)
                    {
                        percentInfluence = 1; //normally if there is only 1 enemy then the above calculation will be 0 but will be the only influence
                    }
                    //System.out.println("Enemies Angle To Player: " + enemy.angleTowardsPlayer + " @ " + percentInfluence+"%");
                    totalAngleInfluence += enemy.angleTowardsPlayer * percentInfluence;
                } else
                {
                    //if we couldn't calculate because something wasn't set right, don't include it
                    enemyCount--;
                }
            }
            //System.out.println("Total Angle: "+ totalAngleInfluence);
            double angleInfluence = (totalAngleInfluence + 180 ) % 360;
            //System.out.println("Influenced Angle: " + angleInfluence);
            //the distance here is a little arbitrary, as long as it is far enough out the player has time to travel and close enough to fit on the screen
            Point2D.Double tempPoint = MathUtils.getPointFromAngleDistance(angleInfluence, (int) (rawImage.getWidth()*0.15));
            //System.out.println("Point2D: "+tempPoint.getX()+","+tempPoint.getY());

            Coordinate influencedCoord = new Coordinate((int)tempPoint.getX(), (int)tempPoint.getY());
            coordinate = new Coordinate(playerCenter.getX()+influencedCoord.getX(), playerCenter.getY()-influencedCoord.getY());
            coordinate.setThreat(Threat.Threat_Val.HIGH);
        }else {
            if(foodPriorityList.size() > 0) //if there is food to eat
            {
                //System.out.println("Here");
                Coordinate closestFoodCenter = foodPriorityList.get(0).approximateCenter();
                coordinate.setX(closestFoodCenter.getX());
                coordinate.setY(closestFoodCenter.getY());
                coordinate.setThreat(Threat.Threat_Val.NONE);
            } //otherwise its centered on the player..
        }

        return coordinate;
    }
}

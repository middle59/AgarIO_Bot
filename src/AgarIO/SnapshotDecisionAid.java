package AgarIO;

import AgarIO.AI.*;
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
 * Uses a snapshot to make a decision for the AgarIOController
 * Created by Mike on 10/13/2016.
 */
public class SnapshotDecisionAid {

    public List<AbstractAI> aiList;
    public AbstractAI activeAI;

    // Manages the Different AIs
    public SnapshotDecisionAid()
    {
        aiList = new ArrayList<>();
        aiList.add(new ClosestFood_AI());
        aiList.add(new RunAway_AI());
        aiList.add(new DecideByClosest_AI());
        aiList.add(new SafeSpaceSimple_AI());
        aiList.add(new SafeSpaceWeighted_AI());

        // The default value on startup
        activeAI = aiList.get(4);
    }

    public Coordinate makeDecision(AgarIODataSnapshot agarIODataSnapshot)
    {
        return activeAI.makeDecision(agarIODataSnapshot);
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
            if(object.approximateArea() > agarIODataSnapshot.player.approximateArea())
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
     * Highest Mass: 28
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

    /**
     * Makes the primary goal to ensure that objects bigger than the player are far enough away before eating something
     * We will just use 40 as the distance to be away from enemies..
     * Highest Mass: 235
     * Time Alive: 1:28
     * @param agarIODataSnapshot
     * @return
     */
    public static Coordinate safeSpaceThenEat(AgarIODataSnapshot agarIODataSnapshot)
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

    /**
     * Same functionality as safeSpaceThenEat except the runaway method weights the run away angle
     * based on all the enemies directions in the safe space
     * Highest Mass: 346
     * Time Alive: 3:01
     * @param agarIODataSnapshot
     * @return
     */
    public static Coordinate weightedAnglesSafeSpace(AgarIODataSnapshot agarIODataSnapshot)
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

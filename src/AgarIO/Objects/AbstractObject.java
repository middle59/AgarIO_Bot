package AgarIO.Objects;

import AgarIO.Grid.Coordinate;

import java.util.HashSet;

/**
 * Created by Mike on 10/8/2016.
 */
public class AbstractObject {

    int pixelCount;
    //Coords in the set are formatted as: x + "," + y
    HashSet<String> pixelCoords;
    boolean isPlayer;
    //This is set in a decision algorithm for enemy objects so it doesn't have to be passed by param..
    public double angleTowardsPlayer;
    public double distanceFromPlayer;

    public AbstractObject()
    {
        isPlayer = false;
        pixelCoords = new HashSet<String>();
        angleTowardsPlayer = -1;
        distanceFromPlayer = -1;
    }

    public int getPixelCount(){ return pixelCount; }
    public void setPixelCount(int pixelCount) { this.pixelCount = pixelCount; }

    public boolean getIsPlayer(){ return isPlayer; }
    public void setIsPlayer(boolean isPlayer) { this.isPlayer = isPlayer; }

    public HashSet<String> getPixelSet(){ return pixelCoords; }
    public void addCoord(String s) { pixelCoords.add(s); pixelCount++; }

    /**
     * Find an estimate of the radius and then approximate the area
     * @return
     */
    public int approximateArea()
    {
        int estimatedArea = -1;

        if(pixelCount > 0)
        {
            //preprocessing
            int leftX = -1;      //lowest x value
            int rightX = -1;   //highest x value
            int topY = -1;     //lowest y value
            int bottomY = -1;    //highest y value

            for (String s : pixelCoords)
            {
                String[] xyCoords = s.split(",");
                int coordX = Integer.parseInt(xyCoords[0]);
                int coordY = Integer.parseInt(xyCoords[1]);
                //if there are not two coords here then invalid data was given
                if(xyCoords.length == 2)
                {
                    if(leftX == -1 || coordX<leftX)
                    {
                        leftX = coordX;
                    }

                    if(rightX == -1 || coordX>rightX)
                    {
                        rightX = coordX;
                    }

                    if(topY == -1 || coordY<topY)
                    {
                        topY = coordY;
                    }

                    if(bottomY == -1 || coordY>bottomY)
                    {
                        bottomY = coordY;
                    }
                }
                else {
                    System.err.println("Invalid data given in coordinate: " +s);
                }
            }

            int estRadius1 = -1;
            int estRadius2 = -1;
            int avgRad = 0;
            int radCount = 0;

            if(leftX != -1 && rightX != -1)
            {
                estRadius1 = (rightX - leftX)/2;
            }
            if(bottomY != -1 && topY != -1)
            {
                estRadius2 = (bottomY - topY)/2;
            }

            if (estRadius1 != -1)
            {
                avgRad += estRadius1;
                radCount++;
            }
            if(estRadius2 != -1)
            {
                avgRad += estRadius2;
                radCount++;
            }

            if(radCount != 0)
            {
                int radius = (avgRad / radCount);
                estimatedArea = (int) (Math.PI * Math.pow(radius, 2));
            }
        }

        return estimatedArea;
    }

    /**
     * This is updated to work differently.
     * Find an estimate of the radius and then approximate the center
     * @return
     */
    public Coordinate approximateCenter()
    {
        Coordinate coordinate = null;

        if(pixelCount > 0)
        {
            //preprocessing
            int leftX = -1;      //lowest x value
            int rightX = -1;   //highest x value
            int topY = -1;     //lowest y value
            int bottomY = -1;    //highest y value

            for (String s : pixelCoords)
            {
                String[] xyCoords = s.split(",");
                int coordX = Integer.parseInt(xyCoords[0]);
                int coordY = Integer.parseInt(xyCoords[1]);
                //if there are not two coords here then invalid data was given
                if(xyCoords.length == 2)
                {
                    if(leftX == -1 || coordX<leftX)
                    {
                        leftX = coordX;
                    }

                    if(rightX == -1 || coordX>rightX)
                    {
                        rightX = coordX;
                    }

                    if(topY == -1 || coordY<topY)
                    {
                        topY = coordY;
                    }

                    if(bottomY == -1 || coordY>bottomY)
                    {
                        bottomY = coordY;
                    }
                }
                else {
                    System.err.println("Invalid data given in coordinate: " +s);
                }
            }

            int horizontalRadius1 = -1;
            int verticalRadius2 = -1;

            //System.out.println("ApproxCenter::"+(leftX+128)+","+(rightX+128)+","+(bottomY+92)+","+(topY+92));
            if(leftX != -1 && rightX != -1)
            {
                horizontalRadius1 = (rightX - leftX)/2;
            }
            if(bottomY != -1 && topY != -1)
            {
                verticalRadius2 = (bottomY - topY)/2;
            }

            if(horizontalRadius1 != -1 && verticalRadius2 != -1)
            {
                coordinate = new Coordinate(leftX + horizontalRadius1, topY + verticalRadius2);
            }
        }

        return coordinate;
    }

    //Approximate the closest pixel in our object to the one given.
    public Coordinate approximateClosest(Coordinate coordinate)
    {
        int xTarget = coordinate.getX();
        int yTarget = coordinate.getY();
        int approxX = xTarget;
        int approxY = yTarget;
        double closestDistance = -1;

        for(String coord : pixelCoords)
        {
            String[] xyCoords = coord.split(",");
            //if there are not two coords here then invalid data was given
            if(xyCoords.length == 2)
            {
                int tempX = Integer.parseInt(xyCoords[0]);
                int tempY = Integer.parseInt(xyCoords[1]);
                double distance = Math.sqrt(Math.pow(xTarget - tempX, 2) + Math.pow(yTarget - tempY, 2));
                if (closestDistance == -1 || distance < closestDistance)
                {
                    closestDistance = distance;
                    approxX = tempX;
                    approxY = tempY;
                }

            }
            else {
                System.err.println("AbstractObject::approximateClosest::Invalid data given in coordinate: " +coord);
            }
        }
        return new Coordinate(approxX, approxY);
    }
}

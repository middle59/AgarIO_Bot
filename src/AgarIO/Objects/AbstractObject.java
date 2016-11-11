package AgarIO.Objects;

import AgarIO.Grid.Coordinate;

import java.util.HashSet;

/**
 * Created by Mike on 10/8/2016.
 */
public class AbstractObject {

    int pixelCount;
    HashSet<String> pixelCoords;
    boolean isPlayer;

    public AbstractObject()
    {
        isPlayer = false;
        pixelCoords = new HashSet<String>();
    }

    public int getPixelCount(){ return pixelCount; }
    public void setPixelCount(int pixelCount) { this.pixelCount = pixelCount; }

    public boolean getIsPlayer(){ return isPlayer; }
    public void setIsPlayer(boolean isPlayer) { this.isPlayer = isPlayer; }

    public HashSet<String> getPixelSet(){ return pixelCoords; }
    public void addCoord(String s) { pixelCoords.add(s); pixelCount++; }

    public int approximateRadius()
    {
        return (int)Math.sqrt((pixelCount/(Math.PI*2)));
    }

    public Coordinate approximateCenter()
    {
        Coordinate coordinate = null;

        if(pixelCount > 0)
        {
            int approxX = 0;
            int approxY = 0;
            int count = 0;

            for (String s : pixelCoords)
            {
                String[] xyCoords = s.split(",");
                //if there are not two coords here then invalid data was given
                if(xyCoords.length == 2)
                {
                    approxX += Integer.parseInt(xyCoords[0]);
                    approxY += Integer.parseInt(xyCoords[1]);
                    count++;
                }
                else {
                    System.err.println("Invalid data given in coordinate: " +s);
                }
            }

            coordinate = new Coordinate(approxX/count, approxY/count);
        }

        return coordinate;
    }
}

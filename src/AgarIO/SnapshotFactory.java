package AgarIO;

import AgarIO.Grid.Coordinate;
import AgarIO.Objects.AbstractObject;
import Utilities.ImageProcessor;
import Utilities.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by Mike on 10/8/2016.
 */
public class SnapshotFactory {


    private static HashSet<String> masterHashSet;
    private static AbstractObject player;
    private static ArrayList<AbstractObject> enemyList;

    /**
     * This will analyze an edge detector translated image.
     * @param colorArray
     * @return
     */
    public static AgarIODataSnapshot analyzeImage(Color[][] colorArray)
    {
        AgarIODataSnapshot agarIODataSnapshot = new AgarIODataSnapshot();
        agarIODataSnapshot.rawImage = ImageProcessor.getImageFromArray(colorArray);

        player = null;
        enemyList = new ArrayList<>();
        masterHashSet = new HashSet<>();
        findObjects(colorArray);

        agarIODataSnapshot.player = player;
        agarIODataSnapshot.enemyList = enemyList;

        Date timestamp = new Date();
        agarIODataSnapshot.timestamp = timestamp;
        return agarIODataSnapshot;
    }

    public static AgarIODataSnapshot analyzeImage(BufferedImage bufferedImage)
    {
        return analyzeImage(ImageProcessor.loadBufferedImage(bufferedImage));
    }

    //Finds a player in an edge detector translated image
    private static void findObjects(Color[][] colorArray)
    {

        for(int i=0; i<colorArray.length; i++)
        {
            for(int j=0; j<colorArray[i].length; j++)
            {
                Color curr = colorArray[i][j];
                if(curr.getRed() == 0 && curr.getGreen() == 0 && curr.getBlue() == 0)
                {
                    //if this is a new object undiscovered yet
                    if(! masterHashSet.contains(i + "," + j))
                    {
                        AbstractObject object = discoverObject(colorArray, i, j, new AbstractObject());
                        //System.out.println("Is Player: "+object.getIsPlayer());
                        Coordinate coordinate = object.approximateCenter();
                        //System.out.println("Object Approx. Center: "+(coordinate.getX()+128)+","+(coordinate.getY()+92));
                        if(object.getIsPlayer())
                        {
                            player = object;
                        }
                        else
                        {
                            enemyList.add(object);
                        }
                    }
                }
            }
        }
    }

    private static AbstractObject discoverObject(Color[][] colorArray, int x, int y, AbstractObject object)
    {
        Coordinate screenCenter = new Coordinate(colorArray.length/2, colorArray[0].length/2);
        //System.out.println("Screen Center: "+screenCenter.getX()+","+screenCenter.getY());
        int playerPossibleDistance = ((int)(25/AgarIOManager.scale)); //this will have to scale with the screen size..

        //Error check bounds
        if(x >= 0 && x < colorArray.length && y >= 0 && y < colorArray[0].length) {
            Color curr = colorArray[x][y];
            if (curr.getRed() == 0 && curr.getGreen() == 0 && curr.getBlue() == 0) {
                if (!masterHashSet.contains(x + "," + y)) {
                    //System.out.println(masterHashSet.size());
                    masterHashSet.add(x + "," + y);
                    object.addCoord(x + "," + y);

                    //Update: determine if this is the player by testing if any of the pixels are
                    // so close to the screen center..
                    double distance = MathUtils.getPointDistance(screenCenter.getX(), screenCenter.getY(), x, y);
                    if (distance < playerPossibleDistance) {
                        if(object.getIsPlayer()!=true) {
                            //System.out.println("Pixel: " + x + "," + y + " is from center: " + distance);
                            //System.out.println("Found a player object.");
                            object.setIsPlayer(true);
                        }
                    }

                    //NOTE - Where O is colorArray[x][y], we only have to discover X pixels due to the ordering of findObjects and this expansion

                    //      | X | X | X |
                    //      | X | O | X |
                    //      |   | X |   |

                    //top left
                    discoverObject(colorArray, x - 1, y - 1, object);
                    //top right
                    discoverObject(colorArray, x + 1, y - 1, object);
                    //top middle
                    discoverObject(colorArray, x, y - 1, object);

                    //middle left
                    discoverObject(colorArray, x - 1, y, object);
                    //middle right
                    discoverObject(colorArray, x + 1, y, object);
                    //bottom middle
                    discoverObject(colorArray, x, y + 1, object);
                }
            }
        }

        return object;
    }
}

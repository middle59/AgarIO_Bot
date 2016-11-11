package AgarIO;

import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mike on 10/2/2016.
 */
public class AgarIODataSnapshot {

    BufferedImage rawImage;
    AbstractObject player;
    ArrayList<AbstractObject> enemyList;
    Date timestamp;

    public AgarIODataSnapshot(){}

}

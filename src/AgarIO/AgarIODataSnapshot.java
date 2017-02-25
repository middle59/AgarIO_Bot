package AgarIO;

import AgarIO.Objects.AbstractObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mike on 10/2/2016.
 */
public class AgarIODataSnapshot {

    public BufferedImage rawImage;
    public AbstractObject player;
    public ArrayList<AbstractObject> enemyList;
    public Date timestamp;

    public AgarIODataSnapshot(){}

}

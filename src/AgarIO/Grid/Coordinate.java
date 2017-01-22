package AgarIO.Grid;

/**
 * Created by Mike on 10/8/2016.
 */
public class Coordinate {

    private int x, y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX(){ return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /**
     * Adjusts the current coordinates to the screenConfiguration
     * Multiplies by the scale and then adds the offsets
     * @param screenConfiguration
     */
    public void adjustToScreenConfiguration(ScreenConfiguration screenConfiguration)
    {

    }


    public String toString(){
        return "[X: " + x + ", Y: " + y + "]";
    }
}

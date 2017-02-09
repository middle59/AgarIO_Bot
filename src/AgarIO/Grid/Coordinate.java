package AgarIO.Grid;

/**
 * Created by Mike on 10/8/2016.
 * This coordinate is specific to AgarIO so it will have additional fields
 */
public class Coordinate {

    private int x, y;
    Threat threat;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
        threat = new Threat();
    }

    public int getX(){ return x; }
    public int getY() { return y; }
    public Threat getThreat() { return threat; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setThreat(Threat.Threat_Val new_threat) { threat.threat_level = new_threat; }

    public String toString(){
        return "[X: " + x + ", Y: " + y + "]";
    }
}

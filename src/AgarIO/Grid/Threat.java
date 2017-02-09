package AgarIO.Grid;

import java.awt.*;

/**
 * Created by Mike on 2/5/2017.
 */
public class Threat {

    public Threat_Val threat_level;

    public Threat(){
       threat_level = Threat_Val.NONE; //None by default
    }

    public enum Threat_Val {
        NONE,
        LOW,
        MEDIUM,
        HIGH
    }

    public Color getThreatColor()
    {
        Color color;

        switch (threat_level)
        {
            case NONE:
                color = Color.GREEN;
                break;
            case LOW:
                color = Color.YELLOW;
                break;
            case MEDIUM:
                color = Color.ORANGE;
                break;
            case HIGH:
                color = Color.RED;
                break;
            default:
                color = Color.BLACK;

        }

        return color;
    }

}

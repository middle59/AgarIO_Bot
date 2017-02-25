package AgarIO.AI;

import AgarIO.AgarIODataSnapshot;
import AgarIO.Grid.Coordinate;

/**
 * Created by Mike on 2/19/2017.
 */
public abstract class AbstractAI {

    public String AI_Name;
    public String description;

    public abstract Coordinate makeDecision(AgarIODataSnapshot agarIODataSnapshot);
}

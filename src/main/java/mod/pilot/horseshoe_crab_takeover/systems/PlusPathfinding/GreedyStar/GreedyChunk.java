package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import java.util.ArrayList;

public class GreedyChunk {
    public GreedyChunk(long CHUNK_ID){
        this.CHUNK_ID = CHUNK_ID;
    }
    public final long CHUNK_ID;

    public ArrayList<GreedyMap> maps;

}

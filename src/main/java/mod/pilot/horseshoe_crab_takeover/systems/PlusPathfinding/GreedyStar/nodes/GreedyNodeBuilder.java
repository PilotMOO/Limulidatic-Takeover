package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import org.jetbrains.annotations.Nullable;

public class GreedyNodeBuilder {
    @SuppressWarnings("unchecked") //Fuck you Java and your shitty fucking Generics system
    public static GreedyNode constructGroundedNode(int x, int y, int z,
                                                   @Nullable final GreedyMap<GreedyNode> optionalMap){
        GreedyMap<GreedyNode> gMap;
        if (optionalMap == null){
            GreedyChunk gChunk = GreedyWorld.retrieveOrCreateGreedyChunk(
                    GreedyChunk.computeCoordinatesToID(x, z));
            gMap = (GreedyMap<GreedyNode>)gChunk.locateClosest(x, y, z, GreedyChunk.SearchType.MapExtension);
        } else gMap = optionalMap;
    }
    public static GreedyNode constructGroundedNode(int x, int y, int z,
                                                   @Nullable final GreedyChunk optionalChunk) {
        GreedyChunk gChunk;
        if (optionalChunk == null){
            gChunk = GreedyWorld.retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(x, z));
        } else gChunk = optionalChunk;
    }

}

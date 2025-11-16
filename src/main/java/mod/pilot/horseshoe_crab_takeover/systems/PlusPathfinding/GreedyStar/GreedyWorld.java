package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3i;

import java.util.HashMap;

public class GreedyWorld {
    public static final int DEFAULT_MAX_CHUNK_COUNT = 65535;
    public static int MAX_CHUNK_COUNT;

    public static void INIT(){
        if (greedyWorldCache != null) throw new RuntimeException("ERROR! Tried to set up GreedyWorld despite a pre-existing cache existing! Make sure the GreedyWorld cache is already cleared via GreedyWorld#cleanCache() before calling INIT");
        //Add config option for changing default max chunk count
        MAX_CHUNK_COUNT = DEFAULT_MAX_CHUNK_COUNT;
    }
    public static void cleanCache(){
        if (greedyWorldCache == null) throw new RuntimeException("ERROR! Tried to clear GreedyWorld cache before initialization! Make sure GreedyWorld is already set up before attempting to clear Cache.");
        greedyWorldCache = null;
    }

    //WIP! Set up long-term storage and unpacking from file
    private static HashMap<Long, GreedyChunk> computeGreedyCache() {
        return new HashMap<>();
    }


    private static HashMap<Long, GreedyChunk> greedyWorldCache;

    public static GreedyChunk retrieveFromWorldCoordinates(Vec3 pos){
        return retrieveOrCreateGreedyChunk(computeCoordinatesToID(pos));
    }
    public static GreedyChunk retrieveOrCreateGreedyChunk(long ID){
        return greedyWorldCache.computeIfAbsent(ID, GreedyWorld::createChunk);
    }

    private static GreedyChunk createChunk(long ID){
        return new GreedyChunk(ID);
    }
    public static long computeCoordinatesToID(Vec3 pos){
        return computeCoordinatesToID((int)Math.floor(pos.x), (int)Math.floor(pos.z));
    }

    /**
     * Computes the map ID of the relevant Greedy Chunk from the given X and Z coordinates
     * @param x the X coordinate in-world to compute the related GC ID from (the last 32 bits)
     * @param z the Z coordinate in-world to compute the related GC ID from (the first 32 bits)
     * @return the long Chunk ID for the related Greedy Chunk (if present)
     */
    public static long computeCoordinatesToID(int x, int z){
        return (x / 64L << 32) | (x / 64L);
    }

    /**
     * Creates the "minor" world coordinate from the supplied ID
     * (the coordinate of the corner closest to [0, 0] from the given chunk)
     * @param id the Chunk ID to unpack
     * @param yCoordinate the expected Y value for the returned {@link Vector3i}
     * @return a {@link Vector3i} with the supplied Y value and X and Z values unpacked
     * from the Greedy Chunk ID
     */
    public static Vector3i unpackMinorWorldCoordinatesFromID(long id, int yCoordinate){
        int x = (int)(id >>> 32) * 64; //Pushes the last 32 bits to the front and casts to int
        int z = (int)id * 64; //Casts to int directly to shave off the last 32 bits
        return new Vector3i(x, yCoordinate, z);
    }
}

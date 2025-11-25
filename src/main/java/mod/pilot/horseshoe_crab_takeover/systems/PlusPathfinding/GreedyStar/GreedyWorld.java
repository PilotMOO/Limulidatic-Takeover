package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;

public class GreedyWorld {
    public static final int DEFAULT_MAX_CHUNK_COUNT = 65535;
    public static int MAX_CHUNK_COUNT;

    public static void init(){
        if (greedyWorldCache != null) throw new RuntimeException("ERROR! Tried to set up GreedyWorld despite a pre-existing cache existing! Make sure the GreedyWorld cache is already cleared via GreedyWorld#cleanCache() before calling init");
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

    private static GreedyChunk[] greedyWorldCache;
    public static GreedyChunk addChunkToRAMCache(GreedyChunk chunk){
        if (chunk == null) return null;
        int newSize = greedyWorldCache.length + 1;
        GreedyChunk[] newRAMCache = new GreedyChunk[newSize];
        System.arraycopy(greedyWorldCache, 0, newRAMCache, 0, newSize);
        newRAMCache[newSize - 1] = chunk;
        greedyWorldCache = newRAMCache;
        return chunk;
    }
    public static @Nullable GreedyChunk getFromRAMCache(long chunkID){
        for (GreedyChunk gChunk : greedyWorldCache) if (gChunk.chunkID == chunkID) return gChunk;
        return null;
    }

    public static GreedyChunk retrieveFromWorldCoordinates(Vec3 pos){
        return retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(pos));
    }
    public static GreedyChunk retrieveFromWorldCoordinates(int worldX, int worldZ){
        return retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(worldX, worldZ));
    }
    public static GreedyChunk retrieveOrCreateGreedyChunk(long chunkID){
        GreedyChunk gChunk = getFromRAMCache(chunkID);
        if (gChunk != null) return gChunk;
        else gChunk = addChunkToRAMCache(checkFileCache(chunkID));
        return gChunk != null ? gChunk : createChunkInRAM(chunkID);
    }
    public static @Nullable GreedyChunk retrieveOnly(long chunkID){
        GreedyChunk gChunk = getFromRAMCache(chunkID);
        if (gChunk != null) return gChunk;
        else return addChunkToRAMCache(checkFileCache(chunkID));
    }

    //ToDo: set up file cache
    private static @Nullable GreedyChunk checkFileCache(long chunkID) {
        return null;
    }

    private static GreedyChunk createChunkInRAM(long ID){
        return addChunkToRAMCache(new GreedyChunk(ID));
    }

    public static long isolateChunkID(long globalID){
        return (globalID >>> 16) << 16;
    }
    public static int isolateMapLevelID(long globalID){
        return (int)(globalID & mapBitmask);
    }
    public static byte isolateMapID(long globalID){
        return (byte)((globalID << 48) >>> 56);
    }
    public static byte isolateNodeID(long globalID){
        return (byte)(globalID & nodeBitmask);
    }
    public static byte isolateNodeID(int mapLevelID){
        return (byte)(mapLevelID & nodeBitmask);
    }
    public static final long nodeBitmask = ~(-1 << 8);
    public static final long mapBitmask = ~(-1 << 16);
}

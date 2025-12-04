package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

public class GreedyWorld {
    private static boolean multiworld;
    public static class MultiworldAccess{
        public void registerNewWorld(String id, String ... tagsByName){
            multiworld = true;
            worldIdentifiers = DataHelper.Arrays.expandAndAdd(worldIdentifiers, id);
            worldTags = DataHelper.Arrays.expandAndAdd(worldTags, tagsByName);
        }
        private String[] worldIdentifiers;
        private String[][] worldTags;
    }

    public static GreedyWorld WORLD_DEFAULT(){
        if (defaultInit) return greedyWorld_DEFAULT;
        else return greedyWorld_DEFAULT = new GreedyWorld("WORLD_DEFAULT");
    }
    static boolean defaultInit = false;


    public static GreedyWorld greedyWorld_DEFAULT;

    private static GreedyWorld[] worlds;

    public static Optional<GreedyWorld> byIdentifier(String identifier){
        for (GreedyWorld gWorld : worlds) {
            if (gWorld.WORLD_IDENTIFIER.equals(identifier)) return Optional.of(gWorld);
        }
        return Optional.empty();
    }

    private GreedyWorld(String identifier, String ... tagsByName){
        WORLD_IDENTIFIER = identifier;
        tags = new GreedyFileManager.WorldTag[tagsByName.length];

        this.init();
    }
    public final String WORLD_IDENTIFIER;
    private GreedyFileManager.WorldTag[] tags;
    public boolean hasTag(GreedyFileManager.WorldTag tag){
        for (GreedyFileManager.WorldTag wTag : tags) if (tag.fuzzy(wTag)) return true;
        return false;
    }

    public static final int DEFAULT_MAX_CHUNK_COUNT = 65535;
    public int MAX_CHUNK_COUNT;

    public void init(){
        if (greedyWorldCache != null) throw new RuntimeException("ERROR! Tried to set up GreedyWorld despite a pre-existing cache existing! Make sure the GreedyWorld cache is already cleared via GreedyWorld#cleanCache() before calling init");
        //Add config option for changing default max chunk count
        MAX_CHUNK_COUNT = DEFAULT_MAX_CHUNK_COUNT;
    }
    public void cleanCache(){
        if (greedyWorldCache == null) throw new RuntimeException("ERROR! Tried to clear GreedyWorld cache before initialization! Make sure GreedyWorld is already set up before attempting to clear Cache.");
        greedyWorldCache = null;
    }

    //WIP! Set up long-term storage and unpacking from file
    private HashMap<Long, GreedyChunk> computeGreedyCache() {
        return new HashMap<>();
    }

    private GreedyChunk[] greedyWorldCache;
    public GreedyChunk addChunkToRAMCache(GreedyChunk chunk){
        if (chunk == null) return null;
        int newSize = greedyWorldCache.length + 1;
        GreedyChunk[] newRAMCache = new GreedyChunk[newSize];
        System.arraycopy(greedyWorldCache, 0, newRAMCache, 0, newSize);
        newRAMCache[newSize - 1] = chunk;
        greedyWorldCache = newRAMCache;
        return chunk;
    }
    public @Nullable GreedyChunk getFromRAMCache(long chunkID){
        for (GreedyChunk gChunk : greedyWorldCache) if (gChunk.chunkID == chunkID) return gChunk;
        return null;
    }

    public GreedyChunk retrieveFromWorldCoordinates(Vec3 pos){
        return retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(pos));
    }
    public GreedyChunk retrieveFromWorldCoordinates(int worldX, int worldZ){
        return retrieveOrCreateGreedyChunk(GreedyChunk.computeCoordinatesToID(worldX, worldZ));
    }
    public GreedyChunk retrieveOrCreateGreedyChunk(long chunkID){
        GreedyChunk gChunk = getFromRAMCache(chunkID);
        if (gChunk != null) return gChunk;
        else gChunk = addChunkToRAMCache(checkFileCache(chunkID));
        return gChunk != null ? gChunk : createChunkInRAM(chunkID);
    }
    public @Nullable GreedyChunk retrieveOnly(long chunkID){
        GreedyChunk gChunk = getFromRAMCache(chunkID);
        if (gChunk != null) return gChunk;
        else return addChunkToRAMCache(checkFileCache(chunkID));
    }

    //ToDo: set up file cache
    private @Nullable GreedyChunk checkFileCache(long chunkID) {
        return null;
    }

    private GreedyChunk createChunkInRAM(long ID){
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

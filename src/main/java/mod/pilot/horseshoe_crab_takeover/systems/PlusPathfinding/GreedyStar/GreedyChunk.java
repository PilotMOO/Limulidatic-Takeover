package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3i;


public class GreedyChunk {
    public GreedyChunk(long chunkID){
        this.chunkID = chunkID;
        int x = (int)(chunkID >>> 40) * 64;
        int z = (int)(chunkID << 24 >>> 40) * 64;
        relative = new Vector2i(x, z);
    }
    public final Vector2i relative;
    public final long chunkID;

    public GreedyMap<?>[] maps;
    public void addMap(GreedyMap<?> map){
        int newSize = maps.length + 1;
        GreedyMap<?>[] newMaps = new GreedyMap<?>[newSize];
        System.arraycopy(maps, 0, newMaps, 0, newSize);
        newMaps[newSize - 1] = map;
        maps = newMaps;
    }
    public void removeMap(int index){
        int newSize = maps.length - 1;
        GreedyMap<?>[] newMaps = new GreedyMap<?>[newSize];
        if (index == 0){
            System.arraycopy(maps, 1, newMaps, 0, newSize);
        }
        else if (index == newSize){
            System.arraycopy(maps, 0, newMaps, 0, newSize);
        }
        else {
            System.arraycopy(maps, 0, newMaps, 0, index - 1);
            System.arraycopy(maps, index + 1, newMaps, index, newSize - index);
        }
        maps = newMaps;
    }
    public @Nullable GreedyMap<?> fromID(byte mapID){
        for (GreedyMap<?> map : maps){
            if (map.mapID == mapID) return map;
        }
        return null;
    }

    public long computeGlobalID(int mapLevelID){
        if (mapLevelID >= 65536){
            System.err.printf("[GREEDY CHUNK] WARNING! Invalid mapLevelID[%s] located in computeGlobalID() call! Ensure only the first 16 bits (excluding first 3) are populated! Returning defaulted ID [-1]...", BitwiseDataHelper.parseIntToBinary(mapLevelID));
            return -1;
        }
        else return chunkID | mapLevelID;
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
        return (x / 64L << 40) | ((z / 64L) << 16);
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
        int x = (int)(id >>> 40) * 64;
        int z = (int)(id << 24 >>> 40) * 64;
        return new Vector3i(x, yCoordinate, z);
    }
}

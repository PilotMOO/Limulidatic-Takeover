package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class GreedyChunk {
    public static byte GreedyChunkXZDimensions = 64;

    public GreedyChunk(long chunkID){
        this.chunkID = chunkID;

        int xIso = (int)(chunkID >>> 40);
        int zIso = (int)(chunkID << 24 >>> 40);
        int bit24 = 1 << 23;
        boolean negX = (bit24 & xIso) != 0;
        boolean negZ = (bit24 & zIso) != 0;
        if (negX) xIso = (xIso & ~bit24) * -1;
        if (negZ) zIso = (zIso & ~bit24) * -1;
        relative = new Vector2i(xIso * 64, zIso * 64);

        //ToDo:
        // NOTE! The relative value MIGHT be offset incorrectly for negative values!
        // make sure to check and fix that
    }
    public final Vector2i relative;
    public final long chunkID;

    public GreedyMap[] maps;
    public void addMap(GreedyMap map){
        maps = DataHelper.Arrays.expandAndAdd(maps, map);
        /*int newSize = maps.length + 1;
        GreedyMap[] newMaps = new GreedyMap[newSize];
        System.arraycopy(maps, 0, newMaps, 0, newSize);
        newMaps[newSize - 1] = map;
        maps = newMaps;*/
    }
    public void removeMap(int index){
        int newSize = maps.length - 1;
        GreedyMap[] newMaps = new GreedyMap[newSize];
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
    public @Nullable GreedyMap getMap(byte mapID){
        for (GreedyMap map : maps){
            if (map.mapID == mapID) return map;
        }
        return null;
    }

    public GreedyMap buildNewMap() {
        GreedyMap gMap = new GreedyMap(newMapID());
        addMap(gMap);
        return gMap;
    }
    public byte newMapID(){
        byte[] existingIDs = new byte[maps.length];
        for (int i = 0; i < maps.length; i++) existingIDs[i] = maps[i].mapID;

        byte id = 0; //Node ID 0 is not valid, this will be inc'd before being used
        while(true){
            id++; //Inc the ID
            boolean flag = false; //if the ID is already used
            for (byte b : existingIDs){
                if (b == id) { //Check all ID's, if any match, set the flag, break, and continue.
                    flag = true; break;
                }
            }
            if (flag) continue; //If the ID is already in use, try the next inc
            return id; //Otherwise, this is our next ID!
        }
    }

    public enum SearchType{
        InBounds,
        MapExtension,
        MapExtensionDefault,
        AnyClosest
    }
    /**
     * Looks for the closest GreedyMap within this GChunk that matches the given [XYZ] coordinates
     * Behaviour changes based off of the 4th argument:
     * <p>{@code InBounds}: The target GreedyMap's {@link GreedyMap#MapBound}
     * MUST contain the supplied coordinate. Strictest argument type.</p>
     * {@code MapExtension}: Same as {@code InBounds} but it adds the Map's
     * {@link GreedyMap#MapExtensionRange} to the bounds.
     * <p>{@code MapExtensionDefault} Same as prior, but ignores the personalized map extension and assumes the default value [{@link GreedyMap#DEFAULT_MapExtensionRange}]</p>
     * {@code AnyClosest} Returns the closest GreedyMap within the GChunk regardless of its distance to the point. Will still return {@code null} if the GChunk is empty
     * ---I suck at coding so this doesn't do shit rn blehhhh :MiddleFinger:
     * @param x the X coordinate of the reference area
     * @param y the Y coordinate of the reference area
     * @param z the Z coordinate of the reference area
     * @param searchType Affects the behaviour of the search function.
     *                  See main description for explanation
     * @return Might return a GreedyChunk relative to the supplied coordinates
     */
    public @Nullable GreedyMap locateClosest(final int x, final int y, final int z,
                                                SearchType searchType){
        if (maps.length == 0) return null;
        return switch(searchType){
            case InBounds -> {
                for (GreedyMap gMap : maps) if (gMap.MapBound.contains(x,y,z)) yield gMap;
                yield null;
            }
            case MapExtension -> {
                for (GreedyMap gMap : maps) {
                    if (gMap.MapBound.containsLargePoint(x,y,z, gMap.MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case MapExtensionDefault -> {
                for (GreedyMap gMap : maps) {
                    if (gMap.MapBound.containsLargePoint(x,y,z, GreedyMap.DEFAULT_MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            //idk how to figure out the distance from a point to the nearest
            // part of a rectangular prism so this'll just be... left as this until I get gud
            case AnyClosest -> null;
            default -> null;
        };
    }

    /**
     * I dont feel like copying over the other javadoc so just go read that
     * <p>{@link GreedyChunk#locateClosest(int, int, int, SearchType)}</p>
     * Oh and this override of it locates the closest map within distance of any corner of the QSpace
     * @param localizedQSpace the QuadSpace to evaluate distance to
     * @param searchType dictates how the method decides if a map is valid for returning
     * @return fuck you
     */
    public @Nullable GreedyMap locateClosest(QuadSpace localizedQSpace, SearchType searchType){
        if (maps.length == 0) return null;
        else return switch (searchType){
            case InBounds -> {
                for (GreedyMap gMap : maps){
                    if (gMap.MapBound.intersects(localizedQSpace)) yield gMap;
                }
                yield null;
            }
            case MapExtension -> {
                for (GreedyMap gMap : maps){
                    if (gMap.MapBound.intersectInflated(localizedQSpace, gMap.MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case MapExtensionDefault -> {
                for (GreedyMap gMap : maps){
                    if (gMap.MapBound.intersectInflated(localizedQSpace, GreedyMap.DEFAULT_MapExtensionRange)){
                        yield gMap;
                    }
                }
                yield null;
            }
            //Bleh i dont wanna do this shit so nah
            case AnyClosest -> null;
        };
    }

    public long computeGlobalID(int mapLevelID){
        if (mapLevelID >= 65536){
            System.err.printf("[GREEDY CHUNK] WARNING! Invalid mapLevelID[%s] located in computeGlobalID() call! Ensure only the first 16 bits (excluding first 3) are populated! Returning defaulted ID [-1]...%n", BitwiseDataHelper.parseIntToBinary(mapLevelID));
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
        //This is really awkward to test and work on
        // because how the fuck do you test if any value would properly compress???
        //I also need to rework how it handles negative numbers,
        // because right now GreedyChunk ID = 0 covers -63 to 63 (both x and z),
        // which is 127 total blocks along one axis
        // that's almost twice the actual amount it should have...
        // this happens for all chunks along a given axis

        boolean negX = x < 0, negZ = z < 0;
        long xComp = Math.abs(x) / 64L,
                zComp = Math.abs(z) / 64L;
        //I'm losing it
        // ough compressing negative values is fucking annoying
        /*if (Math.abs(xComp) >= 8388608 || Math.abs(zComp) >= 8388608){
            throw new RuntimeException("FUCK! INVALID COMP VALUES [" + xComp + ", " + zComp + "]");
        }*/
        /*if (negX) xComp--;
        if (negZ) zComp--;*/
        System.out.println("x, z comp pre-shift [THESE VALUES ARE ABSOLUTE]: x " + xComp + ", z " + zComp);
        xComp <<= 40;
        //Clear mask removes any bits after the 40th one,
        // just in case it's negative and those are filled with 1's
        zComp = (zComp << 16) & clearMask;

        System.out.println("xComp binary[" + BitwiseDataHelper.parseLongToBinary(xComp) + "]");
        System.out.println("zComp binary[" + BitwiseDataHelper.parseLongToBinary(zComp) + "]");

        if (negX) {
            xComp |= finalBitMask;
            System.out.println("x negative fix[" + BitwiseDataHelper.parseLongToBinary(xComp) + "]");
        }
        if (negZ) {
            zComp |= fortiethBitMask;
            System.out.println("z negative fix[" + BitwiseDataHelper.parseLongToBinary(zComp) + "]");
        }
        return xComp | zComp;
    }
    //the FINAL bit of a 64 bit string
    public static final long finalBitMask = -9223372036854775808L;
    //The 40th bit of a 64 bit string
    public static final long fortiethBitMask = 549755813888L;
    //40th and below bits are 1, rest are 0
    public static final long clearMask = 1099511627775L;

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

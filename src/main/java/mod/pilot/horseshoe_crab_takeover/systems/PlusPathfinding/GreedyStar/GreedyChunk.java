package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.Arrays;

public class GreedyChunk {
    public static byte GreedyChunkXZDimensions = 64;

    public GreedyChunk(long chunkID){this(chunkID, new GreedyMap[0]);}
    public GreedyChunk(long chunkID, GreedyMap... maps){
        this.chunkID = chunkID;

        //Isolate the X and Z values from their designated 24 bit section
        int xIso = (int)(chunkID >>> 40); //X is in the last 24
        int zIso = (int)(chunkID << 24 >>> 40); //and Z in the next 24
        // Might hardcode this later, it's the 24th bit =1 while the rest is 0 for masking
        int bit24 = 1 << 23;
        //The 24th bit in the two isolated sections contains the negative flag
        // (the same job that the last bit in a signed binary number holds)
        // Negative values need special attention to decompress,
        // so these flags help with that
        boolean negX = (bit24 & xIso) != 0; /**/
        boolean negZ = (bit24 & zIso) != 0; /**/
        //Decompress the X and Z values--
        //If negative, we need to default the 24th bit [xIso & ~bit24]
        // (contained the negative flag) and then flip to negative [* -1]
        // (values are absoluted before compression, this undoes that step)
        /*See last comment block for why we are subtracting 1*/
        if (negX) xIso = ((xIso & ~bit24) * -1) - 1;
        if (negZ) zIso = ((zIso & ~bit24) * -1) - 1;
        /**/
        //Finally take the isolated positional values and multiply by the chunk size
        // to undo the initial division
        relative = new Vector2i(
                xIso * GreedyChunkXZDimensions,
                zIso * GreedyChunkXZDimensions);

        //Make sure to subtract 1 from the result of
        // the negative-corrected isolated position [xIso, zIso]
        // IF the resulting relative is negative!
        // Because division is zero-oriented
        // (dividing numbers prioritize 0 rather than the lowest value),
        // we need to offset the resulting values by -1 GChunk's
        // otherwise it would return the relative position of the GChunk
        // relative to this chunk by +1 on either axis that is negative
        //Our logic assumes the relative position is the minor of the Chunk (lowest XZ values)
        // rather than closest to zero, so we have to correct this discrepancy
        // otherwise the other logic will meddle with incorrect positional values

        //Yes this is wayy too much work just to avoid having to
        // store 2 integer positions for every object.
        //Hey, those 64 total bits add up over hundreds of objects!
        // (i think...)

        this.maps = maps;
    }
    public final Vector2i relative;
    public final long chunkID;

    public GreedyMap[] maps;
    public void addMap(GreedyMap map){
        //maps = DataHelper.Arrays.expandAndAdd(maps, map);
        int newSize = maps.length + 1;
        GreedyMap[] newMaps = new GreedyMap[newSize];
        if (newSize > 1) System.arraycopy(maps, 0, newMaps, 0, newSize - 1);
        newMaps[newSize - 1] = map;
        maps = newMaps;
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
        System.out.println("NEW map id " + gMap.mapID + ", this gchunk used to have "
        + maps.length + " maps");
        addMap(gMap);
        System.out.println("it now has " + maps.length);
        System.out.println("[BUILD NEW MAP] printing self... " + this);
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
     * Behavior changes based off of the 4th argument:
     * <p>{@code InBounds}: The target GreedyMap's {@link GreedyMap#mapBounds}
     * MUST contain the supplied coordinate. Strictest argument type.</p>
     * {@code MapExtension}: Same as {@code InBounds} but it adds the Map's
     * {@link GreedyMap#MapExtensionRange} to the bounds.
     * <p>{@code MapExtensionDefault} Same as prior, but ignores the personalized map extension and assumes the default value [{@link GreedyMap#DEFAULT_MapExtensionRange}]</p>
     * {@code AnyClosest} Returns the closest GreedyMap within the GChunk regardless of its distance to the point. Will still return {@code null} if the GChunk is empty
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
        System.out.println("map count is not zero, totaling to " + maps.length);
        System.out.println("Search type " + searchType);
        return switch(searchType){
            case InBounds -> {
                for (GreedyMap gMap : maps) if (gMap.getBounds().contains(x,y,z)) yield gMap;
                yield null;
            }
            case MapExtension -> {
                for (GreedyMap gMap : maps) {
                    if (gMap.getBounds().containsLargePoint(x,y,z, gMap.MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case MapExtensionDefault -> {
                for (GreedyMap gMap : maps) {
                    if (gMap.getBounds().containsLargePoint(x,y,z, GreedyMap.DEFAULT_MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case AnyClosest -> {
                GreedyMap closest = null;
                double dist = Double.MAX_VALUE;
                for (GreedyMap gMap : maps) {
                    double dist1 = gMap.getBounds().distanceToEdge(x, y, z);
                    if (dist1 < dist) {
                        closest = gMap;
                        dist = dist1;
                    }
                }
                yield closest;
            }
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
                    if (gMap.getBounds().intersects(localizedQSpace)) yield gMap;
                }
                yield null;
            }
            case MapExtension -> {
                for (GreedyMap gMap : maps){
                    if (gMap.getBounds().intersectInflated(localizedQSpace, gMap.MapExtensionRange * 2)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case MapExtensionDefault -> {
                for (GreedyMap gMap : maps){
                    if (gMap.getBounds().intersectInflated(localizedQSpace, GreedyMap.DEFAULT_MapExtensionRange)){
                        yield gMap;
                    }
                }
                yield null;
            }
            case AnyClosest -> {
                GreedyMap closest = null;
                double dist = Double.MAX_VALUE;
                for (GreedyMap gMap : maps) {
                    double dist1 = gMap.getBounds().distanceEdgeToEdge(localizedQSpace);
                    if (dist1 < dist) {
                        closest = gMap;
                        dist = dist1;
                    }
                }
                yield closest;
            }
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
     * <p>
     *
     * </p>
     *     NOTE! This compression system can only handle X and Z values between
     *     {@code -2^23 * 64} and {@code 2^23 * 64}, little over half a billion
     *     (roughly, I haven't bothered to find the specific values where it breaks...)
     *     and WILL return invalid ID's if incorrect values are inputted.
     *     This isn't that much of a concern though,
     *     vanilla worlds only extend like, 30 million blocks
     *     so unless modded otherwise, this system should work fine 100% of the time :)
     * <p>
     *
     * </p><p>
     *
     * </p>
     * The code is littered with a bunch of old commented out shit and other junk.
     * The actual code isn't very complex, but it's a mess of comments that I don't
     * want to remove for some reason. idk
     * @param x the X coordinate in-world to compute the related GC ID from (the last 24 bits)
     * @param z the Z coordinate in-world to compute the related GC ID from (the 24 bits after the first 16 bits)
     * @return the long Chunk ID for the related Greedy Chunk (if present)
     */
    public static long computeCoordinatesToID(int x, int z){
        //536870912 to -536870912 (or somewhere around that point...) is the max valid value
        // that this system can properly compress. Any other value can't fit in the
        // allocated 24 bits per horizontal axis per ID.
        //Vanilla MC worlds only span like negative 30 million to positive 30 million
        // so this shouldn't be any real issue if the world boarder isn't manually
        // extended with mods.
        if (Math.abs(x) > 536870912 || Math.abs(z) > 536870912) return -1L;
        //This is really awkward to test and work on
        // because how the fuck do you test if any value would properly compress???
        //I also need to rework how it handles negative numbers,
        // because right now GreedyChunk ID = 0 covers -63 to 63 (both x and z),
        // which is 127 total blocks along one axis
        // that's almost twice the actual amount it should have...
        // this happens for all chunks along a given axis
        //^^^^ this is all fixed now, the Constructor explains the decompression

        boolean negX = x < 0, negZ = z < 0;
        //Normally I would want to use the static chunk size object
        // but that's stored as a byte and we need a long here...
        long xComp = Math.abs(x) / 64L,
                zComp = Math.abs(z) / 64L;
        //I'm losing it
        // I dont feel like removing these comments from prior attempts
        // ough compressing negative values is fucking annoying
        /*if (Math.abs(xComp) >= 8388608 || Math.abs(zComp) >= 8388608){
            throw new RuntimeException("FUCK! INVALID COMP VALUES [" + xComp + ", " + zComp + "]");
        }*/
        /*if (negX) xComp--;
        if (negZ) zComp--;*/ //This was the right idea to fix a prior issue
        // but didn't quite work iirc

        //System.out.println("x, z comp pre-shift [THESE VALUES ARE ABSOLUTE]: x " + xComp + ", z " + zComp);
        //I should clean up this stuff, no real need to keep this on its own line...
        xComp <<= 40;
        //Clear mask removes any bits after the 40th one,
        // just in case it's negative and those are filled with 1's
        zComp = (zComp << 16) & clearMask;

        //Debugging stuff...
        /*System.out.println("xComp binary[" + BitwiseDataHelper.parseLongToBinary(xComp) + "]");
        System.out.println("zComp binary[" + BitwiseDataHelper.parseLongToBinary(zComp) + "]");*/

        if (negX) {
            xComp |= finalBitMask;
            //System.out.println("x negative fix[" + BitwiseDataHelper.parseLongToBinary(xComp) + "]");
        }
        if (negZ) {
            zComp |= fortiethBitMask;
            //System.out.println("z negative fix[" + BitwiseDataHelper.parseLongToBinary(zComp) + "]");
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

    @Override
    public String toString() {
        return "GreedyChunk["+chunkID+"]{" +
                "relative" + relative +
                ", \n    maps[" + Arrays.toString(maps) +
                "]}";
    }
    public String toString(boolean binaryID){
        if (binaryID){
            return "GreedyChunk["+BitwiseDataHelper.parseLongToBinary(chunkID)+"]{" +
                    "relative" + relative +
                    "\nmaps[" + Arrays.toString(maps) + "]}";
        } else return toString();
    }
}

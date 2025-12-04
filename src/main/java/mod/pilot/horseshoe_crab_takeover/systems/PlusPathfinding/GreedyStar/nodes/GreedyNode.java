package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

/**
 * The lowest level of the GreedyStar pathfinder system,
 * this represents a 3d quad of traversable terrain.
 * <p>
 *     IMPORTANT VARIABLES:
 * </p>
 * {@link GreedyNode#nodeID} is the ID of the node for this level.
 * Each {@link mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap}
 * contains its own subset of GNodes (and therefor contextual ID's), so in order to get the
 * ID for a given GNode outside the bounds of its respective GMap, they must be combined.
 * Furthermore, it can be combined with the encompassing
 * {@link mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk}
 * to get the {@code GlobalID}, which allows you to locate one specific node from
 * an entirely external view. The first 3 bits (formatted as so: [00000;000])
 * are reserved for directional information within the context of
 * {@link GreedyMap.MapContext}
 * and must be kept zeroed outside of those contexts.
 * <p></p>
 */
public class GreedyNode extends QuadSpace {
    public static boolean CRASH_ON_INVALID_ID = true;

    public static GreedyNode buildSkeleton(int minorX, int minorY, int minorZ){
        byte one = 1, negOne = -1;
        return new GreedyNode(negOne, minorX, minorY, minorZ, one, one, one, true);
    }
    public GreedyNode(byte nodeID, int minorX, int minorY, int minorZ, byte x, byte y, byte z){
        this(nodeID, minorX, minorY, minorZ, x, y, z, false);
    }
    public GreedyNode(byte nodeID, int minorX, int minorY, int minorZ, byte x, byte y, byte z,
                      boolean suppressInvalidID){
        super(minorX, minorY, minorZ, x, y, z);
        this.nodeID = nodeID;
        if (!suppressInvalidID && !validateOrAttemptRepair()) {
            String err1 = String.format("[GREEDY NODE] WARNING! Incorrectly formatted context ID [%s] located in constructor! The first 3 bits have to be 0, those contain directional data only used in GreedyMap.NodeWrapper and must be left empty.", BitwiseDataHelper.parseByteToBinary(nodeID));
            String info = String.format("[GREEDY NODE-- INFO] Attempted to repair node context ID; got value [%s]. This might be a corrupted ID, and may overlap with a preexisting node. Please reevaluate the construction of all GNodes to ensure that invalid IDs are never used as arguments. If you know what you are doing, pass in true for the suppressInvalidID constructor argument.", BitwiseDataHelper.parseByteToBinary(this.nodeID));
            if (CRASH_ON_INVALID_ID) throw new RuntimeException(err1 + "\n--ERROR) CRASH_ON_INVALID_ID is enabled, so the invalid ID caused a crash.");
            else {
                System.err.println(err1);
                System.err.println(info);
            }
        }
    }

    public void flagInvalid(){nodeID = -1;}
    public void assignID(byte ID){nodeID = ID;}
    public byte nodeID;
    public boolean validateOrAttemptRepair(){
        if (nodeID == -1 || containsDirectionalInfo(nodeID)){
            nodeID = attemptRepair(nodeID);
            return false;
        }
        return true;
    }
    //7 is 00000111 in binary
    public static byte attemptRepair(byte contextID){
        return (byte)(contextID & ~7);
    }
    public static boolean containsDirectionalInfo(byte contextID){
        return (contextID & 7) != 0;
    }
    /**/

    public int compressSize(){
        int compr = sizeZ;
        compr = (compr << 8) | sizeY;
        compr = (compr << 8) | sizeX;
        return compr;
    }

    public static byte decompressX(int compressed){
        return (byte)(compressed >>> 16);
    }
    public static byte decompressY(int compressed){
        return (byte)(compressed << 16 >>> 8);
    }
    public static byte decompressZ(int compressed){
        return (byte)(compressed << 24 >>> 24);
    }

    public void unpackSize(int compressed){
        sizeX = decompressX(compressed); sizeY = decompressY(compressed); sizeZ = decompressZ(compressed);
    }

    /**
     * Attempts to retrieve a GNode from the supplied Global ID.
     * Will return null if any of the IDs are invalid or if there is no element with the given ID
     * (for any step)
     * @param globalID The Global ID of the GNode to retrieve
     * @return The GNode with the given ID within the encompassing GMap and GChunk
     * as defined by the Global ID, or null if any retrieval step fails.
     */
    public static @Nullable GreedyNode retrieveFromGlobalID(long globalID){
        //Only check RAM and File cache, we don't want to make a new chunk if there isn't one
        GreedyChunk gChunk = GreedyWorld.greedyWorld_DEFAULT.retrieveOnly(globalID);
        if (gChunk == null) return null; //Womp, no GChunks exist for that ID
        byte mapID = GreedyWorld.isolateMapID(globalID); //Getting the GMap from the I.D....
        GreedyMap gMap = gChunk.getMap(mapID); /**/
        if (gMap == null) return null; //Womp, no GMap exists for that ID within that GChunk
        byte nodeID = GreedyWorld.isolateNodeID(globalID); //Getting the desired GNode...
        return gMap.nodeFromID(nodeID);
        //Return regardless of if it exists, the method is @Nullable
    }

    public QuadSpace buildEquvilantQuadSpace(){
        return new QuadSpace(minorX, minorY, minorZ, sizeX, sizeY, sizeZ);
    }

    @Override
    public String toString() {
        return "GreedyNode[" + nodeID + "]{" +
                "minor[" + minorZ + ", " + minorY + ", " + minorX +
                "], size[" + sizeZ + ", " + sizeY + ", " + sizeX + "]}";
    }

    public static class Blueprint{
        private static final byte zero = 0;
        public Blueprint(Vector3i minor){
            this(minor, zero, zero, zero);
        }
        public Blueprint(Vector3i minor, byte x, byte y, byte z){
            this.minor = minor;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        Vector3i minor;
        public byte x, y, z;
        public void extend(byte x, byte y, byte z){
            this.x += x;
            this.y += y;
            this.z += z;
        }
        public void stepX(){x++;}
        public void stepY(){y++;}
        public void stepZ(){z++;}
        public void shiftMinor(int x, int y, int z){minor.add(x, y, z);}

        public QuadSpace quadSpace(){return new QuadSpace(minor, x, y, z);}

        public GreedyNode build(GreedyMap gMap){
            return new GreedyNode(gMap.newNodeID(), minor.x, minor.y, minor.z, x, y, z);
        }
    }
}


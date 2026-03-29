package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyMap;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

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
 * are reserved for directional information within the context of relatives
 * and must be kept zeroed outside of those contexts.
 * <p></p>
 */
public class GreedyNode extends QuadSpace {
    public static boolean CRASH_ON_INVALID_ID = true;

    public static GreedyNode buildSkeleton(int minorX, int minorY, int minorZ){
        byte negOne = -1;
        return new GreedyNode(negOne, minorX, minorY, minorZ, 1, 1, 1, true);
    }
    public GreedyNode(byte nodeID, int minorX, int minorY, int minorZ, int sizeX, int sizeY, int sizeZ){
        this(nodeID, minorX, minorY, minorZ, sizeX, sizeY, sizeZ, false);
    }
    public GreedyNode(byte nodeID, int minorX, int minorY, int minorZ, int sizeX, int sizeY, int sizeZ,
                      boolean suppressInvalidID){
        super(minorX, minorY, minorZ, sizeX, sizeY, sizeZ);
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

        this.relativeIDs = new byte[this.size = this.occupied = 0];
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


    //RELATIVES
    public byte[] relativeIDs;
    public int size, occupied;

    public void addElementByDirection(byte element, Direction direction){
        insertElement(computeElementID(element, direction),
                iterateUntilValidIndex(direction));
    }
    public void addElementByID(byte isoNodeID, byte ID){
        insertElement((byte)(isoNodeID | ID), iterateUntilValidIndex(ID));
    }
    public void addElementByComputedID(byte element){
        insertElement(element, iterateUntilValidIndex(element));
    }

    public void insertElement(byte id, int index){
        System.out.println("INSERTING [" + BitwiseDataHelper.parseByteToBinary(id) + " INTO INDEX " + index + ", there are already " + size + " nodes here");
        if (index >= size){
            growArray((size + 1) - index);
        }
        else if (index < occupied) {
            growArray(1);
            System.arraycopy(relativeIDs, index, relativeIDs, index + 1, size - (index + 1));
        }
        relativeIDs[index] = id;
        this.occupied++;
    }
    public void removeElement(int index){
        if (index < 0 || index > size) return;
        byte[] newArray = new byte[size];
        System.arraycopy(relativeIDs, 0, newArray, 0, index);
        System.arraycopy(relativeIDs, index+1, newArray, index, size - index);
        this.occupied--;
        relativeIDs = newArray;
    }

    public byte[] getAllIDsOfDirection(Direction direction){
        return getAllIDsOfDirection(idPrependByDirection(direction));
    }
    public byte[] getAllIDsOfDirection(byte id_pre){
        if (id_pre > 6) id_pre = isolateDirection(id_pre);
        byte[] toReturn = new byte[size];
        int count = 0;
        for (int i = 0; i < occupied; i++){
            byte cDirectionID = isolateDirection(relativeIDs[i]);
            if (cDirectionID == id_pre) toReturn[count++] = relativeIDs[i];
            else if (cDirectionID > id_pre) break;
        }
        return capArray(toReturn, count);
    }

    public byte computeElementID(byte id, Direction direction){
        return (byte)(id | idPrependByDirection(direction));
    }
    public int amountForDirection(Direction direction){
        return amountForDirection(idPrependByDirection(direction));
    }
    public int amountForDirection(byte id_pre){
        int count = 0;
        byte directionID;
        for (byte id : relativeIDs){
            directionID = isolateDirection(id);
            if (directionID >= id_pre) {
                if (directionID == id_pre) count++;
                else break;
            }
        }
        if (count > 63) System.err.printf("WARNING! Amount of Nodes within NodeMap %s for the direction %s exceeded the allocated amount supported by the ID system! [%h]%n", this, directionFromId(id_pre), 63);
        return count;
    }
    public int iterateUntilValidIndex(Direction direction){
        return iterateUntilValidIndex(idPrependByDirection(direction));
    }
    public int iterateUntilValidIndex(byte id){
        if (occupied == 0) return 0;
        if (id > 6){
            id = isolateDirection(id);
        }
        System.out.println("direction is " + BitwiseDataHelper.parseByteToBinary(id));
        int index = 0; //current index
        byte cID; //The current ID of the element
        do{
            cID = relativeIDs[index]; //Cycle to the next id and index
            System.out.println(BitwiseDataHelper.parseByteToBinary(cID));
        } while (
            //If the index isn't out of bounds
            //AND the direction prepend of the current ID is equal or less than
            //the target prepend, continue
                ++index < occupied && isolateDirection(cID) <= id
        );
        //Stops upon reaching the end of the array
        // OR finding the index immediately after the last element
        // that shares the same directional prepend
        System.out.println("INDEX " + index);
        return index;
        //Returns the index, this is where we want to place the next object
        //Will return either the next index after a grow if the map does NOT
        // currently contain a direction with a value greater than the desired one,
        // OR the index immediately after the last element with a smaller (or equal)
        // directional index value to the new element
    }

    private void growArray(int amount){
        int newSize = size + amount;
        byte[] newIDs = new byte[newSize];
        System.arraycopy(relativeIDs, 0, newIDs, 0, size);
        relativeIDs = newIDs;
        size = newSize;
    }
    private static byte[] capArray(byte[] array, int size){
        if (array.length <= size) return array;
        byte[] newArray = new byte[size];
        System.arraycopy(array, 0, newArray, 0, size);
        return newArray;
    }

    public static byte ID_MASK = 7; //00000111

    public static byte ID_DOWN = 1; //0000001
    public static byte ID_UP = 2; //00000010
    public static byte ID_NORTH = 3; //00000011
    public static byte ID_SOUTH = 4; //00000100
    public static byte ID_WEST = 5; //00000101
    public static byte ID_EAST = 6; //00000110
    public static byte idPrependByDirection(Direction d){
        return switch(d){
            case DOWN -> ID_DOWN;
            case UP -> ID_UP;
            case NORTH -> ID_NORTH;
            case SOUTH -> ID_SOUTH;
            case WEST -> ID_WEST;
            case EAST -> ID_EAST;
        };
    }
    public static Direction directionFromId(byte id){
        if (id > 5) id = isolateDirection(id);
        return switch(id){
            case 1 -> Direction.DOWN;
            case 2 -> Direction.UP;
            case 3 -> Direction.NORTH;
            case 4 -> Direction.SOUTH;
            case 5 -> Direction.WEST;
            case 6 -> Direction.EAST;
            default -> null;
        };
    }
    public static byte oppositeDirection(byte id){
        if (id % 2 == 0) return --id;
        else return ++id;
    }
    public static byte isolateDirection(byte id){
        byte asf =(byte)(id & ID_MASK);
        System.out.println("Isolate dir " + BitwiseDataHelper.parseByteToBinary(id) + " iso to " + BitwiseDataHelper.parseByteToBinary(asf));
        return asf; //remove all of this and uncomment return after debug
        //Takes the id and masks
        //return (byte)(id & ID_MASK);
    }
    public static byte isolateID(byte id){
        return (byte)(id & ~ID_MASK);
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
        return gMap.nodeByID(nodeID);
        //Return regardless of if it exists, the method is @Nullable
    }

    public QuadSpace buildEquvilantQuadSpace(){
        return new QuadSpace(minorX, minorY, minorZ, sizeX, sizeY, sizeZ);
    }

    @Override
    public String toString() {
        return "GreedyNode[" + nodeID + "]{" +
                "minor[" + minorX + ", " + minorY + ", " + minorZ +
                "], size[" + sizeX + ", " + sizeY + ", " + sizeZ + "]}";
    }
}


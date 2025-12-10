package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class GreedyMap {
    public static int DEFAULT_MapExtensionRange = 4;
    public int MapExtensionRange;

    public GreedyMap(byte mapID){
        this(new MapContext.Container(), mapID);
    }
    public GreedyMap(MapContext.Container nodes, byte mapID){
        this.nodes = nodes;
        this.mapID = mapID;
        MapExtensionRange = DEFAULT_MapExtensionRange;
        computeBound();
    }

    public final byte mapID;
    public int computeMapLevelID(byte nodeID){
        if (GreedyNode.containsDirectionalInfo(nodeID)){
            return (mapID << 8) | (nodeID & ~7);
        } else return (mapID << 8) | nodeID;
    }
    public byte newNodeID(){
        byte[] existingIDs = new byte[nodes.size];
        //Grab all existing Node IDs and push the ID to the edge for easier comparison
        for (int i = 0; i < nodes.size; i++) existingIDs[i] = (byte)(nodes.getNode(i).nodeID >>> 3);

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
            return (byte)(id << 3); //Otherwise, this is our next ID!
            //Make sure to shift the ID over 3 bits

            //We can't just return the next ID based off of the size of the Container
            // because if nodes are removed it won't be 1:1
            // E.G. (if node ID [00101;000] is removed, its ID is valid for another node,
            // but if there's more than 5 nodes in the container, the computed ID would assume
            // [00101;000] is already taken, and the computed value might overlap with another node
        }
    }

    public QuadSpace MapBound;
    /**
     * Creates the {@link QuadSpace} bound of this Greedy Map
     * by finding the extremes of all the Greedy Nodes
     */
    public void computeBound(){
        int nodeCount = nodes.size; //How many nodes we are working with
        if (nodeCount == 0) MapBound = QuadSpace.empty(); //If we don't have any nodes, the bounds are invalid
        else if (nodeCount == 1){
            //If we only have 1 node, create a QuadSpace bound with the same dimensions as the node
            MapBound = nodes.getNode(0).buildEquvilantQuadSpace();
        }
        else if (nodeCount == 2) {
            //If we have two nodes, find the extremes
            GreedyNode node1 = nodes.getNode(0), node2 = nodes.getNode(1);
            MapBound = new QuadSpace(
                    Math.min(node1.minorX, node2.minorX),
                    Math.min(node1.minorY, node2.minorY),
                    Math.min(node1.minorZ, node2.minorZ),
                    Math.max(node1.sizeX, node2.sizeX),
                    Math.max(node1.sizeY, node2.sizeY),
                    Math.max(node1.sizeZ, node2.sizeZ));
        }
        else {
            //Otherwise, loop through all nodes and locate the extremes
            int xCorner, yCorner, zCorner, xMajor, yMajor, zMajor;
            xCorner = yCorner = zCorner = Integer.MAX_VALUE;
            xMajor = yMajor = zMajor = Integer.MIN_VALUE;
            for (GreedyNode node : nodes){
                xCorner = Math.min(xCorner, node.minorX); //Find the smallest minor
                yCorner = Math.min(yCorner, node.minorY); /**/
                zCorner = Math.min(zCorner, node.minorZ); /**/

                xMajor = Math.max(xMajor, node.minorX + node.sizeX); //Find the biggest major
                yMajor = Math.max(yMajor, node.minorY + node.sizeY); /**/
                zMajor = Math.max(zMajor, node.minorZ + node.sizeZ); /**/
            }
            //Combine into a QuadSpace of the extremes
            MapBound = new QuadSpace(xCorner, yCorner, zCorner,
                    xMajor - xCorner, yMajor - yCorner, zMajor - zCorner);
        }
    }

    public MapContext.Container nodes;
    public void addNode(GreedyNode gNode){
        gNode.assignID(newNodeID());
        MapContext mapC = wrap(gNode);
        nodes.addContext(mapC);
        //ToDo: Add system to evaluate all other contained GNodes to see if they are
        // "sisters" to the newly added GNode, then update the MapContexts as needed
        // (will need to add a feature to QuadSpaces to be able to look for touching edges or faces...)
    }

    public void removeNode(GreedyNode gNode){
        int index = 0;
        boolean flag = false;
        for (; index < nodes.size; index++){
            if (nodes.get(index).contains(gNode)){
                flag = true; break;
            }
        }
        if (flag) nodes.removeAtIndex(index);
    }
    public void removeNode(int index){ nodes.removeAtIndex(index); }
    public void removeNode(MapContext context){ removeNode(context, false); }
    public void removeNode(MapContext context, boolean fuzzy){
        nodes.removeContext(context, fuzzy);
    }
    public @Nullable MapContext contextFromID(byte nodeID){
        for (@NotNull Iterator<MapContext> it = nodes.contextIterator(); it.hasNext(); ) {
            MapContext context = it.next();
            if (context.node.nodeID == nodeID) return context;
        }
        return null;
    }
    public @Nullable GreedyNode nodeFromID(byte nodeID){
        for (GreedyNode node : nodes){
            if (node.nodeID == nodeID) return node;
        }
        return null;
    }
    public MapContext wrap(GreedyNode node){
        return new MapContext(node);
    }

    public static GreedyMap retrieveFromGlobalID(long globalID){

        //Only check RAM and File cache, we don't want to make a new chunk if there isn't one
        GreedyChunk gChunk = GreedyWorld.greedyWorld_DEFAULT.retrieveOnly(globalID);
        if (gChunk == null) return null; //Womp, no GChunks exist for that ID
        byte mapID = GreedyWorld.isolateMapID(globalID); //Getting the GMap from the I.D....
        return gChunk.getMap(mapID);
        //Return regardless of if it exists, the method is @Nullable
    }

    public static final class MapContext {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MapContext that)) return false;
            return size == that.size && Objects.equals(node, that.node) && Objects.deepEquals(relativeIDs, that.relativeIDs);
        }
        public boolean fuzzyEquals(MapContext context){
            return node.equals(context.node);
        }
        public boolean contains(GreedyNode gNode){
            return node.equals(gNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, Arrays.hashCode(relativeIDs), size);
        }

        public GreedyNode node;
        public byte[] relativeIDs;
        public int size;

        public MapContext(GreedyNode node, byte[] relativeIDs){
            this.node = node;
            this.relativeIDs = relativeIDs;
            size = relativeIDs.length;
        }
        public MapContext(GreedyNode node, int initCapacity){
            this.node = node;
            this.relativeIDs = new byte[size = initCapacity];
        }
        public MapContext(GreedyNode node){
            this(node, 0);
        }

        public void addElementByDirection(byte element, Direction direction){
            insertElement(computeElementID(element, direction),
                    iterateUntilValidIndex(direction));
        }

        public void insertElement(byte id, int index){
            if (index >= size){
                growArray(size - index);
            }
            else {
                growArray(1);
                System.arraycopy(relativeIDs, index, relativeIDs, index + 1, size - index);
            }
            relativeIDs[index] = id;
        }

        public byte[] getAllIDsOfDirection(Direction direction){
            byte id_pre = idPrependByDirection(direction);
            byte[] toReturn = new byte[size];
            int count = 0;
            for (int i = 0; i < size; i++){
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
            byte id_pre = idPrependByDirection(direction);
            int count = 0;
            byte directionID;
            for (byte id : relativeIDs){
                directionID = isolateDirection(id);
                if (directionID >= id_pre) {
                    if (directionID == id_pre) count++;
                    else break;
                }
            }
            if (count > 63) System.err.printf("WARNING! Amount of Nodes within NodeMap %s for the direction %s exceeded the allocated amount supported by the ID system! [%h]%n", this, direction, 63);
            return count;
        }
        @SuppressWarnings("ExtractMethodRecommender")
        public int iterateUntilValidIndex(Direction direction){
            //Get the target prepend for this direction:
            //[0,1,2,3,4,5] for [Down,Up,North,South,West,East] respectively
            byte id_pre = idPrependByDirection(direction);
            int index = -1; //current index, set to -1 the first cycle doesn't skip index 0
            byte cID; //The current ID of the element
            do{
                cID = relativeIDs[++index]; //Cycle to the next id and index
            } while (
                //If the index isn't out of bounds
                //AND the direction prepend of the current ID is equal or less than
                //the target prepend, continue
                    index < size && isolateDirection(cID) <= id_pre
            );
            //Stops upon reaching the end of the array
            //OR finding the index immediately after the last element that
            //shares the same directional prepend
            return index; //Returns the index, this is where we want to place the next object
            //Will return either the next index after a grow if the map does NOT
            //currently contain a direction with a value greater than the desired one,
            //OR the index immediately after the last element with a smaller (or equal)
            //directional index value to the new element
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
        public static byte isolateDirection(byte id){
            //Takes the id and masks
            return (byte)(id & ID_MASK);
        }
        public static byte isolateID(byte id){
            return (byte)(id & ~ID_MASK);
        }

        public static class Container implements Iterable<GreedyNode>{
            public Container(){
                contexts = new MapContext[0];
                size = 0;
            }
            protected MapContext[] contexts;
            public int size;

            public MapContext get(int index){
                return contexts[index];
            }
            public GreedyNode getNode(int index){
                return get(index).node;
            }

            public void addContext(MapContext node){
                growArray(1);
                contexts[size - 1] = node;
            }
            public void addContexts(MapContext... toAdd){
                int count = toAdd.length;
                int index = size;
                growArray(count);
                System.arraycopy(toAdd, 0, contexts, index, count);
            }
            public void insertElement(MapContext element, int index){
                if (index >= size){
                    growArray(size - index);
                }
                else {
                    growArray(1);
                    System.arraycopy(contexts, index, contexts, index + 1, size - index);
                }
                contexts[index] = element;
            }

            public void removeContext(MapContext context){removeContext(context, false);}
            public void removeContext(MapContext context, boolean fuzzy){
                removeAtIndex(findIndex(context, fuzzy));
            }
            public void removeAtIndex(int index){
                if (index == -1) return;
                contexts = DataHelper.Arrays.removeAndDecrement(contexts, index);
                size--;
            }

            public int findIndex(MapContext context){ return findIndex(context, false); }
            public int findIndex(MapContext context, boolean fuzzy){
                if (fuzzy) for (int index = 0; index < size; index++){
                        if (contexts[index].fuzzyEquals(context)) return index;
                } else for (int index = 0; index < size; index++){
                    if (contexts[index].equals(context)) return index;
                }
                return -1;
            }

            private void growArray(int amount){
                int newSize = size + amount;
                MapContext[] newContexts = new MapContext[newSize];
                System.arraycopy(contexts, 0, newContexts, 0, size);
                this.contexts = newContexts;
                this.size = newSize;
            }

            private final NodeIterator nIterator = new NodeIterator();
            @Override
            public @NotNull Iterator<GreedyNode> iterator() {
                return nIterator.reset();
            }
            private final ContextIterator cIterator = new ContextIterator();
            public @NotNull Iterator<MapContext> contextIterator() {
                return cIterator.reset();
            }

            private class NodeIterator implements Iterator<GreedyNode>{
                public NodeIterator(){reset();}
                public NodeIterator reset(){
                    wrapper = contexts;
                    count = wrapper.length;
                    index = 0;
                    return this;
                }
                public MapContext[] wrapper;
                private int count;
                private int index;

                @Override public boolean hasNext() {return index + 1 < count;}
                @Override public GreedyNode next() {
                    return wrapper[++index].node;
                }
            }
            private class ContextIterator implements Iterator<MapContext>{
                public ContextIterator(){reset();}
                public ContextIterator reset(){
                    elements = contexts;
                    count = elements.length;
                    index = 0;
                    return this;
                }
                public MapContext[] elements;
                private int count;
                private int index;

                @Override public boolean hasNext() {return index + 1 < count;}
                @Override public MapContext next() {
                    return elements[++index];
                }
            }
        }
    }

    /**
     * DEPRECATED!!
     * Was the first generation of what turned into {@link MapContext}
     * I didn't know what I was doing when making it so it was useless
     * and was just recycled.
     * <p></p>
     * But I didnt want to delete it so it's left here as an artifact of the past
     */
    @SuppressWarnings("unchecked")
    @Deprecated //It's cool, but not useful here...
    private final class NodeRelativeMap {
        public NodeRelativeMap(GreedyNode node, GreedyNode[] elements, Byte[] IDs){
            this.relative = node;
            int elementCount = elements.length, idCount = IDs.length;
            if (elementCount != idCount){
                if (idCount > elementCount){
                    this.elements = elements;
                    System.arraycopy(capArray(IDs, elementCount), 0,
                            this.IDs = new byte[elementCount], 0, size = elementCount);
                } else {
                    this.elements = capArray(elements, idCount);
                    System.arraycopy(IDs, 0,
                            this.IDs = new byte[idCount], 0, size = idCount);
                }
            } else {
                this.elements = elements;
                System.arraycopy(IDs, 0, this.IDs = new byte[idCount], 0,
                        size = idCount);
            }
        }
        public NodeRelativeMap(GreedyNode node, int initCapacity){
            relative = node;
            elements = (GreedyNode[])new mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode[initCapacity];
            IDs = new byte[initCapacity];
            size = initCapacity;
        }
        public GreedyNode relative;
        public GreedyNode[] elements;
        public byte[] IDs;
        public int size;

        public void addElementByDirection(GreedyNode element, Direction direction){
            byte id = computeElementID(direction);
            if (id == -1){
                System.err.printf("[NODE FAMILY TREE] WARNING! Attempted to add an element but the computed ID came back as invalid! Likely, the amount of elements for the given direction exceeded the max [%h]. Discarding addition...%n", 63);
                System.err.printf("[NODE FAMILY TREE-- INFO] Failed addition: id[%h], element[%s], direction[%s]; Family tree[%s]", id, element, direction, this);
                return;
            }
            insertElement(element, id, iterateUntilValidIndex(direction));
        }

        private void insertElement(GreedyNode element, byte id, int index){
            if (index >= size){
                growArrays(size - index);
                elements[index] = element;
                IDs[index] = id;
            }
            else if (index == 0){
                int newSize = size + 1;
                GreedyNode[] newElements = (GreedyNode[])new mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode[newSize];
                byte[] newIDs = new byte[newSize];
                System.arraycopy(elements, 0, newElements, 1, size);
                System.arraycopy(IDs, 0, newIDs, 1, size);
                newElements[0] = element;
                newIDs[0] = id;
                elements = newElements;
                IDs = newIDs;
                size = newSize;
            }
            else {
                growArrays(1);
                System.arraycopy(elements, index, elements, index + 1, size - index);
                System.arraycopy(IDs, index, IDs, index + 1, size - index);
                elements[index] = element;
                IDs[index] = id;
            }
        }

        public GreedyNode[] getAllElementsOfDirection(Direction direction){
            byte id_pre = idPrependByDirection(direction);
            GreedyNode[] toReturn = (GreedyNode[])new mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode[size];
            int count = 0;
            for (int i = 0; i < size; i++){
                byte cDirectionID = isolateDirection(IDs[i]);
                if (cDirectionID == id_pre) toReturn[count++] = elements[i];
                else if (cDirectionID > id_pre) break;
            }
            return capArray(toReturn, count);
        }

        public byte computeElementID(Direction direction){
            int amount = amountForDirection(direction);
            if (amount > 63) return -1;
            return (byte)(idPrependByDirection(direction) | (amount << 3));
        }
        public int amountForDirection(Direction direction){
            byte id_pre = idPrependByDirection(direction);
            int count = 0;
            byte directionID;
            for (byte id : IDs){
                directionID = isolateDirection(id);
                if (directionID >= id_pre) {
                    if (directionID == id_pre) count++;
                    else break;
                }
            }
            if (count > 63) System.err.printf("WARNING! Amount of Nodes within NodeMap %s for the direction %s exceeded the allocated amount supported by the ID system! [%h]%n", this, direction, 63);
            return count;
        }
        @SuppressWarnings("ExtractMethodRecommender")
        public int iterateUntilValidIndex(Direction direction){
            //Get the target prepend for this direction:
            //[0,1,2,3,4,5] for [Down,Up,North,South,West,East] respectively
            byte id_pre = idPrependByDirection(direction);
            int index = -1; //current index, set to -1 the first cycle doesn't skip index 0
            byte cID; //The current ID of the element
            do{
                cID = IDs[++index]; //Cycle to the next id and index
            } while (
                //If the index isn't out of bounds
                //AND the direction prepend of the current ID is equal or less than
                //the target prepend, continue
                    index < size && isolateDirection(cID) <= id_pre
            );
            //Stops upon reaching the end of the array
            //OR finding the index immediately after the last element that
            //shares the same directional prepend
            return index; //Returns the index, this is where we want to place the next object
            //Will return either the next index after a grow if the map does NOT
            //currently contain a direction with a value greater than the desired one,
            //OR the index immediately after the last element with a smaller (or equal)
            //directional index value to the new element
        }

        private void growArrays(int additional){
            int newSize = size + additional;
            GreedyNode[] newElements = (GreedyNode[])new mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode[newSize];
            byte[] newIDs = new byte[newSize];
            System.arraycopy(elements, 0, newElements, 0, size);
            System.arraycopy(IDs, 0, newIDs, 0, size);
            elements = newElements;
            IDs = newIDs;
            size = newSize;
        }
        private static <T>T[] capArray(T[] array, int size){
            if (array.length <= size) return array;
            T[] newArray = (T[])new Object[size];
            System.arraycopy(array, 0, newArray, 0, size);
            return newArray;
        }

        public static byte ID_MASK = 7; //00000111

        public static byte ID_DOWN = 0; //0000000
        public static byte ID_UP = 1; //00000001
        public static byte ID_NORTH = 2; //00000010
        public static byte ID_SOUTH = 3; //00000011
        public static byte ID_WEST = 4; //00000100
        public static byte ID_EAST = 5; //00000101
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
        private byte isolateDirection(byte id){
            //Takes the id and masks
            return (byte)(id & ID_MASK);
        }

        //Unused but I already made it so I don't want to remove it :/
        public void setToSize(int newSize){
            if (newSize < size){
                GreedyNode[] newElements = (GreedyNode[])new mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode[newSize];
                byte[] newIDs = new byte[newSize];
                System.arraycopy(elements, 0, newElements, 0, newSize);
                System.arraycopy(IDs, 0, newIDs, 0, newSize);
                elements = newElements;
                IDs = newIDs;
                size = newSize;
            }
            else if (newSize != size) growArrays(size - newSize);
        }
    }
}

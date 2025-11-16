package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.Arrays;
import java.util.Iterator;

public class GreedyMap<G extends GreedyNode> {
    public GreedyMap(MapContext.Container nodes, byte mapID){
        this.nodes = nodes;
        this.mapID = mapID;
    }

    public final byte mapID;
    public int computeMapLevelID(byte nodeID){
        if (GreedyNode.validateID(nodeID)){
            return (mapID << 8) | nodeID;
        } else return -1;
    }

    public QuadSpace MapBound;
    /**
     * Creates the {@link QuadSpace} bound of this Greedy Map
     * by finding the extremes of all the Greedy Nodes
     */
    public void computeBound(){
        int nodeCount = nodes.size; //How many nodes we are working with
        if (nodeCount == 0) MapBound = QuadSpace.INVALID; //If we don't have any nodes, the bounds are invalid
        else if (nodeCount == 1){
            //If we only have 1 node, create a QuadSpace bound with the same dimensions as the node
            GreedyNode node = nodes.getNode(0);
            MapBound = new QuadSpace(node.cornerMinor, node.x, node.y, node.z);
        }
        else if (nodeCount == 2) {
            //If we have two nodes, find the extremes
            GreedyNode node1 = nodes.getNode(0), node2 = nodes.getNode(1);
            Vector3i minor1 = node1.cornerMinor, minor2 = node2.cornerMinor;
            Vector3i minor = new Vector3i(Math.min(minor1.x, minor2.x),
                    Math.min(minor1.y, minor2.y),
                    Math.min(minor1.z, minor2.z));
            MapBound = new QuadSpace(minor, Math.min(node1.x, node2.x),
                    Math.min(node1.y, node2.y), Math.min(node1.z, node2.z));
        }
        else {
            //Otherwise, loop through all nodes and locate the extremes
            int xCorner, yCorner, zCorner, xMajor, yMajor, zMajor;
            xCorner = yCorner = zCorner = Integer.MAX_VALUE;
            xMajor = yMajor = zMajor = Integer.MIN_VALUE;
            for (GreedyNode node : nodes){
                Vector3i minor = node.cornerMinor;
                xCorner = Math.min(xCorner, minor.x); /**/
                yCorner = Math.min(yCorner, minor.y); /**/
                zCorner = Math.min(zCorner, minor.z); //Find the smallest minor

                xMajor = Math.max(xMajor, minor.x + node.x); /**/
                yMajor = Math.max(yMajor, minor.y + node.y); /**/
                zMajor = Math.max(zMajor, minor.z + node.z); //Find the biggest major
            }
            //Combine into a QuadSpace of the extremes
            MapBound = new QuadSpace(new Vector3i(xCorner, yCorner, zCorner),
                    xMajor - xCorner, yMajor - yCorner, zMajor - zCorner);
        }
    }

    public MapContext.Container nodes;
    public @Nullable MapContext contextFromID(byte nodeID){
        for (@NotNull Iterator<MapContext> it = nodes.contextIterator(); it.hasNext(); ) {
            MapContext context = it.next();
            if (context.node.nodeID == nodeID) return context;
        }
        return null;
    }
    public @Nullable G nodeFromID(byte nodeID){
        for (G node : nodes){
            if (node.nodeID == nodeID) return node;
        }
        return null;
    }
    public MapContext wrap(G node){
        return new MapContext(node);
    }

    public final class MapContext {
        public G node;
        public byte[] relativeIDs;
        public int size;

        public MapContext(G node, byte[] relativeIDs){
            this.node = node;
            this.relativeIDs = relativeIDs;
            size = relativeIDs.length;
        }
        public MapContext(G node, int initCapacity){
            this.node = node;
            this.relativeIDs = new byte[size = initCapacity];
        }
        public MapContext(G node){
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

        @SuppressWarnings("unchecked")
        public class Container implements Iterable<G>{
            protected Object[] contexts;
            public int size;

            public MapContext get(int index){
                return (MapContext) contexts[index];
            }
            public G getNode(int index){
                return get(index).node;
            }

            public void addContext(MapContext node){
                growArray(1);
                contexts[size - 1] = node;
            }
            public void addContexts(Object... toAdd){
                int count = toAdd.length;
                int index = size;
                growArray(count);
                if (Arrays.stream(toAdd).allMatch(
                        (obj) -> obj.getClass().equals(MapContext.class))) {
                    System.arraycopy(toAdd, 0, contexts, index, count);
                } else {
                    System.err.println("[MAP CONTEXT] WARNING! Attempted to add a list of MapContexts to a container but at least one of the supplied objects were NOT of the MapContext class! Ensure ALL objects being added are MapContexts. Discarding addition attempt...");
                    System.err.printf("[MAP CONTEXT-- INFO] Objects to add[%s]%n", toAdd);
                }
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

            private void growArray(int amount){
                int newSize = size + amount;
                Object[] newContexts = new Object[newSize];
                System.arraycopy(contexts, 0, newContexts, 0, size);
                this.contexts = newContexts;
                size = newSize;
            }

            private final NodeIterator nIterator = new NodeIterator();
            @Override
            public @NotNull Iterator<G> iterator() {
                return nIterator.reset();
            }
            private final ContextIterator cIterator = new ContextIterator();
            public @NotNull Iterator<MapContext> contextIterator() {
                return cIterator.reset();
            }

            private class NodeIterator implements Iterator<G>{
                public NodeIterator(){reset();}
                public NodeIterator reset(){
                    wrapper = contexts;
                    count = wrapper.length;
                    index = 0;
                    return this;
                }
                public Object[] wrapper;
                private int count;
                private int index;

                @Override public boolean hasNext() {return index + 1 < count;}
                @SuppressWarnings("unchecked")
                @Override public G next() {
                    return ((MapContext)wrapper[++index]).node;
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
                public Object[] elements;
                private int count;
                private int index;

                @Override public boolean hasNext() {return index + 1 < count;}
                @SuppressWarnings("unchecked")
                @Override public MapContext next() {
                    return (MapContext) elements[++index];
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Deprecated //It's cool, but not useful here...
    private final class NodeRelativeMap {
        public NodeRelativeMap(G node, G[] elements, Byte[] IDs){
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
        public NodeRelativeMap(G node, int initCapacity){
            relative = node;
            elements = (G[])new GreedyNode[initCapacity];
            IDs = new byte[initCapacity];
            size = initCapacity;
        }
        public G relative;
        public G[] elements;
        public byte[] IDs;
        public int size;

        public void addElementByDirection(G element, Direction direction){
            byte id = computeElementID(direction);
            if (id == -1){
                System.err.printf("[NODE FAMILY TREE] WARNING! Attempted to add an element but the computed ID came back as invalid! Likely, the amount of elements for the given direction exceeded the max [%h]. Discarding addition...%n", 63);
                System.err.printf("[NODE FAMILY TREE-- INFO] Failed addition: id[%h], element[%s], direction[%s]; Family tree[%s]", id, element, direction, this);
                return;
            }
            insertElement(element, id, iterateUntilValidIndex(direction));
        }

        private void insertElement(G element, byte id, int index){
            if (index >= size){
                growArrays(size - index);
                elements[index] = element;
                IDs[index] = id;
            }
            else if (index == 0){
                int newSize = size + 1;
                G[] newElements = (G[])new GreedyNode[newSize];
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

        public G[] getAllElementsOfDirection(Direction direction){
            byte id_pre = idPrependByDirection(direction);
            G[] toReturn = (G[])new GreedyNode[size];
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
            G[] newElements = (G[])new GreedyNode[newSize];
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
                G[] newElements = (G[])new GreedyNode[newSize];
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

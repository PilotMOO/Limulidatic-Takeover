package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class GreedyMap {
    public static int DEFAULT_MapExtensionRange = 12;
    public int mapExtensionRange;

    public GreedyMap(byte mapID){
        this(new GreedyNode[0], mapID);
    }
    public GreedyMap(GreedyNode[] nodes, byte mapID){
        this.nodes = nodes;
        this.mapID = mapID;
        mapExtensionRange = DEFAULT_MapExtensionRange;
        computeBound();
    }

    public final byte mapID;
    public int computeMapLevelID(byte nodeID){
        if (GreedyNode.containsDirectionalInfo(nodeID)){
            return (mapID << 8) | (nodeID & ~7);
        } else return (mapID << 8) | nodeID;
    }
    public byte newNodeID(){
        byte[] existingIDs = new byte[nodes.length];
        //Grab all existing Node IDs and push the ID to the edge for easier comparison
        for (int i = 0; i < nodes.length; i++) existingIDs[i] = (byte)(nodes[i].nodeID >>> 3);

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

    public boolean recomputeBounds;
    public QuadSpace mapBounds;
    public QuadSpace getBounds(){
        if (recomputeBounds) computeBound();
        return mapBounds;
    }
    /**
     * Creates the {@link QuadSpace} bound of this Greedy Map
     * by finding the extremes of all the Greedy Nodes
     */
    public void computeBound(){
        int nodeCount = nodes.length; //How many nodes we are working with
        if (nodeCount == 0) mapBounds = QuadSpace.empty(); //If we don't have any nodes, the bounds are invalid
        else if (nodeCount == 1){
            //If we only have 1 node, create a QuadSpace bound with the same dimensions as the node
            mapBounds = nodes[0].buildEquvilantQuadSpace();
        }
        else if (nodeCount == 2) {
            //If we have two nodes, find the extremes
            GreedyNode node1 = nodes[0], node2 = nodes[1];
            mapBounds = new QuadSpace(
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
            mapBounds = new QuadSpace(xCorner, yCorner, zCorner,
                    xMajor - xCorner, yMajor - yCorner, zMajor - zCorner);
        }
        recomputeBounds = false;
    }

    public int size() { return nodes.length; }
    public GreedyNode[] nodes;
    public int count;
    public GreedyNode addNode(GreedyNode gNode){
        gNode.assignID(newNodeID());
        if (count == nodes.length) growArray();
        nodes[count++] = gNode;
        recomputeBounds = true;
        return gNode;
        //ToDo: Add system to evaluate all other contained GNodes to see if they are
        // "sisters" to the newly added GNode, then update the MapContexts as needed
        // (will need to add a feature to QuadSpaces to be able to look for touching edges or faces...)
    }
    private void growArray(){
        int newSize = nodes.length + 1;
        GreedyNode[] newNodes = new GreedyNode[newSize];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        nodes = newNodes;
    }
    public void removeAndCollapse(int index){
        if (index < 0 || index > nodes.length) return;
        GreedyNode[] newArray = new GreedyNode[nodes.length];
        System.arraycopy(nodes, 0, newArray, 0, index);
        System.arraycopy(nodes, index + 1, newArray, index, nodes.length - index);
        nodes = newArray;
        count--;
    }


    public void removeNode(GreedyNode gNode){removeNode(gNode.nodeID);}
    public void removeNode(byte id){
        //just in case the id contains directional info
        id = GreedyNode.isolateID(id);

        int index = 0;
        boolean flag = false;
        for (; index < nodes.length; index++){
            if (nodes[index].nodeID == id){
                flag = true; break;
            }
        }
        if (flag) {
            removeAndCollapse(index);
            recomputeBounds = true;
        }
    }

    public @Nullable GreedyNode nodeByID(byte nodeID){
        nodeID = GreedyNode.isolateID(nodeID);
        for (GreedyNode node : nodes) if (node.nodeID == nodeID) return node;
        return null;
    }

    public static @Nullable GreedyMap retrieveFromGlobalID(long globalID){
        //Only check RAM and File cache, we don't want to make a new chunk if there isn't one
        GreedyChunk gChunk = GreedyWorld.greedyWorld_DEFAULT.retrieveOnly(globalID);
        if (gChunk == null) return null; //Womp, no GChunks exist for that ID
        byte mapID = GreedyWorld.isolateMapID(globalID); //Getting the GMap from the I.D....
        return gChunk.getMap(mapID);
        //Return regardless of if it exists, the method is @Nullable
    }

    @Override
    public String toString() {
        return "GreedyMap["+mapID+"]{" +
                "MapExtensionRange(" + mapExtensionRange +
                "), recomputeBounds(" + recomputeBounds +
                "), mapBounds " + mapBounds +
                ",\nnodes" + Arrays.toString(nodes) +
                '}';
    }
}

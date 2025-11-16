package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import org.joml.Vector3i;

import java.util.ArrayList;

public class GreedyMap {
    public GreedyMap(ArrayList<GreedyNode> nodes){
        this.nodes = nodes;
    }

    public QuadSpace MapBound;
    /**
     * Creates the {@link QuadSpace} bound of this Greedy Map
     * by finding the extremes of all the Greedy Nodes
     */
    public void computeBound(){
        int nodeCount = nodes.size(); //How many nodes we are working with
        if (nodeCount == 0) MapBound = QuadSpace.INVALID; //If we don't have any nodes, the bounds are invalid
        else if (nodeCount == 1){
            //If we only have 1 node, create a QuadSpace bound with the same dimensions as the node
            GreedyNode node = nodes.get(0);
            MapBound = new QuadSpace(node.cornerMinor, node.x, node.y, node.z);
        }
        else if (nodeCount == 2) {
            //If we have two nodes, find the extremes
            GreedyNode node1 = nodes.get(0), node2 = nodes.get(1);
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

    protected ArrayList<GreedyNode> nodes;
}

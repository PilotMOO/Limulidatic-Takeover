package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;

public class Node3DGrid {
    public Node3D[][][] grid;
    public Vector3i lowerBottomLeft;

    public int sizeX, sizeY, sizeZ;
    public boolean skipClean;

    public BiFunction<BlockPos, Level, Boolean> invalidNodePosition;

    public Node3DGrid(Vector3i pos, boolean centered, int sizeX, int sizeY, int sizeZ, BiFunction<BlockPos, Level, Boolean> invalidNodePosition){
        this.lowerBottomLeft = pos;
        this.grid = new Node3D[this.sizeX = sizeX][this.sizeY = sizeY][this.sizeZ = sizeZ];
        if (centered) lowerBottomLeft.sub(Math.floorDiv(this.sizeX, 2), Math.floorDiv(this.sizeY, 2), Math.floorDiv(this.sizeZ, 2));
        this.invalidNodePosition = invalidNodePosition;
    }

    public void adjust(Vector3i pos, boolean centered, int sizeX, int sizeY, int sizeZ){
        this.lowerBottomLeft = pos;
        this.grid = new Node3D[this.sizeX = sizeX][this.sizeY = sizeY][this.sizeZ = sizeZ];
        if (centered) lowerBottomLeft.sub(Math.floorDiv(this.sizeX, 2), Math.floorDiv(this.sizeY, 2), Math.floorDiv(this.sizeZ, 2));
    }

    public void fillGrid(Level level){
        BlockPos.MutableBlockPos mBPos = new BlockPos.MutableBlockPos(lowerBottomLeft.x, lowerBottomLeft.y, lowerBottomLeft.z);
        for (int x = 0; x < sizeX; x++){
            mBPos.move(1, -(sizeY - 1), -(sizeZ - 1));
            for (int y = 0; y < sizeX; y++) {
                mBPos.move(0, 1, -(sizeZ - 1));
                for (int z = 0; z < sizeZ; z++) {
                    mBPos.move(0, 0, 1);
                    grid[x][y][z] = new Node3D(x, y, z, invalidNodePosition.apply(mBPos, level));
                }
            }
        }
        skipClean = true;
    }

    protected static final Comparator<Node3D> compareFCost = Comparator.comparingInt(Node3D::fCost);
    public @Nullable Node3D findPath(Vector3i start, Vector3i end){
        if (ensureInRange(start, end)){
            if (!skipClean) for (Node3D[][] nodes : grid) for (Node3D[] nodes1 : nodes) for (Node3D node : nodes1) if (node != null) node.resetCosts();
            skipClean = false;

            Node3D sNode = grid[start.x - lowerBottomLeft.x][start.y - lowerBottomLeft.y][start.z - lowerBottomLeft.z];
            Node3D eNode = grid[end.x - lowerBottomLeft.x][end.y - lowerBottomLeft.y][end.z - lowerBottomLeft.z];
            sNode.initForStart(eNode);
            ArrayList<Node3D> OPEN = new ArrayList<>(1);
            OPEN.add(sNode);

            Node3D current;
            while (true) {
                if (OPEN.isEmpty()){
                    System.err.println("[3D NODE GRID] Oops! Ran out of valid nodes to check when trying to locate a path between points ["
                            + start + "] and [" + end + "]. There likely just isn't a possible path present :[");
                    return null;
                }
                current = OPEN.remove(0);
                current.closed = true;

                if (current == eNode) break;
                for (Node3D n : getValidSisters(current)){
                    if (n.parent == null) n.initCost(current, eNode);
                    else n.switchCosts(current);
                    if (!OPEN.contains(n)) OPEN.add(n);
                }
                OPEN.sort(compareFCost);
            }
            return current;
        } else {
            System.err.println("[3D NODE GRID] ERROR! Positions start[" + start + "] and/or end[" + end
                    + "] were out of bounds of the grid sized [" + sizeX + ", " + sizeY + ", " + sizeZ
                    + "] with bottom left centered at [" + lowerBottomLeft + "]");
            return null;
        }
    }

    public boolean ensureInRange(Vector3i start, Vector3i end){
        return checkCube(start) && checkCube(end);
    }
    public boolean checkCube(Vector3i pos){
        int lowX = lowerBottomLeft.x, lowY = lowerBottomLeft.y, lowZ = lowerBottomLeft.z;
        int highX = lowX + sizeX, highY = lowY + sizeY, highZ = lowZ + sizeZ;
        return pos.x >= lowX && pos.x < highX &&
                pos.y >= lowY && pos.y < highY &&
                pos.z >= lowZ && pos.z < highZ;
    }

    /**
     * Gets all VALID "sister" nodes within 1 step of the supplied node.
     * Does NOT return any of the "true corners" (I.E. the corners of the 3x3x3 cube it checks)
     * @param center the "center" node that is relative to all the sister nodes
     * @return An ArrayList of Sister nodes within 1 step of the supplied node. It EXCLUDES invalid nodes (blocked, closed, not present, etc.)
     */
    public ArrayList<Node3D> getValidSisters(Node3D center){
        //Init capacity so resizing doesn't fuck with performance as much
        //3^3 = 27, remove the center and the 8 corners, and you got a max return capacity of 18
        ArrayList<Node3D> sisters = new ArrayList<>(18);
        //Blacklists only check the 2d diagonals to the center--
        //the X-Z diagonals on the same Y axis [total 4] and each of the "true cardinals" for above/below the center [4 per, total 8]
        ArrayList<Node3D> blacklist = new ArrayList<>(12);

        Node3D[] diagonal = diagonals(center);
        for (int index = 0; index < 12; index++){
            Node3D node = diagonal[index];
            Node3D c1, c2;
            //the first and last 4 are of the top and bottom, so we want the "true cardinal" directions-- North, East, South, West--
            //relative to the starting node.
            //So we want to check the two nodes directly above/below the current node, as well as the one above/below the sister node.
            if (index < 4 || index > 7) {
                c1 = getIfPresent(center.x, node.y, center.z); //Above/Below current node
                c2 = getIfPresent(node.x, center.y, node.z); //Above/Below sister node
            } else {
                //the 5th to 8th nodes are the diagonal corners parallel on the Y-axis to the current node.
                //Grab the two sisters one step in each relative X and Z towards the sister node.
                c1 = getIfPresent(node.x, center.y, center.z); //Relative X
                c2 = getIfPresent(center.x, center.y, node.z); //Relative Z
            }
            //Add the sister to the blacklist if the intercepting relative nodes are not present or blocked
            if ((c1 == null || c1.blocked) && (c2 == null || c2.blocked)) blacklist.add(node);
        }
        //Loop through all X-Y-Z coords from -1 to 1 relative to the current node
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                for (int z = -1; z <= 1; z++){
                    //Skip "3 pair" nodes (the corners of the 3x cube) by ensuring at least 1 value is equal to 0
                    if (x != 0 && y != 0 && z != 0) continue;
                        //also skip if they are all equal-- I.E. all are 0
                        //If all are zero then that's just the starting node, and we don't want that one
                    else if (x == y && y == z) continue;
                    Node3D node = getIfPresent(center.x + x, center.y + y, center.z + z);
                    if (node == null || node.blocked || node.closed || blacklist.contains(node)) continue; //Discard if we don't need to check it
                    sisters.add(node);
                }
            }
        }
        return sisters; //Finally, return all valid ones :)
    }
    //Does NOT grab the true corners of the 3x cube
    private Node3D[] diagonals(Node3D center){
        Node3D[] nodes = new Node3D[12];
        //Top cardinal
        nodes[0] = getIfPresent(center.x, center.y + 1, center.z + 1);
        nodes[1] = getIfPresent(center.x + 1, center.y + 1, center.z);
        nodes[2] = getIfPresent(center.x, center.y + 1, center.z - 1);
        nodes[3] = getIfPresent(center.x - 1, center.y + 1, center.z);
        //Middle diagonals
        nodes[4] = getIfPresent(center.x + 1, center.y, center.z + 1);
        nodes[5] = getIfPresent(center.x + 1, center.y, center.z - 1);
        nodes[6] = getIfPresent(center.x - 1, center.y, center.z - 1);
        nodes[7] = getIfPresent(center.x - 1, center.y, center.z + 1);
        //Bottom cardinal
        nodes[8] = getIfPresent(center.x, center.y - 1, center.z + 1);
        nodes[9] = getIfPresent(center.x + 1, center.y - 1, center.z);
        nodes[10] = getIfPresent(center.x, center.y - 1, center.z - 1);
        nodes[11] = getIfPresent(center.x - 1, center.y - 1, center.z);
        return nodes;
    }

    /**
     * Retrieves a node of the given [X, Y, Z] coordinates from the grid IF it is present.
     * If the given coords fall outside the bounds of the grid, or the node is missing, it will return null
     * @param x the X coord of the wanted node
     * @param y the Y coord of the wanted node
     * @param z the Z coord of the wanted node
     * @return the wanted node with the given coordinates, or null if the coords are out of bounds or the node isn't present.
     */
    public Node3D getIfPresent(int x, int y, int z){
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) return null;
        else return grid[x][y][z];
    }
}

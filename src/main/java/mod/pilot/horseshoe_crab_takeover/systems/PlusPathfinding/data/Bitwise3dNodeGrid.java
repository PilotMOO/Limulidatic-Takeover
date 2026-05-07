package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;

public class Bitwise3dNodeGrid {
    public BitNodeMap bitGrid;
    public Vector3i lowerBottomLeft;

    public int sizeX, sizeY, sizeZ;

    public BiFunction<BlockPos, Level, Byte> nodeStateTest;

    public Bitwise3dNodeGrid(Vector3i pos, boolean centered, int sizeX, int sizeY, int sizeZ, BiFunction<BlockPos, Level, Byte> nodeStateTest){
        this.lowerBottomLeft = pos;
        this.bitGrid = new BitNodeMap(this.sizeX = sizeX, this.sizeY = sizeY, this.sizeZ = sizeZ);
        if (centered) lowerBottomLeft.sub(Math.floorDiv(this.sizeX, 2), Math.floorDiv(this.sizeY, 2), Math.floorDiv(this.sizeZ, 2));
        this.nodeStateTest = nodeStateTest;
    }

    public void adjust(Vector3i pos, boolean centered, int sizeX, int sizeY, int sizeZ){
        this.lowerBottomLeft = pos;
        this.bitGrid = new BitNodeMap(this.sizeX = sizeX, this.sizeY = sizeY, this.sizeZ = sizeZ);
        if (centered) lowerBottomLeft.sub(Math.floorDiv(this.sizeX, 2), Math.floorDiv(this.sizeY, 2), Math.floorDiv(this.sizeZ, 2));
    }

    /*public void fillGrid(Level level){
        BlockPos.MutableBlockPos mBPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    mBPos.set(lowerBottomLeft.x + x, lowerBottomLeft.y + y, lowerBottomLeft.z + z);
                    bitGrid.writeState(x, y, z, nodeStateTest.apply(mBPos, level));
                }
            }
        }
    }*/

    protected static final Comparator<NodeContext> compareFCost = Comparator.comparingInt(NodeContext::fCost);
    public @Nullable ReversibleArray<NodeContext> findPath(Vector3i start, Vector3i end, Level level){
        if (ensureInRange(start, end)){
            NodeContext[][][] nodeGrid = new NodeContext[sizeX][sizeY][sizeZ];

            int sX = start.x - lowerBottomLeft.x, sY = start.y - lowerBottomLeft.y, sZ = start.z - lowerBottomLeft.z;
            int eX = end.x - lowerBottomLeft.x, eY = end.y - lowerBottomLeft.y, eZ = end.z - lowerBottomLeft.z;
            NodeContext sNode = computeNode(sX, sY, sZ, level, nodeGrid);
            NodeContext eNode = computeNode(eX, eY, eZ, level, nodeGrid);
            sNode.initForStart(eNode);
            ArrayList<NodeContext> OPEN = new ArrayList<>(1);
            OPEN.add(sNode);

            NodeContext current;
            int cycle = 0;
            while (true) {
                cycle++;
                if (OPEN.isEmpty()){
                    System.err.println("[BIT NODE GRID] Oops! Ran out of valid nodes to check when trying to locate a path between points ["
                            + start + "] and [" + end + "]. There likely just isn't a possible path present :[");
                    return null;
                }
                current = OPEN.remove(0);
                current.close();

                if (current == eNode) break;
                ArrayList<NodeContext> sisters = getValidSisters(current, level, nodeGrid);
                OPEN.ensureCapacity(OPEN.size() + sisters.size());
                for (NodeContext n : sisters){
                    if (n.parentRelative == 0) n.initCost(current, eNode);
                    else n.switchCosts(current);
                    if (!OPEN.contains(n)) OPEN.add(n);
                }
                OPEN.sort(compareFCost);
            }

            ReversibleArray<NodeContext> path = new ReversibleArray<>(cycle);
            int grow = (int) Math.sqrt(Math.floorDiv(sizeX + sizeY + sizeZ, 3));
            do{
                path.add(current, grow);
                System.out.println("current[" + current.x + ", " + current.y + ", " + current.z + "], relative[" + Integer.toBinaryString(current.state) + "]");
                int pX = ((current.parentRelative << 30) >>> 30), pY = ((current.parentRelative << 28) >>> 30), pZ = (current.parentRelative >>> 4);
                System.out.println("relative UNPACKED[" + pX + ", " + pY + ", " + pZ + "]");
                int x = current.x + (pX > 0 ? pX - 1 == 0 ? 1 : -1 : 0);
                int y = current.y + (pY > 0 ? pY - 1 == 0 ? 1 : -1 : 0);
                int z = current.z + (pZ > 0 ? pZ - 1 == 0 ? 1 : -1 : 0);
                System.out.println("coords[" + x + ", " + y + ", " + z + "]");
                current = nodeGrid[x][y][z];
            } while (current != sNode);
            path.cap();
            return path;
        } else {
            System.err.println("[BIT NODE GRID] ERROR! Positions start[" + start + "] and/or end[" + end
                    + "] were out of bounds of the grid sized [" + sizeX + ", " + sizeY + ", " + sizeZ
                    + "] with bottom left centered at [" + lowerBottomLeft + "]");
            return null;
        }
    }

    public NodeContext computeNode(int x, int y, int z, Level level, final NodeContext[][][] grid){
        byte state = nodeStateTest.apply(new BlockPos(lowerBottomLeft.x + x, lowerBottomLeft.y + y, lowerBottomLeft.z + z), level);
        bitGrid.writeState(x, y, z, state);
        return grid[x][y][z] = NodeContext.createParentlessContext(x, y, z, state);
    }
    public NodeContext computeNode(int x, int y, int z, Level level){
        byte state = nodeStateTest.apply(new BlockPos(lowerBottomLeft.x + x, lowerBottomLeft.y + y, lowerBottomLeft.z + z), level);
        bitGrid.writeState(x, y, z, state);
        return NodeContext.createParentlessContext(x, y, z, state);
    }
    public void computeClean(int x, int y, int z, Level level){
        bitGrid.writeState(x, y, z, nodeStateTest.apply(new BlockPos(lowerBottomLeft.x + x, lowerBottomLeft.y + y, lowerBottomLeft.z + z), level));
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
     * @return An ArrayList of Sister nodes within 1 step of the supplied node. It EXCLUDES invalid nodes (state, closed, not present, etc.)
     */
    public ArrayList<NodeContext> getValidSisters(NodeContext center, Level level, NodeContext[][][] nodeGrid){
        //Init capacity so resizing doesn't fuck with performance as much
        //3^3 = 27, remove the center and the 8 corners, and you got a max return capacity of 18
        ArrayList<NodeContext> sisters = new ArrayList<>(18);
        //Blacklists only check the 2d diagonals to the center--
        //the X-Z diagonals on the same Y axis [total 4] and each of the "true cardinals" for above/below the center [4 per, total 8]
        ArrayList<NodeContext> blacklist = new ArrayList<>(12);

        NodeContext[] diagonal = diagonals(center, level, nodeGrid);
        for (int index = 0; index < 12; index++){
            NodeContext node;
            if ((node = diagonal[index]) == null){
                continue;
            }
            NodeContext c1, c2;
            //the first and last 4 are of the top and bottom, so we want the "true cardinal" directions-- North, East, South, West--
            //relative to the starting node.
            //So we want to check the two nodes directly above/below the current node, as well as the one above/below the sister node.
            if (index < 4 || index > 7) {
                c1 = getOrCompute(center.x, node.y, center.z, level, nodeGrid); //Above/Below current node
                c2 = getOrCompute(node.x, center.y, node.z, level, nodeGrid); //Above/Below sister node
            } else {
                //the 5th to 8th nodes are the diagonal corners parallel on the Y-axis to the current node.
                //Grab the two sisters one step in each relative X and Z towards the sister node.
                c1 = getOrCompute(node.x, center.y, center.z, level, nodeGrid); //Relative X
                c2 = getOrCompute(center.x, center.y, node.z, level, nodeGrid); //Relative Z
            }
            //Add the sister to the blacklist if the intercepting relative nodes are not present or state
            if ((c1 == null || (c1.state >>> 1) == 0) && (c2 == null || (c2.state >>> 1) == 0)){
                blacklist.add(node);
            }
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
                    NodeContext node = getOrCompute(center.x + x, center.y + y, center.z + z, level, nodeGrid);
                    if (node == null || (node.state >>> 1) != 2 || (node.state << 7 >>> 7) == 1 || blacklist.contains(node)) continue; //Discard if we don't need to check it
                    sisters.add(node);
                }
            }
        }
        return sisters; //Finally, return all valid ones :)
    }
    //Does NOT grab the true corners of the 3x cube
    private NodeContext[] diagonals(NodeContext center, Level level, NodeContext[][][] nodeGrid){
        NodeContext[] nodes = new NodeContext[12];
        //Top cardinal
        nodes[0] = getOrCompute(center.x, center.y + 1, center.z + 1, level, nodeGrid);
        nodes[1] = getOrCompute(center.x + 1, center.y + 1, center.z, level, nodeGrid);
        nodes[2] = getOrCompute(center.x, center.y + 1, center.z - 1, level, nodeGrid);
        nodes[3] = getOrCompute(center.x - 1, center.y + 1, center.z, level, nodeGrid);
        //Middle diagonals
        nodes[4] = getOrCompute(center.x + 1, center.y, center.z + 1, level, nodeGrid);
        nodes[5] = getOrCompute(center.x + 1, center.y, center.z - 1, level, nodeGrid);
        nodes[6] = getOrCompute(center.x - 1, center.y, center.z - 1, level, nodeGrid);
        nodes[7] = getOrCompute(center.x - 1, center.y, center.z + 1, level, nodeGrid);
        //Bottom cardinal
        nodes[8] = getOrCompute(center.x, center.y - 1, center.z + 1, level, nodeGrid);
        nodes[9] = getOrCompute(center.x + 1, center.y - 1, center.z, level, nodeGrid);
        nodes[10] = getOrCompute(center.x, center.y - 1, center.z - 1, level, nodeGrid);
        nodes[11] = getOrCompute(center.x - 1, center.y - 1, center.z, level, nodeGrid);
        return nodes;
    }

    /**
     * Retrieves a node of the given [X, Y, Z] coordinates from the supplied 3d node grid IF it is present,
     * or computes a new NodeContext from the BitPackage. Returns {@code null} if the supplied coordinates fall out of line of the grid
     * If the given coords fall outside the bounds of the grid, or the node is missing, it will return null
     * @param x the X coord of the wanted node
     * @param y the Y coord of the wanted node
     * @param z the Z coord of the wanted node
     * @return the wanted node with the given coordinates, or null if the coords are out of bounds or the node isn't present.
     */
    public NodeContext getOrCompute(int x, int y, int z, Level level, final NodeContext[][][] nodeGrid){
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) return null;
        NodeContext ctx = nodeGrid[x][y][z];
        return ctx != null ? ctx : computeNode(x, y, z, level, nodeGrid);
    }

    public static class BitNodeMap extends BitPackage3d<NodeContext>{
        public BitNodeMap(int sizeX, int sizeY, int sizeZ) {
            super(3, sizeX, sizeY, sizeZ);
        }
        public static BitNodeMap square(int size){
            return new BitNodeMap(size, size, size);
        }

        public void writeState(int x, int y, int z, byte state){
            int contextIndex = computeContextualIndexUNSAFE(x, y, z);
            int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64); //No inlining for readability
            /*int lastBit = bitsPerObject + bitOffset;
            int pages = Math.floorDiv(lastBit, 64);
            if (lastBit - (pages * 64) > 0) pages++;*/
            if (contextIndex != bitMailIndex) readBits(contextIndex);
            writeBitsUNSAFE(contextIndex, BitwiseDataHelper.writeRangeToSentence(bitMail, bitOffset, state, 3)/*, pages*/);
        }

        public byte readState(int x, int y, int z){
            int contextIndex = computeContextualIndexUNSAFE(x, y, z);
            int bitOffset = computeBitIndexUNSAFE(x, y, z) - (contextIndex * 64);
            if (contextIndex != bitMailIndex) readBits(contextIndex);
            return (byte) BitwiseDataHelper.isolateAndMergeAcrossWords(bitMail[0], bitMail[1], bitOffset, 3);
        }

        @Override
        protected long[] toBits(NodeContext obj, int bitOffset, long[] bitMail) {
            return BitwiseDataHelper.writeRangeToSentence(bitMail, bitOffset, obj.state, 2);
        }
        @Override
        protected NodeContext fromBits(int bitOffset, long[] bitMail) {
            NodeContext context = NodeContext.createEmptyContext();
            context.state = (byte)BitwiseDataHelper.isolateAndMergeAcrossWords(bitMail[0], bitMail[1], bitOffset, 2);
            return context;
        }
    }

    public static class NodeContext{
        private NodeContext(int x, int y, int z){this.x = x; this.y = y; this.z = z;}
        private NodeContext(){}

        public static NodeContext createEmptyContext(){
            return new NodeContext();
        }
        public static NodeContext createPositionedContext(int x, int y, int z, byte pX, byte pY, byte pZ){
            NodeContext context = new NodeContext(x, y, z);
            context.setParent(pX, pY, pZ);
            return context;
        }
        public static NodeContext createPositionedContext(int x, int y, int z){ return new NodeContext(x, y, z); }
        public static NodeContext createParentlessContext(int x, int y, int z, byte state){
            NodeContext context = new NodeContext(x, y, z);
            context.state = state;
            return context;
        }

        public int x, y, z;
        public byte parentRelative;
        /**
         * Sets the relative position of the parent node to this one
         * @param x the relative direction towards the parent on the X axis-- 0 is NONE, 1 is positive, 2 is negative
         * @param y the relative direction towards the parent on the Y axis-- 0 is NONE, 1 is positive, 2 is negative
         * @param z the relative direction towards the parent on the Z axis-- 0 is NONE, 1 is positive, 2 is negative
         */
        public void setParent(byte x, byte y, byte z){
            parentRelative = (byte)(x | y << 2 | z << 4);
        }

        public int gCost, hCost;
        public int fCost() {return gCost + hCost;}

        public byte state;
        //first bit index defines if the node is closed or not
        //the two proceeding indexes define the following states: 0 == blocked, 1 == traversable, 2 == open
        public void close(){ state |= 1; }

        public void initCost(NodeContext parent, NodeContext end){
            setParent((byte)(parent.x > x ? 1 : parent.x < x ? 2 : 0),
                    (byte)(parent.y > y ? 1 : parent.y < y ? 2 : 0),
                    (byte)(parent.z > z ? 1 : parent.z < z ? 2 : 0));
            byte value = 0;
            if (x != parent.x) value++; if (y != parent.y) value++; if (z != parent.z) value++;
            gCost = parent.gCost + switch (value) {
                case 1 -> STRAIGHT_STEP; case 2 -> DIAGONAL_STEP; case 3 -> THREE_PAIR_STEP;
                default -> 0;
            };

            hCost = 0;
            int x = this.x, y = this.y, z = this.z;
            while (true) {
                byte steps = 0;
                if (x != end.x) {
                    if (x > end.x) { x--; } else { x++; }
                    steps++;
                }
                if (y != end.y) {
                    if (y > end.y) { y--; } else { y++; }
                    steps++;
                }
                if (z != end.z) {
                    if (z > end.z) { z--; } else { z++; }
                    steps++;
                }

                if (steps == 0) break;
                else hCost += switch (steps){
                    case 1 -> STRAIGHT_STEP; case 2 -> DIAGONAL_STEP; case 3 -> THREE_PAIR_STEP;
                    default -> 0;
                };
            }
        }
        public void initForStart(NodeContext end){
            gCost = 0;
            hCost = 0;

            int x = this.x, y = this.y, z = this.z;
            while (true) {
                byte steps = 0;
                if (x != end.x) {
                    if (x > end.x) { x--; } else { x++; }
                    steps++;
                }
                if (y != end.y) {
                    if (y > end.y) { y--; } else { y++; }
                    steps++;
                }
                if (z != end.z) {
                    if (z > end.z) { z--; } else { z++; }
                    steps++;
                }

                if (steps == 0) break;
                else hCost += switch (steps){
                    case 1 -> STRAIGHT_STEP; case 2 -> DIAGONAL_STEP; case 3 -> THREE_PAIR_STEP;
                    default -> 0;
                };
            }
        }
        public void switchCosts(NodeContext parent){
            byte value = 0;
            if (x != parent.x) value++; if (y != parent.y) value++; if (z != parent.z) value++;
            int newG = parent.gCost + switch (value) {
                case 1 -> STRAIGHT_STEP; case 2 -> DIAGONAL_STEP; case 3 -> THREE_PAIR_STEP;
                default -> 0;
            };
            if (newG < gCost || this.parentRelative == 0) {
                setParent((byte)(parent.x > x ? 1 : parent.x < x ? 2 : 0),
                        (byte)(parent.y > y ? 1 : parent.y < y ? 2 : 0),
                        (byte)(parent.z > z ? 1 : parent.z < z ? 2 : 0));
                this.gCost = newG;
            }
        }

        //step value 1, x10. For steps where only ONE value is different
        private static final int STRAIGHT_STEP = 10;
        //step value 1^2 + 1^2 = 2, or roughly 1.4. x10. For steps where two values are different
        private static final int DIAGONAL_STEP = 14;
        //step value (1.4^2) + (1^2) = 2.96, or roughly 1.7. x10. For steps where ALL are different
        private static final int THREE_PAIR_STEP = 17;
    }
}

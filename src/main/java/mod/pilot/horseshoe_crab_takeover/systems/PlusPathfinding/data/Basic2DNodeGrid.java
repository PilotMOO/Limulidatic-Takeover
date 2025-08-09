package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;

public class Basic2DNodeGrid {
    public Basic2DNode[][] grid;
    public Basic2DNode.Snapshot[][] snapshotGrid(){
        return snapshotGrid(true);
    }
    public Basic2DNode.Snapshot[][] snapshotGrid(boolean assumeBlockedIfUnavailable){
        Basic2DNode.Snapshot[][] snap = new Basic2DNode.Snapshot[sizeX][sizeZ];
        if (grid != null) {
            for (int x = 0; x < sizeX; x++){
                for (int z = 0; z < sizeZ; z++){
                    Basic2DNode node = grid[x][z];
                    snap[x][z] = node != null ?
                            Basic2DNode.Snapshot.snapshot(node, true) :
                            Basic2DNode.Snapshot.deadWeight(x, z, assumeBlockedIfUnavailable);
                }
            }
        }
        return snap;
    }

    public Vector3i bottomLeft;
    public int sizeX, sizeZ;

    protected boolean skipClean = false;

    public BiFunction<BlockPos, Level, Boolean> notWalkable;

    public Basic2DNodeGrid(Vector3i pos, boolean centered, int sizeX, int sizeZ, BiFunction<BlockPos, Level, Boolean> notWalkable){
        this.bottomLeft = pos;
        this.grid = new Basic2DNode[this.sizeX = sizeX][this.sizeZ = sizeZ];
        if (centered) bottomLeft.sub(Math.floorDiv(this.sizeX, 2), 0, Math.floorDiv(this.sizeZ, 2));
        this.notWalkable = notWalkable;
    }

    public void adjust(Vector3i pos, boolean centered, int sizeX, int sizeZ){
        this.bottomLeft = pos;
        this.grid = new Basic2DNode[this.sizeX = sizeX][this.sizeZ = sizeZ];
        if (centered) bottomLeft.sub(Math.floorDiv(this.sizeX, 2), 0, Math.floorDiv(this.sizeZ, 2));
    }

    public void fillGrid(Level level){
        for (int x = 0; x < sizeX; x++){
            for (int z = 0; z < sizeZ; z++){
                BlockPos bPos = new BlockPos(bottomLeft.x + x, bottomLeft.y, bottomLeft.z + z);
                grid[x][z] = new Basic2DNode(x, z, notWalkable.apply(bPos, level));
            }
        }
        skipClean = true;
    }

    protected static final Comparator<Basic2DNode> compareFCost = Comparator.comparingInt(Basic2DNode::fCost);
    public @Nullable Basic2DNode findPath(Vector3i start, Vector3i end){
        if (ensureInRange(start, end)){
            if (!skipClean) for (Basic2DNode[] nodes : grid) for (Basic2DNode node : nodes) if (node != null) node.resetCosts();
            skipClean = false;

            Basic2DNode sNode = grid[start.x - bottomLeft.x][start.z - bottomLeft.z];
            Basic2DNode eNode = grid[end.x - bottomLeft.x][end.z - bottomLeft.z];

            ArrayList<Basic2DNode> CLOSED = new ArrayList<>();
            ArrayList<Basic2DNode> OPEN = new ArrayList<>();
            sNode.initForStart(eNode);
            OPEN.add(sNode);
            Basic2DNode current;
            while (true) {
                if (OPEN.isEmpty()){
                    System.err.println("[BASIC NODE GRID] Oops! Ran out of valid nodes to check when trying to locate a path between points ["
                            + start + "] and [" + end + "]. There likely just isn't a possible path present :[");
                    return null;
                }
                current = OPEN.remove(0);
                CLOSED.add(current);

                if (current == eNode) break;
                for (Basic2DNode n : getSisters(current)){
                    if (n == null || n.blocked || CLOSED.contains(n)) continue;
                    else if (n.x != current.x && n.z != current.z){
                        Basic2DNode c1 = getIfPresent(n.x, current.z);
                        Basic2DNode c2 = getIfPresent(current.x, n.z);
                        boolean c1Block = c1 == null || c1.blocked;
                        boolean c2Block = c2 == null || c2.blocked;
                        if (c1Block && c2Block) continue;
                    }

                    if (!n.costInit) n.initCost(current, eNode);
                    else n.switchCosts(current);

                    if (!OPEN.contains(n)) OPEN.add(n);
                }
                OPEN.sort(compareFCost);
            }
            return current;
        } else {
            System.err.println("[BASIC NODE GRID] ERROR! Positions start[" + start + "] and/or end[" + end
                    + "] were out of bounds of the grid sized [" + sizeX + ", " + sizeZ
                    + "] with bottom left centered at [" + bottomLeft + "]");
            return null;
        }
    }

    public boolean ensureInRange(Vector3i start, Vector3i end){
        return checkSquare(start) && checkSquare(end);
    }
    public boolean checkSquare(Vector3i pos){
        if (pos.y != bottomLeft.y) return false;
        int lowX = bottomLeft.x, lowZ = bottomLeft.z;
        int highX = lowX + sizeX, highZ = lowZ + sizeZ;
        return pos.x >= lowX && pos.x < highX &&
                pos.z >= lowZ && pos.z < highZ;
    }

    public Basic2DNode[] getSisters(Basic2DNode center){
        Basic2DNode[] a = new Basic2DNode[8];
        a[0] = getIfPresent(center.x, center.z + 1);
        a[1] = getIfPresent(center.x + 1, center.z + 1);
        a[2] = getIfPresent(center.x + 1, center.z);
        a[3] = getIfPresent(center.x + 1, center.z - 1);
        a[4] = getIfPresent(center.x, center.z - 1);
        a[5] = getIfPresent(center.x - 1, center.z - 1);
        a[6] = getIfPresent(center.x - 1, center.z);
        a[7] = getIfPresent(center.x - 1, center.z + 1);
        return a;
    }
    public Basic2DNode getIfPresent(int x, int z){
        if (x < 0 || x >= sizeX || z < 0 || z >= sizeZ) return null;
        else return grid[x][z];
    }
}

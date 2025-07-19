package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Response;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class Basic2DNodeGrid {
    public Basic2DNode[][] grid;
    protected Basic2DNode[][] snapshot;

    public final Vector3i bottomLeft;
    public final int sizeX, sizeZ;

    public Predicate<BlockState> walkable;

    public Basic2DNodeGrid(Vector3i pos, int sizeX, int sizeZ, Predicate<BlockState> walkable, boolean isCentered){
        this.bottomLeft = isCentered ? pos.sub(Math.floorDiv(sizeX, 2), 0, Math.floorDiv(sizeZ, 2)) : pos;
        this.sizeX = sizeX; this.sizeZ = sizeZ;
        this.walkable = walkable;
        grid = new Basic2DNode[sizeX][sizeZ];
    }

    public void fillGrid(Level level){
        for (int x = 0; x <= sizeX; x++){
            for (int z = 0; z <= sizeZ; z++){
                BlockPos bPos = new BlockPos(bottomLeft.x +  x, bottomLeft.y, bottomLeft.z + z);
                grid[x][z] = new Basic2DNode(x, z, walkable.test(level.getBlockState(bPos)));
            }
        }
        snapshot = grid;
    }

    public void resetNodeValues(){
        this.grid = snapshot;
    }

    public void findPath(Vector3i start, Vector3i end){
        if (ensureInRange(start, end)){
            Basic2DNode sNode = grid[start.x][start.z];
            Basic2DNode eNode = grid[end.x][end.z];

            ArrayList<Basic2DNode> CLOSED = new ArrayList<>();
            ArrayList<Basic2DNode> OPEN = new ArrayList<>();
            //ToDo: finish looping for pathing, set parents after using getSisters()

            Basic2DNode current;
            while (true) {
                OPEN.forEach((node) -> { if (!node.costInit) node.initCost(node.parent, eNode); });
                OPEN.sort(Comparator.comparingInt(Basic2DNode::fCost));
                current = OPEN.get(0);

                if (current == eNode) break;

            }
        } else {
            System.err.println("[BASIC NODE GRID] ERROR! Positions start[" + start + "] and/or end[" + end
                    + "] were out of bounds of the grid sized [" + sizeX + ", " + sizeZ
                    + "] with bottom left centered at [" + bottomLeft + "]");
        }
    }

    protected boolean ensureInRange(Vector3i start, Vector3i end){
        return checkSquare(start) && checkSquare(end);
    }
    protected boolean checkSquare(Vector3i pos){
        if (pos.y != bottomLeft.y) return false;
        int lowX = bottomLeft.x, lowZ = bottomLeft.z;
        int highX = lowX + sizeX, highZ = lowZ + sizeZ;
        return pos.x > lowX && pos.x < highX &&
                pos.z > lowZ && pos.z < highZ;
    }

    protected Basic2DNode[] getSisters(Basic2DNode center){
        Basic2DNode[] a = new Basic2DNode[8];
        a[0] = getIfPresent(center.x, center.z + 1);
        a[1] = getIfPresent(center.x + 1, center.z + 1);
        a[2] = getIfPresent(center.x + 1, center.z);
        a[3] = getIfPresent(center.x + 1, center.z - 1);
        a[4] = getIfPresent(center.x, center.z - 1);
        a[5] = getIfPresent(center.x - 1, center.z - 1);
        a[6] = getIfPresent(center.x - 1, center.z);
        a[7] = getIfPresent(center.x -1, center.z + 1);
        return a;
    }
    protected Basic2DNode getIfPresent(int x, int z){
        if (x < 0 || x > sizeX || z < 0 || z > sizeZ) return null;
        else return grid[x][z];
    }
}

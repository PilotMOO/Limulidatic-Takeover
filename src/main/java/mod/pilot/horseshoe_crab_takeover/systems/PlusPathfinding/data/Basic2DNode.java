package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Basic2DNode {
    private static final int STRAIGHT_STEP = 10;
    private static final int DIAGONAL_STEP = 14;

    public Basic2DNode(int x, int z, boolean blocked){
        this.x = x; this.z = z;
        this.blocked = blocked;
    }
    public final int x, z;
    public Basic2DNode parent;

    public boolean blocked;

    //G = dist from start, H = dist from target, F is both added
    public int gCost, hCost;
    public int fCost(){ return gCost + hCost; }
    public boolean costInit = false;

    public void resetCosts(){
        gCost = hCost = 0;
        parent = null;
        costInit = false;
    }

    public void initCost(Basic2DNode parent, Basic2DNode end){
        this.parent = parent;
        gCost = parent.gCost + (x != parent.x && z != parent.z ? DIAGONAL_STEP : STRAIGHT_STEP);

        hCost = 0;
        int x = this.x, z = this.z;
        while (true) {
            boolean xStep = false;
            if (x != end.x) {
                if (x > end.x) { x--; } else { x++; }
                xStep = true;
            }
            if (z != end.z) {
                if (z > end.z) { z--; } else { z++; }
                hCost += xStep ? DIAGONAL_STEP : STRAIGHT_STEP;
            }
            else if (xStep) hCost += STRAIGHT_STEP;
            else break;
        }
        costInit = true;
    }
    public void initForStart(Basic2DNode end){
        this.parent = this;
        gCost = 0;
        hCost = 0;

        int x = this.x, z = this.z;
        while (true) {
            boolean xStep = false;
            if (x != end.x) {
                if (x > end.x) { x--; } else { x++; }
                xStep = true;
            }
            if (z != end.z) {
                if (z > end.z) { z--; } else { z++; }
                hCost += xStep ? DIAGONAL_STEP : STRAIGHT_STEP;
            }
            else if (xStep) hCost += STRAIGHT_STEP;
            else break;
        }
        costInit = true;
    }
    public void switchCosts(Basic2DNode parent){
        int newG = parent.gCost + (x != parent.x && z != parent.z ? DIAGONAL_STEP : STRAIGHT_STEP);
        if (newG < gCost || this.parent == null) {
            this.parent = parent;
            this.gCost = newG;
        }
    }

    public BlockPos getBlockPosWithOffset(Vector3i offset){
        return new BlockPos(offset.x + x, offset.y, offset.z + z);
    }

    public record Snapshot(int x, int z, boolean blocked, @Nullable Snapshot parent, int gCost, int hCost) implements INode{
        public static Snapshot snapshot(Basic2DNode node, boolean preserveParent){
            return new Snapshot(node.x, node.z, node.blocked,
                    preserveParent && node.parent != node && node.parent != null ? snapshot(node.parent, true) : null,
                    node.gCost, node.hCost);
        }
        public static Snapshot deadWeight(int x, int z, boolean assumeBlocked){
            return new Snapshot(x, z, assumeBlocked, null, -1, -1);
        }
        public static ArrayList<Snapshot> snapshotPathToArray(Basic2DNode endNode, boolean startNodeFirst){
            Snapshot c = snapshot(endNode, true);
            ArrayList<Snapshot> path = new ArrayList<>();
            path.add(c);
            while (c.parent != null) path.add((c = c.parent));
            if (startNodeFirst){
                ArrayList<Snapshot> invert = new ArrayList<>(path.size());
                for (int index = path.size() - 1; index >= 0; index--) invert.add(path.get(index));
                return invert;
            } else return path;
        }

        public int fCost(){ return gCost + hCost; }

        public Vector3i getPosWithOffset(Vector3i offset){
            return new Vector3i(offset.x + x, offset.y, offset.z + z);
        }

        @Override public @Nullable INode getParent() { return parent(); }
        @Override
        public Vector3i getWithOffset(Vector3i offset) {
            return new Vector3i(offset.x + x, offset.y, offset.z + z);
        }
    }
}

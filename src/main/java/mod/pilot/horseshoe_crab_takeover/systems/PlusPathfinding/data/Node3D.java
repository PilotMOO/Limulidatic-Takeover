package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Node3D {
    //step value 1, x10. For steps where only ONE value is different
    private static final int STRAIGHT_STEP = 10;
    //step value 1^2 + 1^2 = 2, or roughly 1.4. x10. For steps where two values are different
    private static final int DIAGONAL_STEP = 14;
    //step value (1.4^2) + (1^2) = 2.96, or roughly 1.7. x10. For steps where ALL are different
    private static final int THREE_PAIR_STEP = 17;

    public Node3D(int x, int y, int z, byte state){
        this.x = x; this.y = y; this.z = z;
        this.state = state;
    }
    public final int x, y, z;
    public Node3D parent;

    public byte state; //0 == OPEN, 1 == RELATIVE TRAVERSABLE, 2 == FULLY BLOCKED
    public boolean open(){ return state == 0; } //OPEN nodes can be stood on
    public boolean traversable(){ return state == 1; } //TRAVERSABLE nodes allow passage through to another node (open or relative)
    public boolean relativeTraversable(){ return state < 2; } //RELATIVE TRAVERSABLE nodes ONLY allow passage through to another node, but NOT a valid node in it of itself
    public boolean blocked(){ return state == 2; } //BLOCKED, nodes that canNOT be pathed through

    public boolean closed;

    //G = dist from start, H = dist from target, F is both added
    public int gCost, hCost;
    public int fCost(){ return gCost + hCost; }
    public void resetCosts(){
        gCost = hCost = 0;
        parent = null;
        closed = false;
    }

    public void initCost(Node3D parent, Node3D end){
        this.parent = parent;
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
    public void initForStart(Node3D end){
        this.parent = this;
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

    public void switchCosts(Node3D parent){
        byte value = 0;
        if (x != parent.x) value++; if (y != parent.y) value++; if (z != parent.z) value++;
        int newG = parent.gCost + switch (value) {
            case 1 -> STRAIGHT_STEP; case 2 -> DIAGONAL_STEP; case 3 -> THREE_PAIR_STEP;
            default -> 0;
        };
        if (newG < gCost || this.parent == null) {
            this.parent = parent;
            this.gCost = newG;
        }
    }

    public BlockPos getBlockPosWithOffset(Vector3i offset){
        return new BlockPos(offset.x + x, offset.y + y, offset.z + z);
    }

    public static class Grounded extends Node3D {
        public Grounded(int x, int y, int z, byte state) {
            super(x, y, z, state);
        }

        public boolean jumpNode;
        @Override public void resetCosts() {
            super.resetCosts();
            jumpNode = false;
        }
    }

    public record Snapshot(int x, int y, int z, byte state, @Nullable Node3D.Snapshot parent, int gCost, int hCost) implements INode {
        public static Node3D.Snapshot snapshot(Node3D node, boolean preserveParent){
            return new Node3D.Snapshot(node.x, node.y, node.z, node.state,
                    preserveParent && node.parent != node && node.parent != null ? snapshot(node.parent, true) : null,
                    node.gCost, node.hCost);
        }
        public static Node3D.Snapshot deadWeight(int x, int y, int z, byte assumeState){
            return new Node3D.Snapshot(x, y, z, assumeState, null, -1, -1);
        }
        public static ArrayList<Node3D.Snapshot> snapshotPathToArray(Node3D endNode, boolean startNodeFirst){
            Node3D.Snapshot c = snapshot(endNode, true);
            ArrayList<Node3D.Snapshot> path = new ArrayList<>();
            path.add(c);
            while (c.parent != null) path.add((c = c.parent));
            if (startNodeFirst){
                ArrayList<Node3D.Snapshot> invert = new ArrayList<>(path.size());
                for (int index = path.size() - 1; index >= 0; index--) invert.add(path.get(index));
                return invert;
            } else return path;
        }

        public int fCost(){ return gCost + hCost; }

        @Override public @Nullable INode getParent() { return parent(); }
        @Override public Vector3i getWithOffset(Vector3i offset) {
            return new Vector3i(offset.x + x, offset.y + y, offset.z + z);
        }
    }
}

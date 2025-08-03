package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

import net.minecraft.core.BlockPos;
import org.joml.Vector3i;

public class Node3D {
    //step value 1, x10. For steps where only ONE value is different
    private static final int STRAIGHT_STEP = 10;
    //step value 1^2 + 1^2 = 2, or roughly 1.4. x10. For steps where two values are different
    private static final int DIAGONAL_STEP = 14;
    //step value (1.4^2) + (1^2) = 2.96, or roughly 1.7. x10. For steps where ALL are different
    private static final int THREE_PAIR_STEP = 17;

    public Node3D(int x, int y, int z, boolean blocked){
        this.x = x; this.y = y; this.z = z;
        this.blocked = blocked;
    }
    public final int x, y, z;
    public Node3D parent;

    public boolean blocked;
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
        return new BlockPos(offset.x + x, offset.y, offset.z + z);
    }

    public static class Grounded extends Node3D {
        public Grounded(int x, int y, int z, boolean blocked) {
            super(x, y, z, blocked);
        }

        public boolean jumpNode;
        @Override public void resetCosts() {
            super.resetCosts();
            jumpNode = false;
        }
    }
}

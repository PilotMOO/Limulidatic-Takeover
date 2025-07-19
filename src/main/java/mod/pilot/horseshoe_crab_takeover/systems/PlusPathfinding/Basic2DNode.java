package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding;

public class Basic2DNode {
    public Basic2DNode(int x, int z, boolean blocked){
        this.x = x; this.z = z;
        this.blocked = blocked;
    }
    public final int x, z;
    public Basic2DNode parent;

    public boolean blocked;

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
    public void changeParentAndCostIfCheaper(Basic2DNode parent){
        int newG = parent.gCost + (x != parent.x && z != parent.z ? DIAGONAL_STEP : STRAIGHT_STEP);
        if (newG < gCost) {
            this.parent = parent;
            this.gCost = newG;
        }
    }
    public boolean costInit = false;
    //G = dist from start, H = dist from target, F is both added
    public int gCost, hCost;
    public int fCost(){ return gCost + hCost; }

    private static final int STRAIGHT_STEP = 10;
    private static final int DIAGONAL_STEP = 14;
}

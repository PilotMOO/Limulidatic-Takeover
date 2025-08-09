package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.AStarTesting;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces.INavigation;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Basic2DNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Basic2DNodeGrid;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.PlusMovementControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class FlatAStarNavigation<U extends WorldEntity, P extends PlusMovementControl> {
    public U user;
    public P plusControl;
    public double defaultError;
    public FlatAStarNavigation(U user, P plusControl, double error){
        this.user = user; this.plusControl = plusControl;
        this.defaultError = error;
    }

    public Basic2DNodeGrid grid;
    public Basic2DNode.Snapshot[][] gridSnapshot;
    public void snapshotCurrent(boolean assumeBlocked){
        gridSnapshot = grid.snapshotGrid(assumeBlocked);
    }

    protected int nodeIndex;
    public Basic2DNode.Snapshot currentNode(){
        return pathSnapshot.get(nodeIndex);
    }
    public Vector3i relativePos(){ return currentNode().getPosWithOffset(grid.bottomLeft); }
    public Vector3d relativeCenter(){ return relativeCenter(0);}
    public Vector3d relativeCenter(double yOff){ return DataHelper.ForVector3d.from(relativePos()).add(.5, yOff, .5); }

    public ArrayList<Basic2DNode.Snapshot> pathSnapshot;
    protected ArrayList<Basic2DNode.Snapshot> o_pathSnapshot;
    public void snapshotPathThenSet(ArrayList<Basic2DNode.Snapshot> snap){
        o_pathSnapshot = pathSnapshot;
        pathSnapshot = snap;
    }
    public void restoreLastPath(){
        pathSnapshot = o_pathSnapshot;
    }

    public BiFunction<BlockPos, Level, Boolean> notWalkable;
    public void setBlockedPredicate(BiFunction<BlockPos, Level, Boolean> notWalkable){
        this.notWalkable = notWalkable;
        if (grid != null) grid.notWalkable = notWalkable;
    }
    public boolean testBlocked(BlockPos bPos, Level level){
        return notWalkable == null || notWalkable.apply(bPos, level);
    }

    public boolean decidePath(U user, Vector3i endPos){
        Vector3i userPos = user.posVi();
        endPos.y = userPos.y; //Ensure that the grid is centered right and the end pos can be pathed to

        if (grid == null){
            buildGrid(userPos, endPos);
            grid.fillGrid(user.level());
        }
        else {
            if (!grid.checkSquare(userPos)){
                grid.adjust(userPos, true, grid.sizeX, grid.sizeZ);
                grid.fillGrid(user.level());
            }
            if (!grid.ensureInRange(userPos, endPos)){
                buildGrid(userPos, endPos);
                grid.fillGrid(user.level());
            }
        }

        Basic2DNode path = grid.findPath(user.posVi(), endPos);
        boolean flag = path != null;
        if (flag){
            snapshotPathThenSet(Basic2DNode.Snapshot.snapshotPathToArray(path, true));
            nodeIndex = 0;
            setStep();
        }
        return pathing = flag;
    }

    boolean o_moving;
    public boolean pathing;
    public void tick(){
        if (!pathing) return;
        boolean cMoving = plusControl.isMoving();
        if (stepFlag || (o_moving && !cMoving)){
            if (stepNode() == -1) pathing = false;
            else{
                Vector3d center = relativeCenter();
                plusControl.moveTo(center, (user instanceof INavigation<?,?> nav ? nav.getNavSpeed() : .5), defaultError);
                plusControl.rotateTowards(center);
            }
            stepFlag = false;
        }
        o_moving = cMoving;
    }

    protected boolean stepFlag;
    public void setStep(){ stepFlag = true; }
    public int stepNode(){
        if (!pathing) return -1;
        if (++nodeIndex >= pathSnapshot.size()) nodeIndex = -1;
        return nodeIndex;
    }

    public void positionGrid(Vector3i position, boolean centered, int sizeX, int sizeZ){
        if (grid == null) grid = new Basic2DNodeGrid(position, centered, sizeX, sizeZ, notWalkable);
        else grid.adjust(position, centered, sizeX, sizeZ);
    }

    private void buildGrid(Vector3i userPos, Vector3i endPos){
        if (grid != null) snapshotCurrent(true);
        int sqrSize = decideOptimalSqrSize(userPos, endPos);
        positionGrid(userPos, true, sqrSize, sqrSize);
    }

    protected int decideOptimalSqrSize(Vector3i start, Vector3i end){
        double dist = DataHelper.ForVector3i.flatDistance(start, end);
        if (dist < 4) return ((int)Math.floor(dist) + 1) * 2;
        else{
            int pow2 = 8;
            while (pow2 < dist) pow2 *= 2;

            boolean flag = pow2 - dist > 8;
            pow2 *= 2;
            if (flag) pow2 += 8;
            return pow2;
        }
    }
}
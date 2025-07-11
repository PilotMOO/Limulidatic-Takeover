package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

public interface IPathfindAccess<pathfinder>{
    void pathfindTo(int x, int y, int z, double speed);
    pathfinder getPathfinder();
}

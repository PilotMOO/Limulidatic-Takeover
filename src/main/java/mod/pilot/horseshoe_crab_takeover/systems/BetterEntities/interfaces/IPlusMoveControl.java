package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.PlusMovementControl;

public interface IPlusMoveControl<U extends WorldEntity, P extends PlusMovementControl> {
    P buildMoveControl(U user);
    void setMoveControl(P moveControl);
    P getMoveControl();

    default void buildAndSetMoveControl(U user){
        setMoveControl(buildMoveControl(user));
    }
}

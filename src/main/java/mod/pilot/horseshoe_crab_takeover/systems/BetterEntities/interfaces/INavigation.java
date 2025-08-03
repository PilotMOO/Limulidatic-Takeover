package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.AStarTesting.FlatAStarNavigation;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.PlusMovementControl;

public interface INavigation<U extends WorldEntity, N extends FlatAStarNavigation<U, PlusMovementControl>> {
    N buildNavigation(U user);
    void setNavigation(N navigation);
    N getNavigation();
    double getNavSpeed();

    default void buildAndSetNavigation(U user){
        setNavigation(buildNavigation(user));
    }
}

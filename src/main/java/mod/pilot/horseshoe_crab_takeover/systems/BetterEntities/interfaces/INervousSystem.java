package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.NervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;

public interface INervousSystem<U extends WorldEntity, N extends NervousSystem<?>>{
    N getNervousSystem();
    void setNervousSystem(N nervousSystem);
    N buildNervousSystem(U user);
    N addResponses(U user, N nervousSystem);

    default void buildAndSetNervousSystem(U user){
        setNervousSystem(buildNervousSystem(user));
    }
}

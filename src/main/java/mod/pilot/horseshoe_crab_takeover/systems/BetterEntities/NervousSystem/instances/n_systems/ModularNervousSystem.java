package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.n_systems;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Nerve;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.NervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;

import java.util.function.Predicate;

public abstract class ModularNervousSystem<U extends WorldEntity> extends NervousSystem<U> {
    public abstract void addNerve(Nerve<U, ?> nerve);
    public abstract void severNerve(Nerve<U, ?> nerve);
    public abstract void severIf(Predicate<Nerve<U, ?>> severIf);
}

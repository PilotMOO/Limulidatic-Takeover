package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;

public abstract class NervousSystem<U extends WorldEntity>{
    public abstract void addDefaultNerves(U user);

    public abstract Iterable<Nerve<U, ?>> getNerves();

    public abstract Nerve<U, ?> filter(Stimulant stimulant);
    public abstract void stimulate(U user, Stimulant stimulant);

    public abstract void addActiveResponse(Response.IAlive<U, ?> alive);
    public abstract void tickActiveResponses(U user);
    public abstract Iterable<Response.IAlive<U, ?>> aliveResponses();
}

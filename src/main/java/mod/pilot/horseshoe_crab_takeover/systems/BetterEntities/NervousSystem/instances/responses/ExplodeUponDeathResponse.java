package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.responses;

import mod.pilot.horseshoe_crab_takeover.damagetypes.HorseshoeDamageTypes;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Response;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Stimulant;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

public class ExplodeUponDeathResponse extends Response<WorldEntity, Stimulant.Hurt> {
    @Override
    protected boolean shouldAct(WorldEntity actor, Stimulant.Hurt stimulant) {
        return stimulant.isServerSide() && stimulant.isKilled();
    }

    @Override
    protected void act(WorldEntity actor, Stimulant.Hurt stimulant) {
        actor.level().explode(actor, HorseshoeDamageTypes.crabbed(actor), new ExplosionDamageCalculator(), actor.position(), 5, true, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    public int priority() {
        return 1;
    }
}

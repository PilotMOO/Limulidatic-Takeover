package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.responses;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Response;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Stimulant;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class LightningAttackerResponse extends Response<WorldEntity, Stimulant.Hurt>
        implements Response.IAlive<WorldEntity, Stimulant.Hurt>{
    @Override
    protected boolean shouldAct(WorldEntity actor, Stimulant.Hurt stimulant) {
        return stimulant.isServerSide() && stimulant.getTarget() != null;
    }

    @Override
    protected void act(WorldEntity actor, Stimulant.Hurt stimulant) {
        Entity e = stimulant.target;
        if (e != null) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, actor.level());
            bolt.copyPosition(stimulant.target);
            actor.level().addFreshEntity(bolt);
        }
        stimulant.markAsExpired();
    }

    @Override
    public int priority() {
        return 0;
    }

    Stimulant.Hurt stimulant;
    int age = 0;

    @Override
    public void rememberStimulant(Stimulant.Hurt stimulant) {
        this.stimulant = stimulant;
    }

    @Override
    public Stimulant.Hurt retrieveStimulant() {
        return stimulant;
    }

    @Override
    public void alive(WorldEntity actor, Stimulant.Hurt stimulant) {
        Entity e = stimulant.target;
        if (e != null) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, actor.level());
            bolt.copyPosition(stimulant.target);
            actor.level().addFreshEntity(bolt);
        }
    }

    @Override
    public boolean survives(WorldEntity actor, Stimulant.Hurt stimulant) {
        return ++age < 20;
    }

    @Override
    public void death(WorldEntity actor, Stimulant.Hurt stimulant) {
        this.stimulant = null;
        age = 0;
        stimulant.markAsExpired();
    }
}

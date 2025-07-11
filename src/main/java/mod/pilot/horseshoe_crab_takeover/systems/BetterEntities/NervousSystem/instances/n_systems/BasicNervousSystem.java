package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.n_systems;

import com.google.common.collect.ImmutableList;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Nerve;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.NervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Response;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Stimulant;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BasicNervousSystem extends NervousSystem<WorldEntity> {
    public Nerve<WorldEntity, Stimulant.Idle> IDLE_NERVE;
    public Nerve<WorldEntity, Stimulant.Aggressive> AGGRO_NERVE;
    public Nerve<WorldEntity, Stimulant.Target> TARGET_NERVE;
    public Nerve<WorldEntity, Stimulant.Hurt> HURT_NERVE;
    public ImmutableList<Nerve<WorldEntity, ?>> NERVES;

    @Override
    public void addDefaultNerves(WorldEntity user) {
        IDLE_NERVE = new Nerve<>(Stimulant.Idle.class).assignParent(this);
        AGGRO_NERVE = new Nerve<>(Stimulant.Aggressive.class).assignParent(this);
        TARGET_NERVE = new Nerve<>(Stimulant.Target.class).assignParent(this);
        HURT_NERVE = new Nerve<>(Stimulant.Hurt.class).assignParent(this);
        NERVES = ImmutableList.of(IDLE_NERVE, AGGRO_NERVE, TARGET_NERVE, HURT_NERVE);

        this.aliveResponses = new ArrayList<>();
        this.que_addition = new ArrayList<>();
        this.que_removal = new ArrayList<>();
    }

    @Override
    public Iterable<Nerve<WorldEntity, ?>> getNerves() {
        return NERVES;
    }

    @Override
    public @Nullable Nerve<WorldEntity, ?> filter(Stimulant stimulant) {
        return switch (stimulant.getType()){
            case OTHER -> null;
            case IDLE -> IDLE_NERVE;
            case AGGRESSIVE -> AGGRO_NERVE;
            case TARGET -> TARGET_NERVE;
            case HURT -> HURT_NERVE;
        };
    }

    @Override
    public void stimulate(WorldEntity user, Stimulant stimulant) {
        Nerve<WorldEntity, ?> n = filter(stimulant);
        if (n != null && n.testStimulant(stimulant)) n.stimulateUnsafe(user, stimulant);
    }

    public ArrayList<Response.IAlive<WorldEntity, ?>> aliveResponses;
    protected ArrayList<Response.IAlive<WorldEntity, ?>> que_addition;
    protected ArrayList<Response.IAlive<WorldEntity, ?>> que_removal;
    protected boolean add, remove;

    @Override
    public Iterable<Response.IAlive<WorldEntity, ?>> aliveResponses() {
        return aliveResponses;
    }
    @Override
    public void addActiveResponse(Response.IAlive<WorldEntity, ?> alive) {
        que_addition.add(alive);
        add = true;
    }
    @Override
    public void tickActiveResponses(WorldEntity user) {
        manageQues();
        for (Response.IAlive<WorldEntity, ?> alive : aliveResponses()){
            if (alive.survives(user)) alive.alive(user);
            else{
                alive.death(user);
                que_removal.add(alive);
                remove = true;
            }
        }
    }
    protected void manageQues(){
        if (add){
            aliveResponses.addAll(que_addition);
            que_addition.clear();
            add = false;
        }
        if (remove){
            aliveResponses.removeAll(que_removal);
            que_removal.clear();
            remove = false;
        }
    }
}

package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * A response, the alterative to {@link net.minecraft.world.entity.ai.goal.Goal} for the {@link NervousSystem}.
 * responses are nested inside of {@link Nerve}s and are triggered in descending priority
 * until either there are no more responses or if the {@link Stimulant} is marked as expired
 * <p>By default, responses are "instant"-- their logic is fired immediately and won't be updated further
 * until being stimulated again. See {@link Response.IAlive} for continuous updates</p>
 * @param <U> The entity (super)class that the Response expects to be used by. By default, extends {@link WorldEntity}.
 *          Must be the same or a subclass of the same variable in the encompassing {@link NervousSystem} and {@link Nerve}
 * @param <S> The related (super)class of the {@link Stimulant}
 *           that is used to "stimulate" (trigger) this Response.
 *           Stimulants contain some information and context about why the Nerve was stimulated
 */
public abstract class Response<U extends WorldEntity, S extends Stimulant> {
    /**
     * Checks if the Response should even fire.
     * <p>Equivalent to {@link Goal#canUse()}</p>
     * @param actor The {@link U} (entity) that the Response is being used by
     * @param stimulant The related {@link Stimulant} containing the information of the stimulation
     * @return {@code true} if the Response should fire
     */
    protected abstract boolean shouldAct(U actor, S stimulant);

    /**
     * Manages the basic logic of the Response.
     * Gets called immediately upon being stimulated IF {@link Response#shouldAct(WorldEntity, Stimulant)} return true.
     * <p>Equivalent to {@link Goal#start()}</p>
     * @param actor The {@link U} (entity) that the Response is being used by
     * @param stimulant The related {@link Stimulant} containing the information of the stimulation
     */
    protected abstract void act(U actor, S stimulant);

    /**
     * An external call to start the Response. Handles internal logic before actually starting the Response
     * @param actor The {@link U} (entity) that the Response is being used by
     * @param stimulant The related {@link Stimulant} containing the information of the stimulation
     * @return {@code true} if the Response acted
     */
    public boolean begin(U actor, S stimulant){
        boolean flag = shouldAct(actor, stimulant);
        System.out.println("[RESPONSE] Trying to begin Response with stimulant type [" + stimulant.getType() + "]... success? [" + flag + "]");
        if (flag) act(actor, stimulant);
        return flag;
    }

    /**
     * The "priority" of the Response-- responses with higher priority will be invoked first.
     * <p>responses can mark the passing {@link Stimulant} as expired. Expired stimulants will be ignored.
     * This allows higher priority responses to "filter" stimulants and prevent lower-priority responses from firing, if so desired.</p>
     * @return the integer priority of the Response. Higher priority values get triggered first.
     */
    public abstract int priority();

    /**
     * An interface for making continuous "Living" responses. Living responses continue to get updated every tick after being stimulated
     * until stopped.
     * <p>Intended to be implemented SOLELY by objects extending {@link Response} or a subclass of</p>
     * @param <U> The entity (super)class that the Response expects to be used by. By default, extends {@link WorldEntity}.
     *          Must be the same or a subclass of the same variable in the encompassing {@link NervousSystem} and {@link Response}.
     *          Must be kept the same as the type parameters of the {@link Response} subclass this interface is implemented in
     * @param <S> The related (super)class of the {@link Stimulant}
     *           that is used to "stimulate" (trigger) this Response.
     *           Stimulants contain some information and context about why the Nerve was stimulated.
     *           Must be kept the same as the type parameters of the {@link Response} subclass this interface is implemented in
     */
    public interface IAlive<U extends WorldEntity, S extends Stimulant> {
        /**
         * A hook to "remember" (keep track of) the stimulant that initially "revived" this Response
         * @param stimulant The related {@link Stimulant} containing the information of the stimulation
         * */
        void rememberStimulant(S stimulant);

        /**
         * Returns the stored {@link Stimulant} that was remembered by {@link IAlive#rememberStimulant(Stimulant)}
         * @return the remembered {@link Stimulant}
         */
        S retrieveStimulant();

        /**
         * Gets updated every tick that this Response is "Alive" for
         * <p>Equivalent to {@link Goal#tick()}</p>
         * @param actor The {@link U} (entity) that the Response is being used by
         * @param stimulant The related {@link Stimulant} containing the information of the initial stimulation
         */
        void alive(U actor, S stimulant);
         /**
          * Shorthand, invokes {@link Response.IAlive#alive(WorldEntity, Stimulant)}
          * feeding in {@link IAlive#retrieveStimulant()} for the second argument
          * @param actor The {@link U} (entity) that the Response is being used by
          */
        default void alive(U actor) { alive(actor, retrieveStimulant()); }

        /**
         * Dictates if the Response "survives" this tick. If true, {@link Response.IAlive#alive(WorldEntity, Stimulant)} will be triggered.
         * Otherwise, this Response will "die" and won't be updated until "revived" (stimulated again)
         * <p><p>Equivalent to {@link Goal#canContinueToUse()}</p></p>
         * @param actor The {@link U} (entity) that the Response is being used by
         * @param stimulant The related {@link Stimulant} containing the information of the initial stimulation
         * @return {@code true} if the Response survives this tick and continues to be updated
         */
        boolean survives(U actor, S stimulant);
         /**
          * Shorthand, invokes {@link Response.IAlive#survives(WorldEntity, Stimulant)}
          * feeding in {@link IAlive#retrieveStimulant()} for the second argument
          * @param actor The {@link U} (entity) that the Response is being used by
          * @return {@code true} if the Response survives this tick and continues to be updated
          */
        default boolean survives(U actor){ return survives(actor, retrieveStimulant()); }

        /**
         * Is called upon a Living Response "dying", as decided by {@link Response.IAlive#survives(WorldEntity, Stimulant)}
         * <p>Equivalent to {@link Goal#stop()}</p>
         * @param actor The {@link U} (entity) that the Response is being used by
         * @param stimulant The related {@link Stimulant} containing the information of the initial stimulation
         */
        void death(U actor, S stimulant);
         /**
          * Shorthand, invokes {@link Response.IAlive#death(WorldEntity, Stimulant)}
          * feeding in {@link IAlive#retrieveStimulant()} for the second argument
          * @param actor The {@link U} (entity) that the Response is being used by
          */
        default void death(U actor) { death(actor, retrieveStimulant());}
    }
}

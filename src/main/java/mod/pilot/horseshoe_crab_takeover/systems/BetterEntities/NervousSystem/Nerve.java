package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem;

import com.google.common.collect.ImmutableList;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * A nerve to be used in a {@link NervousSystem}-- holds a prioritized list of {@link Response}s
 * and triggers all of them in order of priority upon being stimulated.
 * <P></P>responses may "expire" a given stimulant so that it won't trigger responses that have a lower priority
 * @param <U> The entity (super)class that the Nerve and all of its responses can be used in.
 *          Must be the same or a subclass of the same variable in the encompassing {@link NervousSystem}
 *          as well as all contained {@link Response}s
 * @param <S> The related (super)class of the {@link Stimulant}
 *           that is used to "stimulate" (trigger) all the containing {@link Response}s.
 *           Must be the same or a super class of the same class as dictated by the contained {@link Response}'s parameters.
 */
public class Nerve<U extends WorldEntity, S extends Stimulant>{
    /**
     * Creates an empty Nerve with an initial {@link Response} capacity of 0
     * @param clazz The class of the type of stimulant this Nerve responds to
     */
    public Nerve(Class<S> clazz){ this(clazz, 0); }
    /**
     * Creates a new Nerve with a predefined {@link Response} list size for better computing
     * if the immediate amount of responses to be added is known
     * @param clazz The class of the type of stimulant this Nerve responds to
     * @param expectedCount the amount of {@link Response}s expected to be added to the Nerve immediately after construction
     */
    public Nerve(Class<S> clazz, int expectedCount){ this(clazz, new ArrayList<>(expectedCount)); }

    /**
     * Creates a Nerve with a pre-defined list of {@link Response}s
     * @param clazz The class of the type of stimulant this Nerve responds to
     * @param responses an arraylist of {@link Response}s for this Nerve to use
     */
    public Nerve(Class<S> clazz, ArrayList<Response<U, S>> responses){
        this.vResponses = responses;
        this.Responses = ImmutableList.copyOf(vResponses);
        A_QUE = new ArrayList<>(); R_QUE = new ArrayList<>();
        a_q = r_q = false;
        this.stimulantClass = clazz;
    }

    /**
     * The NervousSystem this Nerve exists within
     */
    public NervousSystem<U> parentSystem;
    /**
     * Returns the {@link NervousSystem} this Nerve exists within
     * @return the {@link NervousSystem} this Nerve exists within
     */
    public NervousSystem<U> getParent() { return parentSystem; }
    /**
     * Sets the parent {@link NervousSystem} of this Nerve
     * @param parent the {@link NervousSystem} to set the parent to
     * @return itself
     */
    public Nerve<U, S> assignParent(NervousSystem<U> parent) { this.parentSystem = parent; return this; }

    /**
     * The class of the type of stimulant this Nerve responds to
     */
    public final Class<S> stimulantClass;
    /**
     * Accessor for the encompassing class this Nerve responds to
     * @return The class of the type of stimulant this Nerve responds to
     */
    public Class<S> getStimulantClass(){ return stimulantClass; }
    /**
     * Tests if the given {@link Stimulant} is a stimulant this Nerve responds to
     * @param stimulant The {@link Stimulant} to test
     * @return {@code true} if the Nerve should respond to this stimulant, else {@code false}
     */
    public boolean testStimulant(Stimulant stimulant){ return getStimulantClass().isInstance(stimulant); }

    /**
     * The protected {@code VOLATILE} arraylist of {@link Response}s this nerve manages
     * <p>Do NOT directly modify this variable unless you are certain it won't cause a {@link java.util.ConcurrentModificationException}.</p>
     * See {@link Nerve#Responses} for external access,
     * {@link Nerve#addResponse(Response)}, {@link Nerve#removeResponse(Response)},
     * and {@link Nerve#removeResponsesIf(Predicate)} for external modification.
     */
    protected final ArrayList<Response<U, S>> vResponses;
    /**
     * A public immutable copy of {@link Nerve#vResponses}, for accessing externally.
     * <p>This list is {@code IMMUTABLE} and cannot be used to directly add/remove/modify {@link Response}s within this nerve.</p>
     * See {@link Nerve#addResponse(Response)}, {@link Nerve#removeResponse(Response)},
     * and {@link Nerve#removeResponsesIf(Predicate)} for external modification.
     */
    public ImmutableList<Response<U, S>> Responses;

    /**
     * Internal flags for checking if there are requests to add or remove a response from this nerve.
     * <p>{@link Nerve#a_q} for adding, {@link Nerve#r_q} for removing</p>
     */
    protected boolean a_q, r_q;
    /**
     * A protected arraylist of responses that will be added to the volatile list once the Nerve knows it is safe to
     * <p>Please use {@link Nerve#addResponse(Response)} for queuing additions rather than directly modifying this variable</p>
     */
    protected ArrayList<Response<U, S>> A_QUE;
    /**
     * A protected arraylist of responses that will be removed from the volatile list once the Nerve knows it is safe to
     * <p>Please use {@link Nerve#removeResponse(Response)} and {@link Nerve#removeResponsesIf(Predicate)}
     * for queuing removals rather than directly modifying this variable</p>
     */
    protected ArrayList<Response<U, S>> R_QUE;

    /**
     * Adds a given {@link Response} to the que to be added once the Nerve knows it is safe
     * @param response the given {@link Response} to add
     */
    public void addResponse(Response<U, S> response){ A_QUE.add(response); a_q = true; }

    /**
     * Adds a given {@link Response} to the que to be removed once the Nerve knows it is safe
     * @param response the given {@link Response} to remove
     */
    public void removeResponse(Response<U, S> response){ R_QUE.add(response); r_q = true; }

    /**
     * Shifts through all currently contained {@link Response}s and adds all the ones that match the predicate to the removal que
     * @param _if the predicate that dictates if a given response should be removed
     */
    public void removeResponsesIf(Predicate<Response<U, S>> _if){
        boolean flag = false;
        for (Response<U, S> r1 : Responses) {
            if (_if.test(r1)) {
                R_QUE.add(r1);
                flag = true;
            }
        }
        r_q = r_q || flag;
    }

    /**
     * An unsafe and unchecked call to {@link Nerve#stimulate(WorldEntity, Stimulant)}
     * that assumes the second argument is of or extends the class defined by the second type parameter
     * <p>See {@link Nerve#stimulateGuarded(WorldEntity, Object)} if you are uncertain if the cast is safe</p>
     * ONLY casts, it does NOT invoke {@link Nerve#testStimulant(Stimulant)} before stimulating
     * @param user The {@link U} (entity) that the responses are being used by
     * @param stimulant An object that is ASSUMED TO BE the related {@link Stimulant} containing the information of the stimulation
     */
    public void stimulateUnsafe(U user, Object stimulant){
        stimulate(user, getStimulantClass().cast(stimulant));
    }

    /**
     * A guarded invoke of {@link Nerve#stimulate(WorldEntity, Stimulant)}
     * with an unidentified object that is tested if it is a valid class for stimulating this Nerve.
     * If the object can be safely cast to a stimulant, the object will be cast then used to stimulate the Nerve, else this method does nothing
     * <p>See {@link Nerve#stimulateUnsafe(WorldEntity, Object)} if you already know the given object is safe for casting</p>
     * ONLY checks if casting is safe, it does NOT invoke {@link Nerve#testStimulant(Stimulant)} before stimulating
     * @param user The {@link U} (entity) that the responses are being used by
     * @param stimulant An object that MIGHT BE the related {@link Stimulant} containing the information of the stimulation
     */
    public void stimulateGuarded(U user, Object stimulant){
        Class<S> clazz = getStimulantClass();
        if (clazz.isInstance(stimulant)) stimulate(user, clazz.cast(stimulant));
    }

    /**
     * An external call to stimulate (trigger) the nerve.
     * <p>{@link Response}s with higher priority will be triggered first,
     * and if any of them pre-maturely expires the stimulant, other responses with lower priority will NOT be stimulated</p>
     * @param user The {@link U} (entity) that the responses are being used by
     * @param stimulant The related {@link Stimulant} containing the information of the stimulation
     */
    public void stimulate(U user, S stimulant){
        stimulate_internal(user, stimulant);
    }

    /**
     * The internal logic handler of the Nerve-- checks and cleans out the ques, before stimulating all valid {@link Response}s
     * <p>{@link Response}s with higher priority will be triggered first,
     * and if any of them pre-maturely expires the stimulant, other responses with lower priority will NOT be stimulated</p>
     * @param user The {@link U} (entity) that the responses are being used by
     * @param packet The related {@link Stimulant} containing the information of the stimulation
     */
    @SuppressWarnings("unchecked")
    protected final void stimulate_internal(U user, S packet){
        cleanQues();
        for (Response<U, S> s1 : vResponses){
            if (s1.begin(user, packet) && s1 instanceof Response.IAlive<?,?> alive){
                Response.IAlive<U, S> alive1 = (Response.IAlive<U, S>)alive;
                alive1.rememberStimulant(packet);
                getParent().addActiveResponse(alive1);
            }
            if (packet.isExpired()) break;
        }
    }

    /**
     * Checks and cleans out the ques for this Nerve, adding all queued additions and removing all queued removals,
     * before sorting all the {@link Response}s in order by their priority
     * <p>This WILL cause a {@link java.util.ConcurrentModificationException} if invoked at the wrong time,
     * I.E. while the volatile arraylist is being iterated over</p>
     */
    public void cleanQues(){
        boolean flag = false;
        if (a_q) {
            vResponses.addAll(A_QUE);
            A_QUE.clear();
            a_q = false;
            flag = true;
        }
        if (r_q) {
            vResponses.removeAll(R_QUE);
            R_QUE.clear();
            r_q = false;
            flag = true;
        }
        if (flag) {
            vResponses.sort(Comparator.comparingInt(Response::priority));
            Responses = ImmutableList.copyOf(vResponses);
        }
    }
}

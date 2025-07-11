package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Stimulant {
    private Stimulant(boolean server){ this.server = server; }
    public final boolean server;
    protected boolean expired;

    public void markAsExpired() { expired = true; }
    public boolean isExpired() { return expired; }
    public boolean isServerSide() { return server; }

    public abstract Type getType();

    public enum Type {
        OTHER,
        IDLE,
        AGGRESSIVE,
        TARGET,
        HURT;
    }

    public static class Idle extends Stimulant{
        protected Idle(boolean server, boolean aggressive) {
            super(server);
            this.aggressive = aggressive;
        }

        public final boolean aggressive;
        public boolean isAggressive(){ return aggressive; }
        @Override public Type getType() { return Type.IDLE; }
    }
    public static class Aggressive extends Stimulant{
        protected Aggressive(boolean server, LivingEntity target) {
            super(server);
            this.target = target;
        }

        public final LivingEntity target;
        public LivingEntity getTarget() { return target; }
        @Override public Type getType() { return Type.AGGRESSIVE; }
    }
    public static class Target extends Stimulant{
        protected Target(boolean server, LivingEntity target, boolean lost) {
            super(server);
            this.target = target;
            this.lost = lost;
        }

        public final LivingEntity target;
        public LivingEntity getTarget() { return target; }
        public final boolean lost;
        public boolean lostTarget(){ return lost; }
        @Override public Type getType() { return Type.TARGET; }
    }
    public static class Hurt extends Stimulant{
        protected Hurt(boolean server, @Nullable Entity target, DamageSource source, boolean killed) {
            super(server);
            this.target = target;
            this.source = source;
            this.killed = killed;
        }

        public final @Nullable Entity target;
        public @Nullable Entity getTarget() { return target; }
        public final DamageSource source;
        public DamageSource getSource() { return source; }
        public final boolean killed;
        public final boolean isKilled(){ return killed; }
        @Override public Type getType() { return Type.HURT; }
    }

    public static @NotNull Idle IDLE(boolean server, boolean aggressive){ return new Idle(server, aggressive); }
    public static @NotNull Stimulant.Aggressive AGGRESSIVE(boolean server, LivingEntity target){ return new Aggressive(server, target); }
    public static @NotNull Stimulant.Target TARGET(boolean server, LivingEntity target, boolean lost){ return new Target(server, target, lost); }
    public static @NotNull Hurt HURT(boolean server, Entity target, DamageSource source, boolean killed){ return new Hurt(server, target, source, killed); }
}

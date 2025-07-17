package mod.pilot.horseshoe_crab_takeover.entities;

import mod.pilot.horseshoe_crab_takeover.damagetypes.HorseshoeDamageTypes;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.Stimulant;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.n_systems.BasicNervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.responses.ExplodeUponDeathResponse;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.instances.responses.LightningAttackerResponse;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces.INervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces.IPlusMoveControl;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.PlusMovementControl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class ModifiedHorseshoeCrabEntity extends WorldEntity
        implements INervousSystem<ModifiedHorseshoeCrabEntity, BasicNervousSystem>,
        IPlusMoveControl<ModifiedHorseshoeCrabEntity, PlusMovementControl> {
    public ModifiedHorseshoeCrabEntity(EntityType<? extends WorldEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        RandomizeSize();
        buildAndSetNervousSystem(this);
        buildAndSetMoveControl(this);
    }

    public final AnimationState walkAnimationState = new AnimationState();
    public static final EntityDataAccessor<Float> Size = SynchedEntityData.defineId(ModifiedHorseshoeCrabEntity.class, EntityDataSerializers.FLOAT);
    public float getSize(){
        return entityData.get(Size);
    }
    public void setSize(float size){
        entityData.set(Size, size);
    }
    public void RandomizeSize(){
        setSize((float) getRandom().nextInt(5, 20) / 10);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Size", entityData.get(Size));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(Size, tag.getFloat("Size"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Size, 1f);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return ModifiedHorseshoeCrabEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5D)
                .add(Attributes.ARMOR, 8)
                .add(Attributes.FOLLOW_RANGE, 128)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 5D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.ATTACK_SPEED, 2D);
    }

    /*@Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1, 80));
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false,
                (E) -> !(E instanceof ModifiedHorseshoeCrabEntity)));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true));
    }*/

    @Override
    public @NotNull MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    /*protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new WallClimberNavigation(this, pLevel);
    }*/

    public boolean isMoving() {
        Vec3 delta = getDeltaMovement();
        return delta.x != 0 || delta.y != 0;
    }
    public double getAverageHorizontalMovementSpeed(){
        Vec3 delta = getDeltaMovement();
        return (delta.x + delta.y) / 2;
    }


    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide){
            if (isMoving()){
                walkAnimationState.startIfStopped(tickCount);
            }
            else{
                walkAnimationState.stop();
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        boolean server = !level().isClientSide;
        boolean aggro = getTarget() != null;
        BasicNervousSystem ns = getNervousSystem();
        ns.stimulate(this, Stimulant.IDLE(server, aggro));
        if (aggro) ns.stimulate(this, Stimulant.AGGRESSIVE(server, getTarget()));

        getNervousSystem().tickActiveResponses(this);
        getMoveControl().tick();
    }

    @Override
    public void setTarget(LivingEntity target) {
        boolean noTarget = target == null;
        LivingEntity prior = getTarget();

        super.setTarget(target);

        getNervousSystem().stimulate(this, Stimulant.TARGET(!level().isClientSide, noTarget ? prior : target, noTarget));
    }

    @Override
    public boolean onClimbable() {
        if (getTarget() != null){
            return horizontalCollision && getTarget().position().y > position().y;
        }
        return false;
    }
    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float pAmount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof ModifiedHorseshoeCrabEntity){
            return false;
        }
        if (attacker instanceof LivingEntity LE && getLastHurtByMob() != LE){
            alertNearby(LE);
        }
        boolean hurt = super.hurt(source, pAmount);
        if (hurt) getNervousSystem().stimulate(this, Stimulant.HURT(!level().isClientSide, source.getEntity(), source, isDeadOrDying()));

        Vec3 sourceP = source.getSourcePosition();
        if (sourceP != null) getMoveControl().rotateTowards(sourceP, 30f);

        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);

        boolean flag = target.hurt(HorseshoeDamageTypes.crabbed(this), f);
        if (flag) {
            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }
        if (flag && target instanceof LivingEntity LE){
            LE.invulnerableTime = 0;
            if (getLastHurtMob() != LE){
                alertNearby(LE);
            }
        }

        return flag;
    }

    @Override
    public boolean killedEntity(@NotNull ServerLevel server, @NotNull LivingEntity target) {
        int crabCount = 0;
        int maxHP = (int)target.getAttributeValue(Attributes.MAX_HEALTH);
        while (maxHP > 0){
            crabCount++;
            maxHP -= 5;
        }
        Vec3 crabPos = target.position();
        for (int i = 0; i < crabCount; i++){
            propagateAt(crabPos, server);
            crabPos = crabPos.add(0, 0.5f, 0);
        }
        server.playSound(null, target.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 2.0f, 1.5f);

        return true;
    }

    public static void propagateAt(Vec3 pos, Level level){
        ModifiedHorseshoeCrabEntity crab = HorseshoeEntities.HORSESHOE_CRAB.get().create(level);
        assert crab != null;
        crab.moveTo(pos);
        level.addFreshEntity(crab);
    }
    public void alertNearby(LivingEntity newTarget){
        for (ModifiedHorseshoeCrabEntity crab : level().getEntitiesOfClass(ModifiedHorseshoeCrabEntity.class,
                getBoundingBox().inflate(getAttributeValue(Attributes.FOLLOW_RANGE)),
                (C) -> C.getTarget() == null)){
            crab.setTarget(newTarget);
        }
    }

    public BasicNervousSystem nervousSystem;

    @Override public BasicNervousSystem getNervousSystem() { return nervousSystem; }
    @Override public void setNervousSystem(BasicNervousSystem nervousSystem) { this.nervousSystem = nervousSystem; }
    @Override
    public BasicNervousSystem buildNervousSystem(ModifiedHorseshoeCrabEntity user) {
        BasicNervousSystem ns = new BasicNervousSystem();
        ns.addDefaultNerves(user);
        return addResponses(this, ns);
    }

    @Override
    public BasicNervousSystem addResponses(ModifiedHorseshoeCrabEntity user, BasicNervousSystem nervousSystem) {
        /*nervousSystem.HURT_NERVE.addResponse(new ExplodeUponDeathResponse());
        nervousSystem.HURT_NERVE.addResponse(new LightningAttackerResponse());
        nervousSystem.HURT_NERVE.cleanQues();*/
        return nervousSystem;
    }


    public PlusMovementControl moveControl;
    @Override public PlusMovementControl buildMoveControl(ModifiedHorseshoeCrabEntity user) { return new PlusMovementControl(user); }
    @Override public void setMoveControl(PlusMovementControl moveControl) { this.moveControl = moveControl; }
    @Override public PlusMovementControl getMoveControl() { return moveControl; }
}

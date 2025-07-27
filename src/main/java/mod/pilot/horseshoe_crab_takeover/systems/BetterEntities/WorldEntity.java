package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces.IEquipment;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.ArrayList;

public abstract class WorldEntity extends LivingEntity implements Targeting {
    protected WorldEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Vector3d deltaMovement = new Vector3d();
    public void setDeltaMovement(@NotNull Vector3d deltaMovement) { setDeltaMovement(deltaMovement.x, deltaMovement.y, deltaMovement.z); }
    public void setDeltaMovement(double x, double y, double z) { DataHelper.ForVector3d.copy(deltaMovement, x, y, z); }
    @Override public void setDeltaMovement(@NotNull Vec3 pDeltaMovement) { DataHelper.ForVector3d.copy(deltaMovement, pDeltaMovement); }
    @Override public @NotNull Vec3 getDeltaMovement() { return DataHelper.ForVec3.from(deltaMovement); }
    public Vector3d getDeltaMovement3d() { return deltaMovement; }

    public Vector3i posVi(){
        return DataHelper.ForVector3i.from(position());
    }

    public @Nullable LivingEntity target;

    @Override
    public @Nullable LivingEntity getTarget() {
        return target;
    }
    public void setTarget(LivingEntity target){
        LivingChangeTargetEvent changeTargetEvent = ForgeHooks.onLivingChangeTarget(this, target,
                LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if(!changeTargetEvent.isCanceled()) {
            this.target = changeTargetEvent.getNewTarget();
        }
    }

    //See IEquipment for managing equipment
    protected static final Iterable<ItemStack> _EMPTY = new ArrayList<>();
    @Override public @NotNull Iterable<ItemStack> getArmorSlots() {
        if (this instanceof IEquipment iEq) return iEq.getArmorSlots();
        else return _EMPTY;
    }
    @Override public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot) {
        if (this instanceof IEquipment iEq) return iEq.getItemBySlot(slot);
        else return ItemStack.EMPTY;
    }
    @Override public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        if (this instanceof IEquipment iEq) iEq.setItemSlot(slot, stack);
    }
    private static final HumanoidArm DEFAULT_ARM = HumanoidArm.RIGHT;
    @Override public @NotNull HumanoidArm getMainArm() {
        if (this instanceof IEquipment iEq) return iEq.getMainArm();
        return DEFAULT_ARM;
    }
}

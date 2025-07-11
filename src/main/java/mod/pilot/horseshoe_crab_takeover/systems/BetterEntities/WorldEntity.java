package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities;

import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.NervousSystem.NervousSystem;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces.IEquipment;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class WorldEntity extends LivingEntity implements Targeting {
    protected WorldEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public @Nullable LivingEntity target;

    @Override
    public @Nullable LivingEntity getTarget() {
        return target;
    }
    public void setTarget(LivingEntity target){
        net.minecraftforge.event.entity.living.LivingChangeTargetEvent changeTargetEvent = net.minecraftforge.common.ForgeHooks.onLivingChangeTarget(
                        this, target, net.minecraftforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if(!changeTargetEvent.isCanceled()) {
            this.target = changeTargetEvent.getNewTarget();
        }
    }

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

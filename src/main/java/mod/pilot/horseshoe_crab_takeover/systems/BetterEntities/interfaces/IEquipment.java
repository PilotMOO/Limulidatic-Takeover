package mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.interfaces;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface IEquipment{
    Iterable<ItemStack> getArmorSlots();
    ItemStack getItemBySlot(EquipmentSlot pSlot);
    void setItemSlot(EquipmentSlot pSlot, ItemStack pStack);
    HumanoidArm getMainArm();
}


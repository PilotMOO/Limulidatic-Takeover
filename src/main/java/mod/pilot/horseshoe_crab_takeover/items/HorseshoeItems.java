package mod.pilot.horseshoe_crab_takeover.items;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HorseshoeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Horseshoe_Crab_Takeover.MOD_ID);

    public static final RegistryObject<Item> HORSESHOE_CRAB_SPAWNEGG = ITEMS.register("horseshoe_crab_spawn",
            () -> new ForgeSpawnEggItem(HorseshoeEntities.HORSESHOE_CRAB, -1, -1, new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}

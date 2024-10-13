package mod.pilot.horseshoe_crab_takeover.items;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class HorseshoeCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Horseshoe_Crab_Takeover.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    public static final RegistryObject<CreativeModeTab> HORSESHOE_TAB = CREATIVE_MODE_TABS.register("horseshoe_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3).icon(() -> new ItemStack(HorseshoeItems.HORSESHOE_CRAB_SPAWNEGG.get()))
                    .title(Component.translatable("creativetab.horseshoe_tab"))
                    .displayItems((something, register) ->{
                        register.accept(HorseshoeItems.HORSESHOE_CRAB_SPAWNEGG.get());
                    })
                    .build());
}

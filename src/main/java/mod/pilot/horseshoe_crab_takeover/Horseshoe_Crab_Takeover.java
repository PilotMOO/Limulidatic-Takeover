package mod.pilot.horseshoe_crab_takeover;

import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeCreativeTabs;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeItems;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.ReversibleArray;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Horseshoe_Crab_Takeover.MOD_ID)
public class Horseshoe_Crab_Takeover
{
    public static final String MOD_ID = "horseshoe_crab_takeover";

    public static HorseshoeWorldData activeData;

    public Horseshoe_Crab_Takeover()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HorseshoeEntities.register(modEventBus);
        HorseshoeItems.register(modEventBus);
        HorseshoeCreativeTabs.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC, "HCT_Config.toml");
        Config.loadConfig(Config.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("HCT_Config.toml").toString());
    }
}

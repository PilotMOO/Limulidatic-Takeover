package mod.pilot.horseshoe_crab_takeover;

import com.mojang.logging.LogUtils;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeCreativeTabs;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeItems;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Horseshoe_Crab_Takeover.MOD_ID)
public class Horseshoe_Crab_Takeover
{
    public static final String MOD_ID = "horseshoe_crab_takeover";
    //private static final Logger LOGGER = LogUtils.getLogger();

    public static HorseshoeWorldData activeData;

    public Horseshoe_Crab_Takeover()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HorseshoeEntities.register(modEventBus);
        HorseshoeItems.register(modEventBus);
        HorseshoeCreativeTabs.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC, "HCT_Config.toml");
        Config.loadConfig(Config.SERVER_SPEC, FMLPaths.CONFIGDIR.get().resolve("HCT_Config.toml").toString());

        double x1 = 0, x2 = 0.5, x3 = 1;
        double x4 = x2, x5 = x3;
        x4 *= -1; x5 *= -1;
        System.out.println("Vector and x");
    }
}

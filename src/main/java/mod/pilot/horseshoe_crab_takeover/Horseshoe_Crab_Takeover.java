package mod.pilot.horseshoe_crab_takeover;

import com.mojang.logging.LogUtils;
import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
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

import java.util.Random;

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

        //Left-over debugging code for testing various bitwise helpers
        /*Random random = new Random();

        long bits, bits2, bits3, bits4;
        bits = bits2 = bits3 = bits4 = -1L;
        long[] pack = new long[4];
        pack[0] = bits; pack[1] = bits2; pack[2] = bits3; pack[3] = bits4;
        System.out.println("Bits: [" + Long.toBinaryString(pack[0]) +", "+ Long.toBinaryString(pack[1]) +", "+ Long.toBinaryString(pack[2]) +", "+ Long.toBinaryString(pack[3]) +"]");
        long[] inks = new long[2];
        inks[0] = (random.nextLong() | 1L) | 1L << 63; inks[1] = (random.nextLong() | 1L) | 1L << 63;
        System.out.println("ink: [" + Long.toBinaryString(inks[0]) +", "+ Long.toBinaryString(inks[1]) +"]");
        try {
            DataHelper.mergeBitSentences(pack, 32, inks, 70);
        } catch (DataHelper.InvalidBitWriteOperation ignored) {}
        System.out.println("Bits post-write: [" + Long.toBinaryString(pack[0]) +", "+ Long.toBinaryString(pack[1]) +", "+ Long.toBinaryString(pack[2]) +", "+ Long.toBinaryString(pack[3]) +"]");*/
    }
}

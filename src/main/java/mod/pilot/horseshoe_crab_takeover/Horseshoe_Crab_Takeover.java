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

        Random random = new Random();

        long writeRangeTest = 0L;
        long g = (-1L << 48) >>> 48;
        System.out.println("Bits: " + Long.toBinaryString(writeRangeTest));
        System.out.println("to write: " + Long.toBinaryString(g));
        System.out.println("written: " + Long.toBinaryString(DataHelper.writeRange(writeRangeTest, 8, g, 16)));

        long bits = random.nextLong() | (1L << 63);
        System.out.println("BITS: " + Long.toBinaryString(bits));
        long bits2 = (random.nextLong() | 1L) | (1L << 63);
        System.out.println("BITS 2: " + Long.toBinaryString(bits2));

        long mark = -1L << 48;
        System.out.println("marker: " + Long.toBinaryString(mark));

        long[] sentence = new long[2];
        sentence[0] = bits; sentence[1] = bits2;

        //For some reason writeRangeToSentence(args...) is working entirely inversely to how it should. :/
        try {
            DataHelper.writeRangeToSentence(sentence, 48, mark, 32);
            System.out.println("mod1: " + Long.toBinaryString(sentence[0]));
            System.out.println("mod2: " + Long.toBinaryString(sentence[1]));
        } catch (DataHelper.InvalidBitWriteOperation e) {
            throw new RuntimeException(e);
        }
    }
}

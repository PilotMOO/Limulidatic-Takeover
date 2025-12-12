package mod.pilot.horseshoe_crab_takeover;

import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeCreativeTabs;
import mod.pilot.horseshoe_crab_takeover.items.HorseshoeItems;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyFileManager;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

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

        GreedyFileManager.setupGreedyTags(); //Temp for testing

        //Debugging GreedyChunk ID system
        /*Random random = new Random();
        int x, z;
        //x = -3287; z = -18820;
        for (int i = 0; i < 100; i++) {
            System.out.println();
            System.out.println();
            System.out.println("GEN[" + i + "]");
            //We don't want to generate numbers outside the bounds of normal minecraft generation
            x = random.nextInt(-300000000, 300000001);
            z = random.nextInt(-300000000, 300000001);
            long id = GreedyChunk.computeCoordinatesToID(x, z);
            System.out.println("hyp gChunk at[" + x + ", " + z + "] computed to [" + id + "], binary[" + BitwiseDataHelper.parseLongToBinary(id) + "]");
            int xIso = (int) (id >>> 40);
            int zIso = (int) (id << 24 >>> 40);
            System.out.println("x, z id iso binary: x[" + BitwiseDataHelper.parseIntToBinary(xIso) + "], z[" + BitwiseDataHelper.parseIntToBinary(zIso) + "]");
            int bit24 = 1 << 23;
            boolean negX = (bit24 & xIso) != 0;
            boolean negZ = (bit24 & zIso) != 0;
            int xDecomp, zDecomp;
            if (negX) {
                xIso = (xIso & ~bit24) * -1;
                xDecomp = (xIso * 64) - 64;
                System.out.println("iso X negative fix: " + xIso + ", binary[" + BitwiseDataHelper.parseIntToBinary(xIso) + "]");
            } else xDecomp = xIso * 64;
            if (negZ) {
                zIso = (zIso & ~bit24) * -1;
                zDecomp = (zIso * 64) - 64;
                System.out.println("iso Z negative fix: " + zIso + ", binary[" + BitwiseDataHelper.parseIntToBinary(zIso) + "]");
            } else zDecomp = zIso * 64;
            System.out.println("X, Z decompressed: [" + xDecomp + ", " + zDecomp + "]");
            int xBallpark = x - xDecomp;
            int zBallpark = z - zDecomp;
            System.out.println("Ballparks: x[" + xBallpark + "], z[" + zBallpark + "]");
            boolean xAccurate = (Math.abs(xBallpark) <= 64), //I don't know if right on 64
                    zAccurate = (Math.abs(zBallpark) <= 64); // is ok or not...
            //I think it's ok...?
            System.out.println("Accurate? x[" + xAccurate + "], z[" + zAccurate + "]");
            if (xAccurate && zAccurate) continue;
            else throw new RuntimeException("FUCK, FAILED TO COMPRESS AND DECOMPRESS [" + x + ", " + z + "]");
        }
        System.out.println("We balling");*/


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

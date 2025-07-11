package mod.pilot.horseshoe_crab_takeover;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Horseshoe_Crab_Takeover.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static class Server {
        public final ForgeConfigSpec.ConfigValue<Integer> time_until_shit_gets_real;
        public final ForgeConfigSpec.ConfigValue<Boolean> disable_invasion_start;

        public Server(ForgeConfigSpec.Builder builder) {
            time_until_shit_gets_real = builder.defineInRange("How long until the Horseshoe Crab Invasion starts, in ticks", 6000, 0, Integer.MAX_VALUE);
            disable_invasion_start = builder.define("Disable the invasion start?", false);
        }
    }

    static {
        Pair<Server, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = commonSpecPair.getLeft();
        SERVER_SPEC = commonSpecPair.getRight();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
}

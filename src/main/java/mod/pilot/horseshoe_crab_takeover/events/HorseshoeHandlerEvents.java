package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Config;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.OriginalHorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Horseshoe_Crab_Takeover.MOD_ID)
public class HorseshoeHandlerEvents {
    private static ArrayList<LivingEntity> victims = new ArrayList<>();
    private static final int shitGetsReal = Config.SERVER.time_until_shit_gets_real.get();
    private static final boolean invasionDisabled = Config.SERVER.disable_invasion_start.get();

    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (invasionDisabled) return;
        if (event.getEntity() instanceof LivingEntity LE && !(LE instanceof OriginalHorseshoeCrabEntity)){
            victims.add(LE);
        }
    }

    @SubscribeEvent
    public static void ServerStart(ServerStartedEvent event){
        HorseshoeWorldData.setActiveData(event.getServer().overworld());
        victims = new ArrayList<>();
    }

    @SubscribeEvent
    public static void InvasionStartManager(TickEvent.ServerTickEvent event){
        if (invasionDisabled) return;
        Horseshoe_Crab_Takeover.activeData.ageWorld();
        if (!HorseshoeWorldData.hasStarted() && HorseshoeWorldData.getWorldAge() > shitGetsReal){
            for (LivingEntity target : victims){
                OriginalHorseshoeCrabEntity.propagateAt(target.position().add(0, 5, 0), event.getServer().overworld());
            }

            event.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("horseshoe.system.infection_start"), false);
            Horseshoe_Crab_Takeover.activeData.setHasStarted(true);
        }
    }
}

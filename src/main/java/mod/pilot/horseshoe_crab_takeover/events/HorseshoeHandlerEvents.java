package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Config;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.HorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Horseshoe_Crab_Takeover.MOD_ID)
public class HorseshoeHandlerEvents {
    private static ArrayList<LivingEntity> victims = new ArrayList<>();
    private static final int ShitGetsReal = Config.SERVER.time_until_shit_gets_real.get();

    @SubscribeEvent
    public static void onLivingSpawned(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity LE && !(LE instanceof HorseshoeCrabEntity)){
            victims.add(LE);
        }
    }

    @SubscribeEvent
    public static void ServerStart(ServerStartedEvent event){
        HorseshoeWorldData.SetActiveData(event.getServer().overworld());
        victims = new ArrayList<>();
    }

    @SubscribeEvent
    public static void InvasionStartManager(TickEvent.ServerTickEvent event){
        Horseshoe_Crab_Takeover.activeData.ageWorld();

        if (!HorseshoeWorldData.hasStarted() && HorseshoeWorldData.getWorldAge() > ShitGetsReal){
            for (LivingEntity target : victims){
                HorseshoeCrabEntity.PropagateAt(target.position().add(0, 5, 0), event.getServer().overworld());
            }

            event.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("horseshoe.system.infection_start"), false);
            Horseshoe_Crab_Takeover.activeData.setHasStarted(true);
        }
    }
}

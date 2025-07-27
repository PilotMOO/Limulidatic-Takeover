package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Config;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.OriginalHorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.items.unique.AStarGridWand;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.Basic2DNode;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
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

    private static Basic2DNode next;
    private static int inc;
    @SubscribeEvent
    public static void serverGridTick(TickEvent.ServerTickEvent event){
        if (AStarGridWand.placeGrid){
            for (Basic2DNode[] node : AStarGridWand.grid.grid){
                for (Basic2DNode n : node){
                    event.getServer().overworld()
                            .setBlock(n.getBlockPosWithOffset(AStarGridWand.grid.bottomLeft).below(),
                                    n.blocked ? Blocks.REDSTONE_BLOCK.defaultBlockState() : Blocks.IRON_BLOCK.defaultBlockState(),
                                    3);
                }
            }
            event.getServer().overworld()
                    .setBlock(AStarGridWand.grid.grid[0][0].getBlockPosWithOffset(AStarGridWand.grid.bottomLeft).below(),
                            Blocks.EMERALD_BLOCK.defaultBlockState(), 3);
            AStarGridWand.placeGrid = false;
        }
        if (AStarGridWand.pathfind){
            next = AStarGridWand.grid.findPath(AStarGridWand.start, AStarGridWand.end);
            AStarGridWand.pathfind = false;
            inc = 0;
        }
        if (next != null){
            if (++inc % 15 == 0) {
                event.getServer().overworld().setBlock(next.getBlockPosWithOffset(AStarGridWand.grid.bottomLeft),
                        Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                next = next.parent != next ? next.parent : null;
            }
        }
        else inc = 0;
    }
}

package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Config;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.entities.OriginalHorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.items.unique.AStarGridWand;
import mod.pilot.horseshoe_crab_takeover.items.unique.BitPackageTestWand;
import mod.pilot.horseshoe_crab_takeover.items.unique.Node3DGridWand;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Basic2DNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Node3D;
import mod.pilot.horseshoe_crab_takeover.worlddata.HorseshoeWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3i;

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

    private static ArrayList<Basic2DNode.Snapshot> path;
    private static Basic2DNode.Snapshot current;
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
            Basic2DNode node = AStarGridWand.grid.findPath(AStarGridWand.start, AStarGridWand.end);
            AStarGridWand.pathfind = false;
            inc = 0;
            if (node != null){
                path = Basic2DNode.Snapshot.snapshotPathToArray(node, true);
                current = path.get(0);
            }
        }
        if (path != null){
            if (++inc % 15 == 0) {
                Vector3i pos = current.getPosWithOffset(AStarGridWand.grid.bottomLeft);
                event.getServer().overworld().setBlock(new BlockPos(pos.x, pos.y, pos.z),
                        Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                int index = path.indexOf(current);
                if (++index >= path.size()){
                    path = null; current = null;
                } else current = path.get(index);
            }
        }
        else inc = 0;
    }

    private static ArrayList<Node3D.Snapshot> path3d;
    private static Node3D.Snapshot current3d;
    private static int inc3d;
    @SubscribeEvent
    public static void serverGridTick3d(TickEvent.ServerTickEvent event){
        ServerLevel server = event.getServer().overworld();
        if (Node3DGridWand.renderGrid){
            for (Node3D[][] node : Node3DGridWand.grid.grid){
                for (Node3D[] node1 : node){
                    for (Node3D n : node1){
                        if (n.blocked()) continue;
                        BlockPos bPos = n.getBlockPosWithOffset(Node3DGridWand.grid.lowerBottomLeft);
                        //server.setBlock(bPos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                        SimpleParticleType particle = n.traversable() ? ParticleTypes.BUBBLE_POP : ParticleTypes.BUBBLE;
                        server.sendParticles(particle, bPos.getX() + .5, bPos.getY() + .5, bPos.getZ() + .5,
                                1, 0, 0, 0, 0);
                    }
                }
            }
        }
        if (Node3DGridWand.pathfind){
            Node3D node = Node3DGridWand.grid.findPath(Node3DGridWand.start, Node3DGridWand.end);
            Node3DGridWand.pathfind = false;
            inc3d = 0;
            if (node != null){
                path3d = Node3D.Snapshot.snapshotPathToArray(node, true);
                current3d = path3d.get(0);
            }
        }
        if (path3d != null){
            if (++inc3d % 15 == 0) {
                Vector3i pos = current3d.getWithOffset(Node3DGridWand.grid.lowerBottomLeft);
                event.getServer().overworld().setBlock(new BlockPos(pos.x, pos.y, pos.z),
                        Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
                int index = path3d.indexOf(current3d);
                if (++index >= path3d.size()){
                    path3d = null; current3d = null;
                } else current3d = path3d.get(index);
            }
        }
        else inc3d = 0;
    }

    public static BlockPos.MutableBlockPos mBPosREAD;
    public static Vector3i lowerLeft;
    public static boolean read, write;
    @SubscribeEvent
    public static void bitPackageTicker(TickEvent.ServerTickEvent event){
        Level level = event.getServer().overworld();

        if (read){
            boolean flip = false;

            mBPosREAD.move(-5, -5, -5);
            lowerLeft = DataHelper.ForVector3i.from(mBPosREAD.getCenter());
            int xC = mBPosREAD.getX(), yC = mBPosREAD.getY(), zC = mBPosREAD.getZ();
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    for (int z = 0; z < 10; z++) {
                        mBPosREAD.set(xC + x, yC + y, zC + z);

                        //BitPackageTestWand.bitPackage.writeObject(x, y, z, (flip = !flip) ? Blocks.AIR.defaultBlockState() : Blocks.IRON_BLOCK.defaultBlockState());
                        BitPackageTestWand.bitPackage.writeObject(x, y, z, level.getBlockState(mBPosREAD));
                        level.setBlock(mBPosREAD, BitPackageTestWand.AIR, 3);
                    }
                }
            }
            System.out.println("BIT PACKAGE: ");
            System.out.println(BitPackageTestWand.bitPackage);
            read = false;
        }
        if (write){
            BlockPos.MutableBlockPos mBPosWRITE = new BlockPos.MutableBlockPos(lowerLeft.x, lowerLeft.y, lowerLeft.z);
            int xC = mBPosWRITE.getX(), yC = mBPosWRITE.getY(), zC = mBPosWRITE.getZ();
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    for (int z = 0; z < 10; z++) {
                        mBPosWRITE.set(xC + x, yC + y, zC + z);
                        level.setBlock(mBPosWRITE, BitPackageTestWand.bitPackage.readObject(x, y, z), 3);
                    }
                }
            }
            write = false;
        }
    }
}

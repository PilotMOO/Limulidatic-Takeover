package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector2i;

public class GreedyChunkReaderWand extends Item {
    public GreedyChunkReaderWand(Properties pProperties) {
        super(pProperties);
        MinecraftForge.EVENT_BUS.addListener(GreedyChunkReaderWand::serverTick);
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent tick){
        if (level != null) {
            int min = level.dimensionType().minY(), max = level.dimensionType().height();
            for (int i = min; i < max; i++){
                BlockPos.MutableBlockPos bMinor =
                        new BlockPos.MutableBlockPos(minorX, i, minorZ);
                level.setBlock(bMinor, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
                bMinor.move(63, 0, 63);
                level.setBlock(bMinor, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
            }
            level = null;
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, pPlayer, pUsedHand);

        long id = GreedyChunk.computeCoordinatesToID(pPlayer.position());
        pPlayer.displayClientMessage(Component.literal("GreedyChunk ID: " + id + ", binary[" + BitwiseDataHelper.parseLongToBinary(id) + "]"), false);
        GreedyChunk gChunk = GreedyWorld.WORLD_DEFAULT().retrieveOrCreateGreedyChunk(id);
        Vector2i relative = gChunk.relative;
        pPlayer.displayClientMessage(Component.literal("Relative [" + relative.x + ", " + relative.y + "]"), false);
        minorX = relative.x; minorZ = relative.y;
        level = pLevel;

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static int minorX, minorZ;
    private static Level level;
}

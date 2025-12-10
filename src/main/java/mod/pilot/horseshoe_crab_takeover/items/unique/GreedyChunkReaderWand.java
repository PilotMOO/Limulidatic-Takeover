package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreedyChunkReaderWand extends Item {
    public GreedyChunkReaderWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, pPlayer, pUsedHand);

        long id = GreedyChunk.computeCoordinatesToID(pPlayer.position());
        pPlayer.displayClientMessage(Component.literal("GreedyChunk ID: " + id + ", binary[" + BitwiseDataHelper.parseLongToBinary(id) + "]"), false);
        GreedyChunk gChunk = GreedyWorld.WORLD_DEFAULT().retrieveOrCreateGreedyChunk(id);
        pPlayer.displayClientMessage(Component.literal("Relative " + gChunk.relative), false);

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}

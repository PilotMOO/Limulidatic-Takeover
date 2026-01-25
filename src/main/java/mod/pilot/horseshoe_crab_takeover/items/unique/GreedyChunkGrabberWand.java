package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreedyChunkGrabberWand extends Item {
    public GreedyChunkGrabberWand(Properties pProperties) {
        super(pProperties);
    }

    private static final GreedyWorld gWorld = GreedyWorld.WORLD_DEFAULT();
    private static GreedyChunk gChunk;
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player,
                                                  InteractionHand hand) {
        if (level.isClientSide) return super.use(level, player, hand);

        GreedyChunk greedyChunk = gWorld.retrieveFromWorldCoordinates(player.position());
        System.out.println("[GreedyChunkGrabberWand] RETRIEVED chunk " + greedyChunk);
        player.displayClientMessage(Component.literal("GreedyChunk with ID[" + greedyChunk.chunkID + "]"),false);
        System.out.println("Prior chunk: " + gChunk);
        if (gChunk != null) {
            player.displayClientMessage(Component.literal("prior equal? " + gChunk.equals(greedyChunk)), false);
            System.out.println("prior equal? " + gChunk.equals(greedyChunk));
        }
        gChunk = greedyChunk;
        return super.use(level, player, hand);
    }
}

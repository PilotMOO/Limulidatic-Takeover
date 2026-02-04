package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNodeEvaluator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;

public class GreedyChunkRelativeWand extends Item {
    public GreedyChunkRelativeWand(Properties pProperties) {
        super(pProperties);
        evaluator = new GreedyNodeEvaluator() {
            @Override
            protected boolean evaluatePosition(int contextX, int contextY, int contextZ) {
                return false;
            }

            @Override
            public boolean evaluateSoloInstance(Level level, LevelChunk chunk, BlockPos.MutableBlockPos bPos, BlockState bState) {
                return false;
            }

            @Override
            public boolean checkNegativeY() {
                return false;
            }

            @Override
            public boolean checkPositiveY() {
                return false;
            }
        };
    }

    public static GreedyChunk gChunk;
    public final GreedyNodeEvaluator evaluator;

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player,
                                                  InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, player, pUsedHand);

        Vec3 pos = player.position();
        gChunk = GreedyWorld.WORLD_DEFAULT()
                .retrieveFromWorldCoordinates(pos.x, pos.z);
        evaluator.setupGChunkEvaluation(pLevel, gChunk);
        byte relX = (byte) (GreedyNodeEvaluator.toGreedyChunkContext((int)Math.floor(pos.x)) / 16),
                relZ = (byte) (GreedyNodeEvaluator.toGreedyChunkContext((int)Math.floor(pos.z)) / 16);
        player.displayClientMessage(Component.literal(
                    "You are in a chunk context of [" + relX + ", " + relZ + "]"
        ), false);

        boolean flag = false;
        for (LevelChunkSection b : evaluator.getChunk(relX, relZ).getSections()){
            for (int xb = 0; xb < 16; xb++){
                for (int yb = 0; yb < 16; yb++){
                    for (int zb = 0; zb < 16; zb++){
                        BlockState bState = b.getBlockState(xb, yb, zb);
                        if (bState.is(Blocks.DIAMOND_BLOCK)){
                            flag = true;
                            break;
                        }
                    }
                    if (flag) break;
                }
                if (flag) break;
            }
            if (flag) break;
        }
        if (flag){
            player.displayClientMessage(
                    Component.literal("DIAMOND BLOCK FUCK FUCK FUCK FUCK"),
                    false);
        }


        return super.use(pLevel, player, pUsedHand);
    }
}

package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNodeEvaluator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.joml.Vector3i;

public class GStarNodeGeneratorWand extends Item {
    public GStarNodeGeneratorWand(Properties pProperties) {
        super(pProperties);
        evaluator = new GreedyNodeEvaluator() {
            @Override
            protected boolean evaluatePosition(int contextX, int contextY, int contextZ) {
/*                System.out.println("Evaluating at coords ["
                        + contextX + ", " + contextY + ", " + contextZ +"]");*/
                BlockState bState = curSection.getBlockState(contextX, contextY, contextZ);
                if (!bState.isAir()){
                    System.out.println("State WASN'T AIR: " + bState);
                    return false;
                }
                BlockState bLow;
                if (contextY > 1) bLow = curSection.getBlockState(contextX, contextY - 1, contextZ);
                else{
                    int index;
                    LevelChunkSection[] sections = curChunk.getSections();
                    for (index = 0; index < sections.length; index++){
                        if (sections[index].equals(curSection)){
                            index--;
                            break;
                        }
                    }
                    bLow = sections[index].getBlockState(contextX, 15, contextZ);
                }
                if (!bLow.isAir()) return true;
                else{
                    System.out.println("BELOW WAS AIR :[");
                    return false;
                }
            }

            @Override
            public boolean evaluateSoloInstance(Level level, LevelChunk chunk, BlockPos.MutableBlockPos bPos, BlockState bState) {
                BlockState bLow = chunk.getBlockState(bPos.below());
                return bState.isAir() && !bLow.isAir();
            }

            @Override public boolean evaluateEvenIfOnlyAir() {return false;}
            @Override public boolean checkNegativeY() {return false;}
            @Override public boolean checkPositiveY() {return false;}
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        player.getCooldowns().addCooldown(this, 10);

        Level level;
        if ((level = context.getLevel()).isClientSide()) return super.useOn(context);

        BlockPos bPos = context.getClickedPos().relative(context.getClickedFace());
        GreedyChunk gChunk = GreedyWorld.WORLD_DEFAULT()
                .retrieveFromWorldCoordinates(bPos.getX(), bPos.getZ());
        evaluator.setupGChunkEvaluation(level, gChunk);
        gNode = evaluator.buildNode(bPos.getX(), bPos.getY(), bPos.getZ(),
                false, false);
        GStarNodeGeneratorWand.gChunk = evaluator.greedyChunk;
        player.displayClientMessage(Component.literal("node at " + bPos), false);
        System.out.println("Created node " +  gNode);
        evaluator.logger.printAll();
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.getCooldowns().addCooldown(this, 10);
        if (level.isClientSide) return super.use(level, player, hand);

        if (gNode == null){
            player.displayClientMessage(Component.literal("Can't render/fill GreedyNode, it doesn't exist yet!"), false);
        } else {
            player.displayClientMessage(Component.literal("Filling..."), true);
            for (BlockPos bPos : gNode.getBlockPosIterator(true)){
                level.setBlock(bPos.offset(gChunk.relative.x, 0, gChunk.relative.y),
                        Blocks.GLASS.defaultBlockState(), 3);
            }
        }
        return super.use(level, player, hand);
    }

    private static GreedyNode gNode;
    private static GreedyChunk gChunk;

    private static GreedyNodeEvaluator evaluator;
}

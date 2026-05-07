package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

public class QuadSpaceFillWand extends Item {
    public QuadSpaceFillWand(Properties pProperties) {
        super(pProperties);
        MinecraftForge.EVENT_BUS.addListener(QuadSpaceFillWand::tick);
    }

    public static void tick(TickEvent.ServerTickEvent tick){
        if (_level != null){
            System.out.println("Filling QuadSpace of " + qSpace);
            for (BlockPos bPos : qSpace.getBlockPosIterator(true)){
                System.out.println("Block at " + bPos);
                _level.setBlock(bPos, Blocks.BAMBOO_BLOCK.defaultBlockState(), 3);
            }
            System.out.println("DONE!");
            _level = null;
            qSpace = null;
        }
    }

    private static QuadSpace qSpace;
    private static Level _level;

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level;
        if((level = pContext.getLevel()).isClientSide) return InteractionResult.FAIL;

        BlockPos bPos = pContext.getClickedPos().relative(pContext.getClickedFace());
        qSpace = new QuadSpace(bPos.getX(), bPos.getY(), bPos.getZ(), 5, 2, 3);
        _level = level;

        return super.useOn(pContext);
    }
}

package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.data.DataHelper;
import mod.pilot.horseshoe_crab_takeover.events.HorseshoeHandlerEvents;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.DynamicBitPackage3d;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.joml.Vector3i;

public class BitPackageSlabTestWand extends Item {
    public BitPackageSlabTestWand(Properties pProperties) {
        super(pProperties);
        bitPackage = new DynamicBitPackage3d<>(3, 10, 10, 10,
                BitPackageSlabTestWand::toBits, BitPackageSlabTestWand::fromBits);
        AIR = Blocks.AIR.defaultBlockState();
        BLOCK = Blocks.DIAMOND_BLOCK.defaultBlockState();
        SLAB = Blocks.BIRCH_SLAB.defaultBlockState();
    }

    public static DynamicBitPackage3d<BlockState> bitPackage;
    public static Vector3i lowerLeft;
    public static BlockState AIR, BLOCK, SLAB;
    public static BlockState slab(SlabType type){
        return SLAB.setValue(SlabBlock.TYPE, type);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            context.getPlayer().displayClientMessage(Component.literal("Encoding all values to bit package..."), true);
            HorseshoeHandlerEvents.mBPosREAD = context.getClickedPos().mutable();
            HorseshoeHandlerEvents.read = true;
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.isSecondaryUseActive()) {
            player.displayClientMessage(Component.literal("Decoding all values to world..."), true);
            HorseshoeHandlerEvents.write = true;
        }
        return super.use(level, player, hand);
    }

    private static long[] toBits(BlockState obj, int bitOffset, long[] mail){
        //Since we are only using 1 bit per obj we don't need to worry about writing across multiple "words". Just modify the first one
        mail[0] = DataHelper.writeBit(mail[0], bitOffset, obj.isAir());
        return mail;
    }
    private static BlockState fromBits(int bitOffset, long[] mail){
        return DataHelper.readBitAt(mail[0], bitOffset) ? AIR : BLOCK;
    }
}

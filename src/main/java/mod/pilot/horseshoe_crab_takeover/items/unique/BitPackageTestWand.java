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

public class
BitPackageTestWand extends Item {
    public BitPackageTestWand(Properties pProperties) {
        super(pProperties);
        bitPackage = new DynamicBitPackage3d<>(3, 10, 10, 10,
                BitPackageTestWand::toBits, BitPackageTestWand::fromBits);
        AIR = Blocks.AIR.defaultBlockState();
        BLOCK = Blocks.SANDSTONE.defaultBlockState();
        SLAB = Blocks.SANDSTONE_SLAB.defaultBlockState();
    }

    public static DynamicBitPackage3d<BlockState> bitPackage;
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

    private static long[] toBits(BlockState obj, int bitOffset, long[] mail) {
        long ink = 0L;
        if (!obj.isAir()) {
            try {
                SlabType sType = obj.getValue(SlabBlock.TYPE);
                ink |= switch (sType) {
                    case TOP -> 1L;
                    case BOTTOM -> 2L;
                    case DOUBLE -> 3L;
                };
            } catch (Exception ignored) {}
            ink = (ink << 1) | 1L;
        }

        try {
            DataHelper.writeRangeToSentence(mail, bitOffset, ink, 3);
        } catch (DataHelper.InvalidBitWriteOperation e) {
            throw new RuntimeException(e);
        }

        return mail;
    }
    private static BlockState fromBits(int bitOffset, long[] mail){
        long data = DataHelper.isolateAndMergeAcrossWords(mail[0], mail[1], bitOffset, 3);
        if (data == 0) return AIR;
        else if (data > 1){
            data >>>= 1;
            return slab(switch ((int) data){
                case 3 -> SlabType.DOUBLE;
                case 2 -> SlabType.BOTTOM;
                default -> SlabType.TOP;
            });
        }
        else return BLOCK;
    }
}

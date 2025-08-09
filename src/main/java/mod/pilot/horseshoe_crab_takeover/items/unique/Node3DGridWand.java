package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Node3DGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.joml.Vector3i;

public class Node3DGridWand extends Item {
    public Node3DGridWand(Properties pProperties) {
        super(pProperties);
    }

    public static Node3DGrid grid;
    public static boolean renderGrid;
    public static Vector3i start, end;
    public static boolean pathfind;

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel server){
            BlockPos bPos = pContext.getClickedPos().relative(pContext.getClickedFace());
            if (pContext.isSecondaryUseActive()) {
                pContext.getPlayer().displayClientMessage(Component.literal("Creating new grid!"), true);
                Vector3i pos = new Vector3i(bPos.getX() - 10, bPos.getY(), bPos.getZ() - 10);
                grid = new Node3DGrid(pos, false, 20, 20, 20, Node3DGridWand::nodeState);
                grid.fillGrid(server);
                renderGrid = true;
            } else if (grid != null){
                if (start == null){
                    start = new Vector3i(bPos.getX(), bPos.getY(), bPos.getZ());
                    pContext.getPlayer().displayClientMessage(Component.literal("setting start to " + start), true);
                }
                else{
                    end = new Vector3i(bPos.getX(), bPos.getY(), bPos.getZ());
                    pContext.getPlayer().displayClientMessage(Component.literal("setting end to " + end), true);
                }

                pathfind = start != null && end != null;
            }
        }
        pContext.getPlayer().getCooldowns().addCooldown(this, 5);
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel instanceof ServerLevel){
            if (pPlayer.isSecondaryUseActive()){
                if (start != null || end != null) {
                    pPlayer.displayClientMessage(Component.literal("Clearing start and finish!"), true);
                } else {
                    pPlayer.displayClientMessage(Component.literal("Clearing EVERYTHING!"), true);
                    grid = null;
                    renderGrid = false;
                }
                start = end = null;
                pathfind = false;
            }
            else if (grid != null){
                pPlayer.displayClientMessage(Component.literal("Regenerating node values..."), true);
                grid.fillGrid(pLevel);
                renderGrid = true;
            }
        }
        pPlayer.getCooldowns().addCooldown(this, 5);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static byte nodeState(BlockPos bPos, Level level){
        if (!level.getBlockState(bPos).isAir()) return 2;

        if (!level.getBlockState(bPos.below()).isAir()) return 0;
        if (checkAround(bPos, level)) return 1;

        return 2;
    }

    private static boolean checkAround(BlockPos bPos, Level level){
        BlockPos.MutableBlockPos mBPos = new BlockPos.MutableBlockPos();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++){
                    if (x != 0 && y != 0 && z != 0) continue;
                    else if (x == y && y == z) continue;
                    mBPos.set(bPos.getX() + x, bPos.getY() + y, bPos.getZ() + z);
                    if (checkBPos(mBPos, level)) return true;
                }
            }
        }
        return false;
    }
    private static boolean checkBPos(BlockPos bPos, Level level){
        return level.getBlockState(bPos).isAir() && !level.getBlockState(bPos.below()).isAir();
    }
}

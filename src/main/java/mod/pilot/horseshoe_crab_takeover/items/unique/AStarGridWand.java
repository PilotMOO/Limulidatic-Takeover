package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.Basic2DNodeGrid;
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

public class AStarGridWand extends Item {
    public AStarGridWand(Properties pProperties) {
        super(pProperties);
    }

    public static Basic2DNodeGrid grid;
    public static boolean placeGrid;
    public static Vector3i start, end;
    public static boolean pathfind;

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel() instanceof ServerLevel server){
            BlockPos bPos = pContext.getClickedPos().relative(pContext.getClickedFace());
            if (pContext.isSecondaryUseActive()) {
                pContext.getPlayer().displayClientMessage(Component.literal("Creating new grid!"), true);
                Vector3i pos = new Vector3i(bPos.getX(), bPos.getY(), bPos.getZ());
                grid = new Basic2DNodeGrid(pos, 10, 10, AStarGridWand::isNotWalkable, true);
                grid.fillGrid(server);
                placeGrid = true;
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
                    placeGrid = false;
                }
                start = end = null;
                pathfind = false;
            }
            else if (grid != null){
                pPlayer.displayClientMessage(Component.literal("Regenerating node values..."), true);
                grid.fillGrid(pLevel);
                placeGrid = true;
            }
        }
        pPlayer.getCooldowns().addCooldown(this, 5);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public static boolean isNotWalkable(BlockPos bPos, Level level){
        boolean flag = !level.getBlockState(bPos).isAir();
        return flag;
    }
}

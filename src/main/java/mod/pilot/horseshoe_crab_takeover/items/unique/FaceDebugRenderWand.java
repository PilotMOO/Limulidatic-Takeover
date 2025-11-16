package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger.RenderDebuggerQue;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger.SoloBlockRenderPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class FaceDebugRenderWand extends Item {
    public FaceDebugRenderWand(Properties pProperties) {
        super(pProperties);
    }

    public static ArrayList<SoloBlockRenderPacket> renderPackets = new ArrayList<>();

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide()) {
            BlockPos bPos = pContext.getClickedPos();
            Direction face = pContext.getClickedFace();
            SoloBlockRenderPacket packet = new SoloBlockRenderPacket(bPos,
                    face, 0f, 1f, 0f);
            packet.que();
            renderPackets.add(packet);
            pContext.getPlayer().displayClientMessage(Component.literal("Creating render face at " + bPos + " facing " + face), false);
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide() && pPlayer.isSecondaryUseActive()){
            renderPackets.forEach(RenderDebuggerQue::removeRender);
            renderPackets.clear();
            pPlayer.displayClientMessage(Component.literal("Clearing render packets!"), false);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}

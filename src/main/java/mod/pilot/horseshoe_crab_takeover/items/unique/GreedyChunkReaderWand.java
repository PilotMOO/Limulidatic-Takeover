package mod.pilot.horseshoe_crab_takeover.items.unique;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.BitwiseDataHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

public class GreedyChunkReaderWand extends Item {
    public GreedyChunkReaderWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (minor != null && major != null){
            pLevel.addParticle(ParticleTypes.CRIT, minor.x, minor.y, minor.z,
                    0,.4, 0);
            pLevel.addParticle(ParticleTypes.CRIT, major.x, major.y, major.z,
                    0,-.4, 0);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, pPlayer, pUsedHand);

        long id = GreedyChunk.computeCoordinatesToID(pPlayer.position());
        pPlayer.displayClientMessage(Component.literal("GreedyChunk ID: " + id + ", binary[" + BitwiseDataHelper.parseLongToBinary(id) + "]"), false);
        GreedyChunk gChunk = GreedyWorld.WORLD_DEFAULT().retrieveOrCreateGreedyChunk(id);
        Vector2i relative = gChunk.relative;
        pPlayer.displayClientMessage(Component.literal("Relative [" + relative.x + ", " + relative.y + "]"), false);
        minor = new Vec3(relative.x, pPlayer.getY(), relative.y);
        major = new Vec3(relative.x + 63.5, pPlayer.getY() + 1, relative.y + 63.5);

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static Vec3 minor, major;
}

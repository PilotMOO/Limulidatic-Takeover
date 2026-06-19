package mod.pilot.horseshoe_crab_takeover.entities;

import mod.pilot.horseshoe_crab_takeover.data.syncing.NodeSyncingPacket;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class NodeVisualizerEntity extends Entity {
    public NodeVisualizerEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static class NodeSyncingBay{
        private static final HashMap<UUID, GreedyNode[]> nodesByOwner = new HashMap<>();

        public static void update(NodeSyncingPacket.ServerSyncPacket packet){update(packet.entityUUID(), packet.gnodes());}
        public static void update(UUID uuid, GreedyNode[] nodes){nodesByOwner.put(uuid, nodes);}

        public static GreedyNode[] get(UUID uuid){
            return nodesByOwner.getOrDefault(uuid, EMPTY).clone();
        }

        private static final GreedyNode[] EMPTY = new GreedyNode[0];
    }

    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {}
    @Override protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {}

    public static AttributeSupplier.Builder createAttributes(){
        return AttributeSupplier.builder().add(Attributes.MAX_HEALTH, 1D);
    }
}

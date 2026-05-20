package mod.pilot.horseshoe_crab_takeover.entities;

import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class NodeVisualizerEntity extends Entity {
    public NodeVisualizerEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ArrayList<GreedyNode> NODES_TO_RENDER;
    public GreedyNode highlighted;
    public void highlightNode(GreedyNode gNode) {
        if (NODES_TO_RENDER == null || NODES_TO_RENDER.isEmpty()) {
            System.err.println("WARNING! There is no currently assigned gnodes to this entity, so there isn't anything to render!");
            return;
        }
        highlighted = gNode;
        if (!NODES_TO_RENDER.contains(gNode)) System.err.println("WARNING! The highlighted node is not included in the list of rendering nodes, so it wont do anything.");
    }

    public void setNodes(@Nullable ArrayList<GreedyNode> nodes) {
        NODES_TO_RENDER = level().isClientSide ? nodes : null;
    }

    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override protected void addAdditionalSaveData(CompoundTag pCompound) {}

    public static AttributeSupplier.Builder createAttributes(){
        return AttributeSupplier.builder().add(Attributes.MAX_HEALTH, 1D);
    }
}

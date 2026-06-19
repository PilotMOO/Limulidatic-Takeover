package mod.pilot.horseshoe_crab_takeover.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.NodeVisualizerEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyChunk;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.GreedyWorld;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar.nodes.GreedyNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.QuadSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class NodeVisualizerRenderer extends EntityRenderer<NodeVisualizerEntity> {
    public NodeVisualizerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(NodeVisualizerEntity entity, float yaw, float partial,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        GreedyNode[] gNodes;
        if ((gNodes = NodeVisualizerEntity.NodeSyncingBay.get(entity.getUUID())).length == 0) return;
        Level level = entity.level();
        GreedyChunk gChunk = GreedyWorld.greedyWorld_DEFAULT.retrieveFromWorldCoordinates(entity.getX(), entity.getZ());
        Vector3d relative = new Vector3d(gChunk.relative.x, 0, gChunk.relative.y);
        for (GreedyNode g : gNodes) {
            ParticleOptions particle = ParticleTypes.END_ROD;
            renderQuadSpaceBorder(level, relative, g, particle);
        }
    }


    private static void renderQuadSpaceBorder(Level level, Vector3d worldRelative, QuadSpace quad, ParticleOptions particleOptions){
        Vector3d bottomLeftSouth = new Vector3d(quad.minorX, quad.minorY, quad.minorZ).add(worldRelative),
                bottomLeftNorth = new Vector3d(bottomLeftSouth).add(quad.sizeX, 0, 0),
                bottomRightNorth = new Vector3d(bottomLeftSouth).add(quad.sizeX, 0, quad.sizeZ),
                bottomRightSouth = new Vector3d(bottomLeftSouth).add(0, 0, quad.sizeZ);
        Vector3d topLeftSouth = new Vector3d(bottomLeftSouth).add(0, quad.sizeY, 0),
                topLeftNorth = new Vector3d(bottomLeftSouth).add(quad.sizeX, quad.sizeY, 0),
                topRightNorth = new Vector3d(bottomLeftSouth).add(quad.sizeX, quad.sizeY, quad.sizeZ),
                topRightSouth = new Vector3d(bottomLeftSouth).add(0, quad.sizeY, quad.sizeZ);

        //Render bottom border
        renderParticlesBetween(level, bottomLeftSouth, bottomLeftNorth, particleOptions);
        renderParticlesBetween(level, bottomLeftNorth, bottomRightNorth, particleOptions);
        renderParticlesBetween(level, bottomRightNorth, bottomRightSouth, particleOptions);
        renderParticlesBetween(level, bottomRightSouth, bottomLeftSouth, particleOptions);
        //Render top border
        renderParticlesBetween(level, topLeftSouth, topLeftNorth, particleOptions);
        renderParticlesBetween(level, topLeftNorth, topRightNorth, particleOptions);
        renderParticlesBetween(level, topRightNorth, topRightSouth, particleOptions);
        renderParticlesBetween(level, topRightSouth, topLeftSouth, particleOptions);
        //Render connections
        renderParticlesBetween(level, bottomLeftSouth, topLeftSouth, particleOptions);
        renderParticlesBetween(level, bottomLeftNorth, topLeftNorth, particleOptions);
        renderParticlesBetween(level, bottomRightNorth, topRightNorth, particleOptions);
        renderParticlesBetween(level, bottomRightSouth, topRightSouth, particleOptions);
    }
    private static void renderParticlesBetween(Level level, final Vector3d start, final Vector3d end, ParticleOptions particleOptions) {
        double dist = start.distance(end), dist2 = dist * 2;
        Vector3d step = new Vector3d(end).sub(start).div(dist2);
        Vector3d pos = new Vector3d(start);
        for (int i = 1; i < dist2; i++){
            pos.add(step);
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }


    @Override
    public boolean shouldRender(NodeVisualizerEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull NodeVisualizerEntity pEntity) {
        return new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "textures/entity/fuck_you.png");
    }
}

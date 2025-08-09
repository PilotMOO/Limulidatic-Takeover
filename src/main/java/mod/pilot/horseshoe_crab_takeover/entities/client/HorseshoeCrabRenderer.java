package mod.pilot.horseshoe_crab_takeover.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.ModifiedHorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Basic2DNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data.Basic2DNodeGrid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class HorseshoeCrabRenderer extends LivingEntityRenderer<ModifiedHorseshoeCrabEntity, HorseshoeCrabModel<ModifiedHorseshoeCrabEntity>> {
    public HorseshoeCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseshoeCrabModel<>(context.bakeLayer(HorseshoeCrabModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ModifiedHorseshoeCrabEntity pEntity) {
        return new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "textures/entity/horseshoe_crab_texture.png");
    }

    @Override
    public void render(@NotNull ModifiedHorseshoeCrabEntity crab, float yaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float size = crab.getSize();
        poseStack.scale(size, size, size);
        super.render(crab, yaw, partialTicks, poseStack, buffer, packedLight);

        Basic2DNodeGrid grid = crab.getNavigation().grid;
        if (grid != null && grid.grid != null) {
            for (Basic2DNode[] nodes : grid.grid) {
                for (Basic2DNode node : nodes) {
                    BlockPos bPos = node.getBlockPosWithOffset(grid.bottomLeft);
                    SimpleParticleType particle = node.blocked ? ParticleTypes.SMOKE : ParticleTypes.BUBBLE;
                    crab.level().addParticle(particle, bPos.getX() + .5, bPos.getY() + .5, bPos.getZ() + .5, 0, 0, 0);
                }
            }
            ArrayList<Basic2DNode.Snapshot> snap = crab.getNavigation().pathSnapshot;
            if (snap != null){
                for (Basic2DNode.Snapshot snap1 : snap){
                    Vector3i vi = snap1.getPosWithOffset(grid.bottomLeft);
                    crab.level().addParticle(ParticleTypes.CRIT, vi.x + .5, vi.y + .75, vi.z + .5, 0, 0, 0);
                }
            }
        }
    }

    private void drawTaperedCube(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                                 float bottomWidth, float bottomDepth,
                                 float topWidth, float topDepth, float height,
                                 int overlay, int lightmap, float red, float green, float blue, float alpha,
                                 float U1, float U2,
                                 float V1, float V2) {
        // Define bottom and top face dimensions
        float x1b = -bottomWidth / 2, x2b = bottomWidth / 2;  // Bottom face width
        float x1t = -topWidth / 2, x2t = topWidth / 2;        // Top face width
        float z1b = -bottomDepth / 2, z2b = bottomDepth / 2;  // Bottom face depth
        float z1t = -topDepth / 2, z2t = topDepth / 2;        // Top face depth
        float y1 = 0f, y2 = height;                // Height

        // Bottom Face (larger or smaller base)
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z2b, x2b, y1, z2b, x2b, y1, z1b, x1b, y1, z1b, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, -1, 0);
        // Top Face (larger or smaller top)
        drawFace(vertexConsumer, matrix, normal, x1t, y2, z2t, x2t, y2, z2t, x2t, y2, z1t, x1t, y2, z1t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Front Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z2b, x2b, y1, z2b, x2t, y2, z2t, x1t, y2, z2t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Back Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z1b, x1b, y1, z1b, x1t, y2, z1t, x2t, y2, z1t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Left Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z1b, x1b, y1, z2b, x1t, y2, z2t, x1t, y2, z1t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);
        // Right Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z2b, x2b, y1, z1b, x2t, y2, z1t, x2t, y2, z2t, overlay, lightmap,
                red, green, blue, alpha,
                U1, U2, V1, V2,
                0, 1, 0);

    }

    private void drawFace(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          float x3, float y3, float z3, float x4, float y4, float z4,
                          int overlay, int lightmap,
                          float red, float green, float blue, float alpha,
                          float U1, float U2, float V1, float V2,
                          float normalX, float normalY, float normalZ) {

        // First vertex (bottom left)
        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(red, green, blue, alpha)
                .uv(U1, V2)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Second vertex (bottom right)
        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(red, green, blue, alpha)
                .uv(U2, V2)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Third vertex (top right)
        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(red, green, blue, alpha)
                .uv(U2, V1)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();

        // Fourth vertex (top left)
        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(red, green, blue, alpha)
                .uv(U1, V1)
                .overlayCoords(overlay)
                .uv2(lightmap)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();
    }
}

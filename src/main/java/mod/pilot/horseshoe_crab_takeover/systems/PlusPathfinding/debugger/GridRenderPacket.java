package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.horseshoe_crab_takeover.systems.BetterEntities.WorldEntity;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.AStarTesting.FlatAStarNavigation;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.Basic2DNode;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.Basic2DNodeGrid;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.PlusMovementControl;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class GridRenderPacket implements RenderDebuggerQue.IRenderInstructions {
    public GridRenderPacket(FlatAStarNavigation<? extends WorldEntity, ? extends PlusMovementControl> NAV){ this.nav = NAV; }
    FlatAStarNavigation<? extends WorldEntity, ? extends PlusMovementControl> nav;

    @Override
    public void render(MultiBufferSource.BufferSource buffer, PoseStack poseStack, float partial, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightTexture lightTexture, Matrix4f projection) {
        Basic2DNodeGrid grid = nav.grid;
        if (grid != null && grid.grid != null) {
            for (Basic2DNode[] nodes : grid.grid) {
                for (Basic2DNode node : nodes) {
                    poseStack.pushPose();
                    BlockPos bPos = node.getBlockPosWithOffset(grid.bottomLeft);
                    Vec3 relative = bPos.getCenter().subtract(camera.getPosition());
                    poseStack.translate(relative.x, relative.y, relative.z);
                    poseStack.rotateAround(new Quaternionf(), 0, 0, 0);
                    PoseStack.Pose pose = poseStack.last();

                    float r = 0f, g = 1f, b = 0f;
                    if (node.blocked){ r = 1f; g = 0f; }
                    drawTaperedCube(buffer.getBuffer(RenderType.lightning()),
                            pose.pose(), pose.normal(),
                            0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                            OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT,
                            r, g, b, .25f,
                            0, 1, 0, 1);
                    poseStack.popPose();
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

package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.horseshoe_crab_takeover.data.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SoloBlockRenderPacket implements RenderDebuggerQue.IRenderInstructions {
    public BlockPos bPos;
    public float red, green, blue;
    public Direction face;
    public SoloBlockRenderPacket(BlockPos bPos, Direction face, float red, float green, float blue){
        this.bPos = bPos;
        this.face = face;
        this.red = red; this.green = green; this.blue = blue;
    }

    @Override
    public void render(MultiBufferSource.BufferSource buffer, PoseStack poseStack, float partial,
                       long finishNanoTime, boolean renderBlockOutline, Camera camera,
                       GameRenderer renderer, LightTexture lightTexture, Matrix4f projection) {
        poseStack.pushPose();
        Vec3 relative = bPos.getCenter().subtract(camera.getPosition());
        poseStack.translate(relative.x, relative.y, relative.z);

        Vector3f step = face.step().mul(.5f);
        poseStack.translate(step.x, step.y, step.z);

        PoseStack.Pose pose = poseStack.last();
        poseStack.popPose();

        RenderUtil.drawTrueCube(buffer.getBuffer(RenderType.lightning()),
                pose.pose(), pose.normal(),
                .5f,
                1f, 0, 0, .75f);
        /*RenderUtil.drawDynamicCube(buffer.getBuffer(RenderType.lightning()),
                pose.pose(), pose.normal(),
                new Vector3f(-1.25f, -1.25f, -1.25f),
                new Vector3f(1.25f, -1.25f, -1.25f),
                new Vector3f(1.25f, -1.25f, 1.25f),
                new Vector3f(-1.25f, -1.25f, 1.25f),
                new Vector3f(-1.25f, 1.25f, -1.25f),
                new Vector3f(1.25f, 1.25f, -1.25f),
                new Vector3f(1.25f, 1.25f, 1.25f),
                new Vector3f(-1.25f, 1.25f, 1.25f),
                1f, 0, 0, .5f);*/

        /*float x1, y1, z1;
        float x2, y2, z2;
        float x3, y3, z3;
        float x4, y4, z4;
        switch (face){
            case DOWN -> {
                x1 = x4 = y1 = y2 = y3 = y4 = z3 = z4 = -0.5f;
                x2 = x3 = z1 = z2 = 0.5f;
            }
            case UP -> {
                x1 = x4 = z3 = z4 = -0.5f;
                x2 = x3 = y1 = y2 = y3 = y4 = z1 = z2 = 0.5f;
            }
            case NORTH -> {
                x1 = x4 = y1 = y2 = z1 = z2 = z3 = z4 = -0.5f;
                x2 = x3 = y3 = y4 = 0.5f;
            }
            case SOUTH -> {
                x1 = x4 = y1 = y2 = -0.5f;
                x2 = x3 = y3 = y4 = z1 = z2 = z3 = z4 = 0.5f;
            }
            case WEST -> {
                x1 = x2 = x3 = x4 = y1 = y2 = z1 = z4 = -0.5f;
                y3 = y4 = z2 = z3 = 0.5f;
            }
            case EAST -> {
                y1 = y2 = z1 = z4 = -0.5f;
                x1 = x2 = x3 = x4 = y3 = y4 = z2 = z3 = 0.5f;
            }
            default -> x1 = x2 = x3 = x4 = y1 = y2 = y3 = y4 = z1 = z2 = z3 = z4 = 0;
        }
        drawFace(buffer.getBuffer(RenderType.lightning()),
                pose.pose(), pose.normal(),
                x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                red, green, blue, .25f
        );*/
    }

    private void drawFace(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          float x3, float y3, float z3, float x4, float y4, float z4,
                          float red, float green, float blue, float alpha) {

        // First vertex (bottom left)
        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(red, green, blue, alpha)
                .uv((float) 0, (float) 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, (float) 0, (float) 1, (float) 0)
                .endVertex();

        // Second vertex (bottom right)
        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(red, green, blue, alpha)
                .uv((float) 1, (float) 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, (float) 0, (float) 1, (float) 0)
                .endVertex();

        // Third vertex (top right)
        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(red, green, blue, alpha)
                .uv((float) 1, (float) 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, (float) 0, (float) 1, (float) 0)
                .endVertex();

        // Fourth vertex (top left)
        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(red, green, blue, alpha)
                .uv((float) 0, (float) 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, (float) 0, (float) 1, (float) 0)
                .endVertex();
    }


    private void drawTaperedCube(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                                 float bottomWidth, float bottomDepth,
                                 float topWidth, float topDepth, float height,
                                 float red, float green, float blue, float alpha) {
        // Define bottom and top face dimensions
        float x1b = -bottomWidth / 2, x2b = bottomWidth / 2;  // Bottom face width
        float x1t = -topWidth / 2, x2t = topWidth / 2;        // Top face width
        float z1b = -bottomDepth / 2, z2b = bottomDepth / 2;  // Bottom face depth
        float z1t = -topDepth / 2, z2t = topDepth / 2;        // Top face depth
        float y1 = 0f, y2 = height;                // Height

        // Bottom Face (larger or smaller base)
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z2b, x2b, y1, z2b, x2b, y1, z1b, x1b, y1, z1b,
                red, green, blue, alpha);
        // Top Face (larger or smaller top)
        drawFace(vertexConsumer, matrix, normal, x1t, y2, z2t, x2t, y2, z2t, x2t, y2, z1t, x1t, y2, z1t,
                red, green, blue, alpha);

        // Front Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z2b, x2b, y1, z2b, x2t, y2, z2t, x1t, y2, z2t,
                red, green, blue, alpha);
        // Back Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z1b, x1b, y1, z1b, x1t, y2, z1t, x2t, y2, z1t,
                red, green, blue, alpha);
        // Left Face
        drawFace(vertexConsumer, matrix, normal, x1b, y1, z1b, x1b, y1, z2b, x1t, y2, z2t, x1t, y2, z1t,
                red, green, blue, alpha);
        // Right Face
        drawFace(vertexConsumer, matrix, normal, x2b, y1, z2b, x2b, y1, z1b, x2t, y2, z1t, x2t, y2, z2t,
                red, green, blue, alpha);

    }
}

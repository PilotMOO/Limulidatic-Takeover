package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3i;

public class FaceRenderPacket implements RenderDebuggerQue.IRenderInstructions {

    public FaceRenderPacket(Vector3i coord, int x, int y, int z){
        this.minorCoordinate = coord;
        xSet = x; ySet = y; zSet = z;
    }
    public Vector3i minorCoordinate;
    public int xSet, ySet, zSet;

    public Vector3i getCompleteMajor(){
        return getMajor(true, true, true);
    }
    public Vector3i getMajor(boolean x, boolean y, boolean z){
        return new Vector3i(x ? xSet : 0, y ? ySet : 0, z ? zSet : 0).add(minorCoordinate);
    }

    @Override
    public void render(MultiBufferSource.BufferSource buffer, PoseStack poseStack, float partial,
                       long finishNanoTime, boolean renderBlockOutline, Camera camera,
                       GameRenderer renderer, LightTexture lightTexture, Matrix4f projection) {
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

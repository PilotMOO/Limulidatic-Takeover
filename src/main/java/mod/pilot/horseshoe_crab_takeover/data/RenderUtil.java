package mod.pilot.horseshoe_crab_takeover.data;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {

    /**
     *broken!!!!!
     */
    public static void drawDynamicCube(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                                       Vector3f bottomLowerLeft, Vector3f bottomLowerRight,
                                       Vector3f bottomUpperLeft, Vector3f bottomUpperRight,
                                       Vector3f topLowerLeft, Vector3f topLowerRight,
                                       Vector3f topUpperLeft, Vector3f topUpperRight,
                                       float red, float green, float blue, float alpha){
        //Draw bottom face
        drawFace(vertexConsumer, matrix, normal,
                bottomLowerLeft.x, bottomLowerLeft.y, bottomLowerLeft.z,
                bottomLowerRight.x, bottomLowerRight.y, bottomLowerRight.z,
                bottomUpperLeft.x, bottomUpperLeft.y, bottomUpperLeft.z,
                bottomUpperRight.x, bottomUpperRight.y, bottomUpperRight.z,
                red, green, blue, alpha);

        //top face
        drawFace(vertexConsumer, matrix, normal,
                topLowerLeft.x, topLowerLeft.y, topLowerLeft.z,
                topLowerRight.x, topLowerRight.y, topLowerRight.z,
                topUpperLeft.x, topUpperLeft.y, topUpperLeft.z,
                topUpperRight.x, topUpperRight.y, topUpperRight.z,
                red, green, blue, alpha);

        //south face
        drawFace(vertexConsumer, matrix, normal,
                bottomLowerLeft.x, bottomLowerLeft.y, bottomLowerLeft.z,
                bottomLowerRight.x, bottomLowerRight.y, bottomLowerRight.z,
                topLowerLeft.x, topLowerLeft.y, topLowerLeft.z,
                topLowerRight.x, topLowerRight.y, topLowerRight.z,
                red, green, blue, alpha);

        //north face
        drawFace(vertexConsumer, matrix, normal,
                bottomUpperLeft.x, bottomUpperLeft.y, bottomUpperLeft.z,
                bottomUpperRight.x, bottomUpperRight.y, bottomUpperRight.z,
                topUpperLeft.x, topUpperLeft.y, topUpperLeft.z,
                topUpperRight.x, topUpperRight.y, topUpperRight.z,
                red, green, blue, alpha);

        //east face
        drawFace(vertexConsumer, matrix, normal,
                bottomLowerLeft.x, bottomLowerLeft.y, bottomLowerLeft.z,
                bottomUpperLeft.x, bottomUpperLeft.y, bottomUpperLeft.z,
                topUpperLeft.x, topUpperLeft.y, topUpperLeft.z,
                topLowerLeft.x, topLowerLeft.y, topLowerLeft.z,
                red, green, blue, alpha);

        //west face
        drawFace(vertexConsumer, matrix, normal,
                bottomLowerRight.x, bottomLowerRight.y, bottomLowerRight.z,
                bottomUpperRight.x, bottomUpperRight.y, bottomUpperRight.z,
                topUpperRight.x, topUpperRight.y, topUpperRight.z,
                topLowerRight.x, topLowerRight.y, topLowerRight.z,
                red, green, blue, alpha);
    }

    public static void drawTrueCube(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f normal,
                                    float size,
                                    float r, float g, float b, float a){
        float pos = size / 2f, neg = -pos;

        //Bottom
        drawFace(vertexConsumer, matrix4f, normal,
                neg, neg, neg,
                pos, neg, neg,
                pos, neg, pos,
                neg, neg, pos,
                r, g, b, a);
        //Top
        drawFace(vertexConsumer, matrix4f, normal,
                neg, pos, neg,
                pos, pos, neg,
                pos, pos, pos,
                neg, pos, pos,
                r, g, b, a);
        //South
        drawFace(vertexConsumer, matrix4f, normal,
                neg, neg, neg,
                pos, neg, neg,
                pos, pos, neg,
                neg, pos, neg,
                r, g, b, a);
        //North
        drawFace(vertexConsumer, matrix4f, normal,
                neg, neg, pos,
                pos, neg, pos,
                pos, pos, pos,
                neg, pos, pos,
                r, g, b, a);
        //East
        drawFace(vertexConsumer, matrix4f, normal,
                neg, neg, neg,
                neg, neg, pos,
                neg, pos, pos,
                neg, pos, neg,
                r, g, b, a);
        //West
        drawFace(vertexConsumer, matrix4f, normal,
                pos, neg, neg,
                pos, neg, pos,
                pos, pos, pos,
                pos, pos, neg,
                r, g, b, a);
    }

    public static void drawFace(VertexConsumer vertexConsumer, Matrix4f matrix, Matrix3f normal,
                          float x1, float y1, float z1, float x2, float y2, float z2,
                          float x3, float y3, float z3, float x4, float y4, float z4,
                          float red, float green, float blue, float alpha) {

        // First vertex (bottom left)
        vertexConsumer.vertex(matrix, x1, y1, z1)
                .color(red, green, blue, alpha)
                .uv(0f, 1f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();

        // Second vertex (bottom right)
        vertexConsumer.vertex(matrix, x2, y2, z2)
                .color(red, green, blue, alpha)
                .uv(1f, 1f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();

        // Third vertex (top right)
        vertexConsumer.vertex(matrix, x3, y3, z3)
                .color(red, green, blue, alpha)
                .uv(1f, 0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();

        // Fourth vertex (top left)
        vertexConsumer.vertex(matrix, x4, y4, z4)
                .color(red, green, blue, alpha)
                .uv(0f, 0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();
    }
}

package mod.pilot.horseshoe_crab_takeover.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.debugger.RenderDebuggerQue;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRenderInjectMixin {
    @Shadow @Final private RenderBuffers renderBuffers;

    /*@Inject(method = "renderLevel", at = @At(value = *//*"RETURN"*//* "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    public void debuggerHook(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline,
                             Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci){
        RenderDebuggerQue.renderAll(renderBuffers.bufferSource(), pPoseStack, pPartialTick, pFinishNanoTime, pRenderBlockOutline,
                pCamera, pGameRenderer, pLightTexture, pProjectionMatrix);
    }*/

    @Inject(method = "renderLevel", at = @At(value = "HEAD"))
    public void debuggerHook(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline,
                             Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci){
        RenderDebuggerQue.renderAll(renderBuffers.bufferSource(), pPoseStack, pPartialTick, pFinishNanoTime, pRenderBlockOutline,
                pCamera, pGameRenderer, pLightTexture, pProjectionMatrix);
    }
}

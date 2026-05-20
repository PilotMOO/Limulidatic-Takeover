package mod.pilot.horseshoe_crab_takeover.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.NodeVisualizerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NodeVisualizerRenderer extends EntityRenderer<NodeVisualizerEntity> {
    protected NodeVisualizerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(NodeVisualizerEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull NodeVisualizerEntity pEntity) {
        return new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "textures/entity/fuck_you.png");
    }
}

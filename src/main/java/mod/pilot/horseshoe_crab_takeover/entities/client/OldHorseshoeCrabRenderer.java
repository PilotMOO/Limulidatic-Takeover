package mod.pilot.horseshoe_crab_takeover.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.OriginalHorseshoeCrabEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OldHorseshoeCrabRenderer extends MobRenderer<OriginalHorseshoeCrabEntity, OldHorseshoeCrabModel<OriginalHorseshoeCrabEntity>> {
    public OldHorseshoeCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new OldHorseshoeCrabModel<>(context.bakeLayer(OldHorseshoeCrabModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull OriginalHorseshoeCrabEntity pEntity) {
        return new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "textures/entity/horseshoe_crab_texture.png");
    }

    @Override
    public void render(@NotNull OriginalHorseshoeCrabEntity crab, float yaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float size = crab.getSize();
        poseStack.scale(size, size, size);

        super.render(crab, yaw, partialTicks, poseStack, buffer, packedLight);
    }
}

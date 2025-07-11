package mod.pilot.horseshoe_crab_takeover.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.ModifiedHorseshoeCrabEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HorseshoeCrabRenderer extends LivingEntityRenderer<ModifiedHorseshoeCrabEntity, HorseshoeCrabModel<ModifiedHorseshoeCrabEntity>> {
    public HorseshoeCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseshoeCrabModel<>(context.bakeLayer(HorseshoeCrabModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ModifiedHorseshoeCrabEntity pEntity) {
        return new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "textures/entity/horseshoe_crab_texture.png");
    }

    @Override
    public void render(@NotNull ModifiedHorseshoeCrabEntity crab, float yaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float size = crab.getSize();
        poseStack.scale(size, size, size);
        super.render(crab, yaw, partialTicks, poseStack, buffer, packedLight);
    }
}

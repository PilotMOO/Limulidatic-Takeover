package mod.pilot.horseshoe_crab_takeover.entities.client;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeAnimationDefinitions;
import mod.pilot.horseshoe_crab_takeover.entities.HorseshoeCrabEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class HorseshoeCrabModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, "horseshoe_crab_texture"), "main");
	private final ModelPart crab;
	private final ModelPart Rim;
	private final ModelPart LeftSpikes;
	private final ModelPart RightSpikes;
	private final ModelPart Tail;
	private final ModelPart TailSpikes;

	public HorseshoeCrabModel(ModelPart root) {
		this.crab = root.getChild("crab");
		this.Rim = this.crab.getChild("Rim");
		this.LeftSpikes = this.Rim.getChild("LeftSpikes");
		this.RightSpikes = this.Rim.getChild("RightSpikes");
		this.Tail = this.crab.getChild("Tail");
		this.TailSpikes = this.Tail.getChild("TailSpikes");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition crab = partdefinition.addOrReplaceChild("crab", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -9.0F, 10.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 0.0F));

		PartDefinition BackRight_r1 = crab.addOrReplaceChild("BackRight_r1", CubeListBuilder.create().texOffs(0, 49).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.8882F, -1.6307F, 0.4911F, -0.2647F, 0.4721F, -0.1962F));

		PartDefinition BackLeft_r1 = crab.addOrReplaceChild("BackLeft_r1", CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8882F, -1.6307F, 0.4911F, -0.2647F, -0.4721F, 0.1962F));

		PartDefinition Top_r1 = crab.addOrReplaceChild("Top_r1", CubeListBuilder.create().texOffs(36, 37).addBox(-5.0F, -1.0F, -6.0F, 10.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition Rim = crab.addOrReplaceChild("Rim", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Right_r1 = Rim.addOrReplaceChild("Right_r1", CubeListBuilder.create().texOffs(0, 13).mirror().addBox(-3.0F, 0.0F, -5.0F, 5.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.0F, -2.0F, -3.0F, -0.0674F, 0.1611F, -0.3981F));

		PartDefinition Left_r1 = Rim.addOrReplaceChild("Left_r1", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, 0.0F, -5.0F, 5.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.0F, -3.0F, -0.0674F, -0.1611F, 0.3981F));

		PartDefinition FrontRight_r1 = Rim.addOrReplaceChild("FrontRight_r1", CubeListBuilder.create().texOffs(0, 43).mirror().addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -0.9476F, -11.7907F, 0.3927F, 0.7418F, 0.0F));

		PartDefinition FrontLeft_r1 = Rim.addOrReplaceChild("FrontLeft_r1", CubeListBuilder.create().texOffs(0, 43).addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -0.9476F, -11.7907F, 0.3927F, -0.7418F, 0.0F));

		PartDefinition FrontRidge_r1 = Rim.addOrReplaceChild("FrontRidge_r1", CubeListBuilder.create().texOffs(40, 46).addBox(-0.5F, -0.5F, -4.75F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.9831F, -5.7675F, 0.3054F, 0.0F, 0.0F));

		PartDefinition Front_r1 = Rim.addOrReplaceChild("Front_r1", CubeListBuilder.create().texOffs(36, 28).addBox(-5.0F, 0.0F, -2.75F, 10.0F, 2.0F, 7.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, -9.25F, 0.3927F, 0.0F, 0.0F));

		PartDefinition LeftSpikes = Rim.addOrReplaceChild("LeftSpikes", CubeListBuilder.create(), PartPose.offsetAndRotation(9.2373F, 0.25F, -6.0744F, 0.0F, -0.1745F, 0.1745F));

		PartDefinition Spike_r1 = LeftSpikes.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 10.4244F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r2 = LeftSpikes.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 9.3244F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r3 = LeftSpikes.addOrReplaceChild("Spike_r3", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.225F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 8.0744F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r4 = LeftSpikes.addOrReplaceChild("Spike_r4", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 6.8244F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r5 = LeftSpikes.addOrReplaceChild("Spike_r5", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.1F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 5.1744F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r6 = LeftSpikes.addOrReplaceChild("Spike_r6", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.1F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 3.3244F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r7 = LeftSpikes.addOrReplaceChild("Spike_r7", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, 1.3244F, 0.0F, 1.0036F, 0.0F));

		PartDefinition Spike_r8 = LeftSpikes.addOrReplaceChild("Spike_r8", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.7373F, 0.0F, -0.6756F, 0.0F, 1.0036F, 0.0F));

		PartDefinition RightSpikes = Rim.addOrReplaceChild("RightSpikes", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.2373F, 0.25F, -6.0744F, 0.0F, 0.1745F, -0.1745F));

		PartDefinition Spike_r9 = RightSpikes.addOrReplaceChild("Spike_r9", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 10.4244F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r10 = RightSpikes.addOrReplaceChild("Spike_r10", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 9.3244F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r11 = RightSpikes.addOrReplaceChild("Spike_r11", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.225F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 8.0744F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r12 = RightSpikes.addOrReplaceChild("Spike_r12", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 6.8244F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r13 = RightSpikes.addOrReplaceChild("Spike_r13", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.1F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 5.1744F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r14 = RightSpikes.addOrReplaceChild("Spike_r14", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, -0.1F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 3.3244F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r15 = RightSpikes.addOrReplaceChild("Spike_r15", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7373F, 0.0F, 1.3244F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Spike_r16 = RightSpikes.addOrReplaceChild("Spike_r16", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.7373F, 0.0F, -0.6756F, 0.0F, -1.0036F, 0.0F));

		PartDefinition Tail = crab.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(36, 13).addBox(-0.5F, 0.75F, 6.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 1.0F));

		PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(42, 0).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition TailSpikes = Tail.addOrReplaceChild("TailSpikes", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -1.0F));

		PartDefinition Spike_r17 = TailSpikes.addOrReplaceChild("Spike_r17", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6522F, 5.7654F, 0.3927F, 0.0F, 0.0F));

		PartDefinition Spike_r18 = TailSpikes.addOrReplaceChild("Spike_r18", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.1522F, 3.5154F, 0.3927F, 0.0F, 0.0F));

		PartDefinition Spike_r19 = TailSpikes.addOrReplaceChild("Spike_r19", CubeListBuilder.create().texOffs(42, 10).addBox(-0.5F, -0.5F, 0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6522F, 1.2654F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.crab.getAllParts().forEach(ModelPart::resetPose);

		this.animate(((HorseshoeCrabEntity) entity).walkAnimationState, HorseshoeAnimationDefinitions.MOVE, ageInTicks, (float) (1f * ((HorseshoeCrabEntity) entity).getAverageHorizontalMovementSpeed()) + 1);
	}

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		crab.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}

	@Override
	public @NotNull ModelPart root() {
		return crab;
	}
}
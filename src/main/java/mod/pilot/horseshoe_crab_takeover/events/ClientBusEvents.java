package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.client.HorseshoeCrabModel;
import mod.pilot.horseshoe_crab_takeover.entities.client.HorseshoeCrabRenderer;
import mod.pilot.horseshoe_crab_takeover.entities.client.OldHorseshoeCrabModel;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import mod.pilot.horseshoe_crab_takeover.entities.client.OldHorseshoeCrabRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Horseshoe_Crab_Takeover.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(OldHorseshoeCrabModel.LAYER_LOCATION, OldHorseshoeCrabModel::createBodyLayer);
        event.registerLayerDefinition(HorseshoeCrabModel.LAYER_LOCATION, HorseshoeCrabModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(HorseshoeEntities.OLD_HORSESHOE_CRAB.get(), OldHorseshoeCrabRenderer::new);
        EntityRenderers.register(HorseshoeEntities.HORSESHOE_CRAB.get(), HorseshoeCrabRenderer::new);
    }
}

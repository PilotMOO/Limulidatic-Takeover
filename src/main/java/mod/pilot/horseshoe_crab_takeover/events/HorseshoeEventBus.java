package mod.pilot.horseshoe_crab_takeover.events;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.HorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.entities.common.HorseshoeEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Horseshoe_Crab_Takeover.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HorseshoeEventBus {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(HorseshoeEntities.HORSESHOE_CRAB.get(), HorseshoeCrabEntity.createAttributes().build());
    }
}

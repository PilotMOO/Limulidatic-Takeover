package mod.pilot.horseshoe_crab_takeover.entities.common;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import mod.pilot.horseshoe_crab_takeover.entities.ModifiedHorseshoeCrabEntity;
import mod.pilot.horseshoe_crab_takeover.entities.OriginalHorseshoeCrabEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HorseshoeEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Horseshoe_Crab_Takeover.MOD_ID);

    public static final RegistryObject<EntityType<OriginalHorseshoeCrabEntity>> OLD_HORSESHOE_CRAB =
            ENTITY_TYPES.register("old_horseshoe_crab", () -> EntityType.Builder.of(OriginalHorseshoeCrabEntity::new, MobCategory.MONSTER)
                    .sized(1f, 0.4f).build("old_horseshoe_crab"));

    public static final RegistryObject<EntityType<ModifiedHorseshoeCrabEntity>> HORSESHOE_CRAB =
            ENTITY_TYPES.register("horseshoe_crab", () -> EntityType.Builder.of(ModifiedHorseshoeCrabEntity::new, MobCategory.MONSTER)
                    .sized(1f, 0.4f).build("horseshoe_crab"));



    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}

package mod.pilot.horseshoe_crab_takeover.damagetypes;

import mod.pilot.horseshoe_crab_takeover.Horseshoe_Crab_Takeover;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class HorseshoeDamageTypes {
    public static ResourceKey<DamageType> create(String id){
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Horseshoe_Crab_Takeover.MOD_ID, id));
    }

    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey));
    }
    public static DamageSource damageSource(Entity entity, ResourceKey<DamageType> registryKey, @Nullable Entity entity2){
        return new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(registryKey), entity2);
    }

    public static final ResourceKey<DamageType> CRABBED1 = create("crabbed_1");
    public static final ResourceKey<DamageType> CRABBED2 = create("crabbed_2");
    public static final ResourceKey<DamageType> CRABBED3 = create("crabbed_3");

    public static DamageSource crabbed(LivingEntity entity){
        switch (entity.getRandom().nextIntBetweenInclusive(1, 3)){
            default -> {
                return damageSource(entity, CRABBED1, entity);
            }
            case 2 -> {
                return damageSource(entity, CRABBED2, entity);
            }
            case 3 -> {
                return damageSource(entity, CRABBED3, entity);
            }
        }
    }

}

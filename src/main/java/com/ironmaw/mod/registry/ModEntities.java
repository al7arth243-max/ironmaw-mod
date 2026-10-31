package com.ironmaw.mod.registry;

import com.ironmaw.mod.IronmawMod;
import com.ironmaw.mod.entity.IronmawEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IronmawMod.MODID);

    /**
     * Hitbox: wolf is 0.6 x 0.85, polar bear is 1.4 x 1.4.
     * The Ironmaw sits in between at 1.1 wide x 1.2 tall.
     */
    public static final RegistryObject<EntityType<IronmawEntity>> IRONMAW = ENTITY_TYPES.register("ironmaw",
            () -> EntityType.Builder.of(IronmawEntity::new, MobCategory.CREATURE)
                    .sized(1.1F, 1.2F)
                    .clientTrackingRange(10)
                    .build(IronmawMod.MODID + ":ironmaw"));

    private ModEntities() {}
}

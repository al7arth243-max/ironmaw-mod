package com.ironmaw.mod;

import com.ironmaw.mod.entity.IronmawEntity;
import com.ironmaw.mod.registry.ModEntities;
import com.ironmaw.mod.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IronmawMod.MODID)
public class IronmawMod {
    public static final String MODID = "ironmaw";

    public IronmawMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);

        modBus.addListener(this::registerAttributes);
        modBus.addListener(this::addCreativeTabItems);

        // Disease Sample -> wolf transformation lives on the Forge (game) event bus
        MinecraftForge.EVENT_BUS.register(com.ironmaw.mod.event.TransformHandler.class);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.IRONMAW.get(), IronmawEntity.createAttributes().build());
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DISEASE_SAMPLE);
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.IRONMAW_SPAWN_EGG);
        }
    }
}

package com.ironmaw.mod.registry;

import com.ironmaw.mod.IronmawMod;
import com.ironmaw.mod.item.DiseaseSampleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IronmawMod.MODID);

    public static final RegistryObject<Item> DISEASE_SAMPLE = ITEMS.register("disease_sample",
            () -> new DiseaseSampleItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> IRONMAW_SPAWN_EGG = ITEMS.register("ironmaw_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.IRONMAW, 0x4A2C26, 0x7A1F2B, new Item.Properties()));

    private ModItems() {}
}

package com.ironmaw.mod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The actual transformation is handled in {@link com.ironmaw.mod.event.TransformHandler}.
 * It has to be an event because a tamed wolf normally "eats" the right-click (it toggles sitting)
 * before the held item ever gets a chance to react.
 */
public class DiseaseSampleItem extends Item {
    public DiseaseSampleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ironmaw.disease_sample.tooltip").withStyle(ChatFormatting.DARK_GREEN));
    }
}

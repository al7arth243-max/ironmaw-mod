package com.ironmaw.mod.event;

import com.ironmaw.mod.entity.IronmawEntity;
import com.ironmaw.mod.registry.ModEntities;
import com.ironmaw.mod.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class TransformHandler {

    private TransformHandler() {}

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.DISEASE_SAMPLE.get())) return;

        // Only plain vanilla wolves (an Ironmaw is also a Wolf subclass, so check the exact type)
        if (!(event.getTarget() instanceof Wolf wolf) || wolf.getType() != EntityType.WOLF) return;

        Player player = event.getEntity();
        Level level = event.getLevel();

        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ironmaw.not_your_wolf"), true);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }
        if (wolf.isBaby()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ironmaw.too_young"), true);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }

        // Stop the wolf's own interaction (which would toggle sitting) from running
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        boolean wasOrderedToSit = wolf.isOrderedToSit();
        boolean wasInSittingPose = wolf.isInSittingPose();
        float bodyRot = wolf.yBodyRot;
        float headRot = wolf.yHeadRot;

        // Vanilla conversion helper: copies position/rotation, custom name, persistence, then removes the wolf
        IronmawEntity ironmaw = wolf.convertTo(ModEntities.IRONMAW.get(), false);
        if (ironmaw == null) return;

        ironmaw.setTame(true);
        ironmaw.setOwnerUUID(player.getUUID());
        ironmaw.setOrderedToSit(wasOrderedToSit);
        ironmaw.setInSittingPose(wasInSittingPose);
        ironmaw.copyCollarFrom(wolf);
        ironmaw.setPersistenceRequired();
        ironmaw.yBodyRot = bodyRot;
        ironmaw.yHeadRot = headRot;
        ironmaw.setHealth(ironmaw.getMaxHealth());
        ForgeEventFactory.onLivingConvert(wolf, ironmaw);

        double x = ironmaw.getX();
        double y = ironmaw.getY() + 0.6D;
        double z = ironmaw.getZ();
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 30, 0.6D, 0.5D, 0.6D, 0.02D);
        serverLevel.sendParticles(ParticleTypes.SNEEZE, x, y, z, 40, 0.6D, 0.5D, 0.6D, 0.05D);
        serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y + 0.4D, z, 10, 0.5D, 0.3D, 0.5D, 0.1D);
        serverLevel.playSound(null, x, y, z, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.NEUTRAL, 1.0F, 0.6F);
        serverLevel.playSound(null, x, y, z, SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 1.5F, 0.5F);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }
}

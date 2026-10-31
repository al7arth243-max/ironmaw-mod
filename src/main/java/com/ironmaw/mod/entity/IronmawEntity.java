package com.ironmaw.mod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * The Ironmaw is a Wolf subclass, so it inherits ALL vanilla wolf behaviour:
 * taming, sit/stand on right-click, following its owner, teleporting to the owner,
 * defending the owner, attacking what the owner attacks, healing with meat, collar dyeing, etc.
 * It only changes stats, size and the model/animations.
 */
public class IronmawEntity extends Wolf implements GeoEntity {

    // Stats (vanilla tamed wolf: 20 HP, 4 damage, 0.3 speed, 0 armor)
    public static final double MAX_HEALTH = 60.0D;
    public static final double ATTACK_DAMAGE = 9.0D;
    public static final double MOVE_SPEED = 0.32D;
    public static final double ARMOR = 6.0D;
    public static final double KNOCKBACK_RESISTANCE = 0.6D;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.ironmaw.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.ironmaw.walk");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("animation.ironmaw.sit");
    private static final RawAnimation LASH = RawAnimation.begin().thenPlay("animation.ironmaw.tongue_lash");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public IronmawEntity(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVE_SPEED)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
    }

    /**
     * Vanilla Wolf.setTame() hard-resets max health to 20 and damage to 4.
     * It also sets health to 20 when tamed. It runs when tamed and every time the entity is loaded
     * from disk, so re-apply our stats afterwards and restore the health the Ironmaw had before.
     */
    @Override
    public void setTame(boolean tame) {
        float healthBefore = this.getHealth();
        super.setTame(tame);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        if (tame) {
            this.setHealth(healthBefore > 0.0F ? Math.min(healthBefore, this.getMaxHealth()) : this.getMaxHealth());
        }
    }

    /** Copies the collar colour across from the wolf this Ironmaw was made from. */
    public void copyCollarFrom(Wolf wolf) {
        DyeColor color = wolf.getCollarColor();
        CompoundTag tag = new CompoundTag();
        this.addAdditionalSaveData(tag);
        tag.putByte("CollarColor", (byte) color.getId());
        this.readAdditionalSaveData(tag);
    }

    /**
     * Vanilla wolves turn their body mostly by walking. The Ironmaw is big enough to bite from where it
     * stands, so without this it would attack sideways. While it has a target, swing the whole body
     * (and head) to face it, up to 30 degrees per tick.
     */
    @Override
    public void tick() {
        super.tick();
        if (this.isInSittingPose()) return;

        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                if (dx * dx + dz * dz > 1.0E-4D) {
                    float wanted = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                    float yaw = Mth.approachDegrees(this.getYRot(), wanted, 30.0F);
                    this.setYRot(yaw);
                    this.yBodyRot = yaw;
                    this.yHeadRot = yaw;
                }
            }
        } else if (this.isAggressive()) {
            // Client side: the target isn't known here, but the head direction is synced, so turn the body after it
            this.yBodyRot = Mth.approachDegrees(this.yBodyRot, this.yHeadRot, 30.0F);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && !this.level().isClientSide) {
            this.triggerAnim("attack", "lash");
        }
        return hit;
    }

    @Override
    public float getVoicePitch() {
        // Deeper, beefier wolf sounds
        return super.getVoicePitch() * 0.7F;
    }

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    // ---------------- GeckoLib ----------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, this::mainPredicate));
        controllers.add(new AnimationController<>(this, "attack", 2, state -> PlayState.STOP)
                .triggerableAnim("lash", LASH));
    }

    private PlayState mainPredicate(AnimationState<IronmawEntity> state) {
        if (this.isInSittingPose()) {
            state.getController().setAnimationSpeed(1.0D);
            return state.setAndContinue(SIT);
        }
        if (state.isMoving()) {
            // Faster leg cycle when running (e.g. chasing a target or catching up to the owner)
            double speed = 0.6D + state.getLimbSwingAmount() * 1.4D;
            state.getController().setAnimationSpeed(Math.max(0.8D, Math.min(2.2D, speed)));
            return state.setAndContinue(WALK);
        }
        state.getController().setAnimationSpeed(1.0D);
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

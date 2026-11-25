package dev.fluttercat.rocketjumping.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fluttercat.rocketjumping.RocketJumping;
import dev.fluttercat.rocketjumping.TempInterface;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 5000)
public abstract class LivingEntityMixin extends Entity implements TempInterface { //note: abstract == dont need deps as they are kept default

    @Unique
    private Vec3 rocketjumping$fixEnchancementVelocity;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract @Nullable AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Unique
    private boolean isRocketJumping;

    @Override
    public void rocketjumping$setRocketJumping(boolean value) {
        isRocketJumping = value;
    }

    @Override
    public boolean rocketjumping$isRocketJumping() {
        return this.isRocketJumping;
    }

    @ModifyReturnValue(method = "getEffectiveGravity", at = @At("RETURN"))
    public double getEffectiveGravity(double original) {
        if (this.rocketjumping$isRocketJumping() && this.getDeltaMovement().y() < 0 && !this.hasEffect(MobEffects.SLOW_FALLING)) {
            return original - 0.01f;
        }
        return original;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
        AttributeInstance drag = this.getAttribute(Attributes.AIR_DRAG_MODIFIER);
        if (gravity != null && drag != null) {
            if (this.rocketjumping$isRocketJumping() && !this.hasEffect(MobEffects.SLOW_FALLING)) {
                gravity.setBaseValue(0.06);
                drag.setBaseValue(0.01);
                RocketJumping.LOGGER.info("a");
            } else {
                gravity.setBaseValue(0.08);
                drag.setBaseValue(1.0);
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        rocketjumping$fixEnchancementVelocity = this.getDeltaMovement();
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dealDefaultKnockback(Lnet/minecraft/world/damagesource/DamageSource;FZ)V", shift = At.Shift.AFTER))
    private void actuallyWhatIfWeDontRebalanceProjectiles(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.FIREWORKS)) {
            setDeltaMovement(rocketjumping$fixEnchancementVelocity);
        }
    }
}

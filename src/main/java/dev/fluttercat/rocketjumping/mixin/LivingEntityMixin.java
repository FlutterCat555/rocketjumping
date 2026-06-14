package dev.fluttercat.rocketjumping.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fluttercat.rocketjumping.RocketJumping;
import dev.fluttercat.rocketjumping.TempInterface;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements TempInterface { //note: abstract == dont need deps as they are kept default


    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);


    @Unique
    private static final EntityDataAccessor<Boolean> ROCKET_JUMPING =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL")) //i hate that ai codes better than me. can i just have one win...
    private void defineRocketJumpData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ROCKET_JUMPING, false);
    }

    @Override
    public void rocketjumping$setRocketJumping(boolean value) {
        this.getEntityData().set(ROCKET_JUMPING, value);
    }

    @Override
    public boolean rocketjumping$isRocketJumping() {
        return this.getEntityData().get(ROCKET_JUMPING);
    }

    @ModifyReturnValue(method = "getEffectiveGravity", at = @At("RETURN"))
    public double getEffectiveGravity(double original) {
            if (this.rocketjumping$isRocketJumping() && this.getDeltaMovement().y() < 0 && !this.hasEffect(MobEffects.SLOW_FALLING)) {
                return original - 0.025f;
            }
        return original;
    }
}

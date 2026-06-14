package dev.fluttercat.rocketjumping.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fluttercat.rocketjumping.RocketJumping;
import dev.fluttercat.rocketjumping.TempInterface;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements TempInterface { //REMEMBER: abstract = defined somewhere else.  idk why but it has to be in the class def also?
    //maybe i'd understand this if i actually studied

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
            if (this.onGround()) {
                this.rocketjumping$setRocketJumping(false);
            }
    }

    @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
    public float airSpeed(float original) {
            if (this.rocketjumping$isRocketJumping()) {
                return original * 3f;
            }
        return original;
    }
}
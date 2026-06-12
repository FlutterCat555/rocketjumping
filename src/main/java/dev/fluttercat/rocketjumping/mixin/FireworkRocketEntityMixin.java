package dev.fluttercat.rocketjumping.mixin;


import dev.fluttercat.rocketjumping.RocketJumping;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile {
    public FireworkRocketEntityMixin(EntityType<? extends @NotNull Projectile> type, Level level) {
        super(type, level);
    }

    @Redirect(
            method = "dealExplosionDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 1
            )
    )
    private boolean dealExplosionDamage(LivingEntity target, ServerLevel level, DamageSource source, float damage) {
        Vec3 rocketPos = this.position();
        Vec3 targetPos = target.position();
        Vec3 direction = targetPos.subtract(rocketPos).normalize();
        double scale;
        if(target==this.getOwner()) {
            if(direction.y<0.1){
                direction = new Vec3(direction.x/3,direction.y+1,direction.z/3);
            }
            scale = 1.75;
            damage = damage / 2;
            direction = new Vec3(direction.x * 3, direction.y, direction.z * 3 );
            target.setOnGround(false);
            target.setDeltaMovement(direction.scale(scale));
            target.hurtMarked = true;
            target.setIgnoreFallDamageFromCurrentImpulse(true,this.position());
            RocketJumping.rocketJumpingPlayers.add(target.getUUID());
        }
        return target.hurtServer(level, source, damage);
    }
}
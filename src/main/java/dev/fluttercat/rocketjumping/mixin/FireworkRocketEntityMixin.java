package dev.fluttercat.rocketjumping.mixin;


import dev.fluttercat.rocketjumping.TempInterface;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile implements TempInterface {
    @Shadow
    protected abstract List<FireworkExplosion> getExplosions();

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
        Vec3 direction = target.position().subtract(this.position()).normalize();
        if(target==this.getOwner()) {
            damage = damage / 2;
        }
        if(direction.y==0.0) {
            direction = direction.add(0,0.75,0); //launch up if on ground
        }
        direction = direction.add(0,0.2,0);//TODO: finish balancing
        int explosions = this.getExplosions().size();
        double mult = 1.25+((double) explosions /4);
        target.setDeltaMovement(direction.scale(mult));

        target.setIgnoreFallDamageFromCurrentImpulse(true,this.position());


        ((TempInterface)target).rocketjumping$setRocketJumping(true); //cast to interface because otherwise no
        target.setOnGround(false);
        target.hurtMarked = true;
        return target.hurtServer(level, source, damage);
    }
}
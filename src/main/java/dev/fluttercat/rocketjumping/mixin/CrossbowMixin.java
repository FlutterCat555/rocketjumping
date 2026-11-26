package dev.fluttercat.rocketjumping.mixin;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Predicate;

import static net.minecraft.world.item.ProjectileWeaponItem.ARROW_OR_FIREWORK;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {
    /**
     * @author fluttercat555
     * @reason allow loading both firework and arrow from inventory
     */
    @Overwrite
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_OR_FIREWORK;
    }
}
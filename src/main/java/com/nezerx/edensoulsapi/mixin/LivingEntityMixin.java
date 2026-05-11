package com.nezerx.edensoulsapi.mixin;

import com.nezerx.edensoulsapi.roll.RollingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurt_HEAD(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        if (!((Object) this instanceof Player player)) return;

        RollingEntity rolling = (RollingEntity) player;
        if (rolling.getRollManager().isInvulnerable()) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
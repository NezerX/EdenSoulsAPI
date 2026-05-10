package com.nezerx.edensouls.mixin;

import com.nezerx.edensouls.roll.RollManager;
import com.nezerx.edensouls.roll.RollingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.player.Player.class)
public abstract class PlayerMixin implements RollingEntity {

    @Unique
    private final RollManager edensouls$rollManager = new RollManager();

    @Override
    public RollManager getRollManager() {
        return edensouls$rollManager;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tick_TAIL(CallbackInfo ci) {
        edensouls$rollManager.tick((Player)(Object)this);
    }
}
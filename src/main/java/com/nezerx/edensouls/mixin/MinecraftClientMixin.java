package com.nezerx.edensouls.mixin;

import com.nezerx.edensouls.KeyBindings;
import com.nezerx.edensouls.client.RollAnimationHandler;
import com.nezerx.edensouls.config.RollConfig;
import com.nezerx.edensouls.roll.RollDirectionProvider;
import com.nezerx.edensouls.roll.RollManager;
import com.nezerx.edensouls.roll.RollingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 449)
public abstract class MinecraftClientMixin {

    @Shadow
    public @Nullable LocalPlayer player;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void doAttack_HEAD(CallbackInfoReturnable<Boolean> info) {
        RollingEntity rollingPlayer = (RollingEntity) this.player;
        if (rollingPlayer != null && rollingPlayer.getRollManager().isRolling()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking_HEAD(boolean bl, CallbackInfo ci) {
        RollingEntity rollingPlayer = (RollingEntity) this.player;
        if (rollingPlayer != null && rollingPlayer.getRollManager().isRolling()) {
            ci.cancel();
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void doItemUse_HEAD(CallbackInfo ci) {
        RollingEntity rollingPlayer = (RollingEntity) this.player;
        if (rollingPlayer != null && rollingPlayer.getRollManager().isRolling()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void handleInputEvents_TAIL(CallbackInfo ci) {
        this.tryRolling();
    }

    private void tryRolling() {
        Minecraft client = (Minecraft) (Object) this;
        if (this.player == null || client.isPaused() || client.screen != null) return;

        RollingEntity rollingPlayer = (RollingEntity) this.player;
        RollManager rollManager = rollingPlayer.getRollManager();

        if (!KeyBindings.ROLL.isDown()) return;
        if (!rollManager.isRollAvailable(this.player)) return;
        if (!RollConfig.get().allow_rolling_while_airborn && !this.player.onGround()) return;
        if (this.player.getFoodData().getFoodLevel() <= RollConfig.get().food_level_required) return;
        if (this.player.isSwimming() || this.player.isVisuallyCrawling()) return;
        if (this.player.getVehicle() != null) return;
        if (this.player.isUsingItem() || this.player.isBlocking()) return;

        float forward = this.player.input.forwardImpulse;
        float sideways = this.player.input.leftImpulse;

        Vec3 direction;
        if (forward == 0.0f && sideways == 0.0f) {
            // Если стоим — перекат вперёд относительно камеры
            direction = new Vec3(0.0, 0.0, 1.0);
        } else {
            direction = new Vec3(sideways, 0.0, forward).normalize();
        }

        // Переводим в мировые координаты
        direction = direction.yRot((float) Math.toRadians(-1.0 * this.player.getYRot()));

        // Угол направления движения для фиксации модели
        float directionYRot = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        ((RollDirectionProvider) this.player).edensouls$setRollDirectionYRot(directionYRot);

        // Скорость = расстояние / время анимации.
        RollConfig.RollTypeConfig cfg = RollConfig.get().getConfig(rollManager.getRollType());
        double speed = cfg.distance_blocks / cfg.animation_ticks;
        direction = direction.scale(speed);

        rollManager.onRoll(this.player, direction);
        RollAnimationHandler.playAnimation((AbstractClientPlayer) this.player, rollManager.getRollType());
    }
}
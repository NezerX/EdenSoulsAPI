package com.nezerx.edensoulsapi.mixin;

import com.nezerx.edensoulsapi.roll.RollingEntity;
import com.nezerx.edensoulsapi.roll.RollDirectionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends LivingEntity implements RollDirectionProvider {

    protected LocalPlayerMixin() {
        super(null, null);
    }

    @Unique private float edensouls$lockedYRot = 0f;
    @Unique private float edensouls$lockedYHeadRot = 0f;
    @Unique private boolean edensouls$wasRolling = false;
    @Unique private float edensouls$rollDirectionYRot = 0f;

    @Override
    public void edensouls$setRollDirectionYRot(float yRot) {
        this.edensouls$rollDirectionYRot = yRot;
        this.edensouls$lockedYRot = yRot;
        this.edensouls$lockedYHeadRot = yRot;
    }

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void aiStep_afterInputTick(CallbackInfo ci) {
        if (((RollingEntity) this).getRollManager().isRolling()) {
            LocalPlayer self = (LocalPlayer) (Object) this;
            self.input.forwardImpulse = 0f;
            self.input.leftImpulse = 0f;
            self.input.jumping = false;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick_HEAD(CallbackInfo ci) {
        if (((RollingEntity) this).getRollManager().isRolling()) {
            this.jumping = false;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick_TAIL(CallbackInfo ci) {
        RollingEntity rolling = (RollingEntity) this;

        if (rolling.getRollManager().isRolling()) {
            if (!edensouls$wasRolling) {
                edensouls$lockedYRot = edensouls$rollDirectionYRot;
                edensouls$lockedYHeadRot = edensouls$rollDirectionYRot;
                edensouls$wasRolling = true;
            }
            this.yBodyRot = edensouls$lockedYRot;
            this.yHeadRot = edensouls$lockedYHeadRot;
            this.yBodyRotO = edensouls$lockedYRot;
            this.yHeadRotO = edensouls$lockedYHeadRot;

            LocalPlayer self = (LocalPlayer) (Object) this;
            self.input.forwardImpulse = 0f;
            self.input.leftImpulse = 0f;
            self.input.jumping = false;

        } else {
            edensouls$wasRolling = false;
        }
    }
}
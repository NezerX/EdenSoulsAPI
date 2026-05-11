package com.nezerx.edensoulsapi.roll;

import com.nezerx.edensoulsapi.config.RollConfig;
import com.nezerx.edensoulsapi.config.RollConfig.RollTypeConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RollManager {

    private RollType rollType = RollType.NORMAL;
    private int movementTicks = 0;
    private boolean rolling = false;
    private boolean invulnerable = false;

    private int animationTicks = 0;
    private int iframeTicks = 0;
    private int startupTicks = 0;
    private int cooldownTicks = 0;

    private Vec3 rollVelocity = Vec3.ZERO;

    private static final int ROLL_COOLDOWN = 20;

    // ── публичное API ──────────────────────────────────────────────

    public void setRollType(RollType type) {
        this.rollType = type;
    }

    public RollType getRollType() {
        return rollType;
    }

    public boolean isRolling() {
        return rolling;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public boolean isRollAvailable(Player player) {
        return !rolling && cooldownTicks <= 0;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    // ── запуск ролла ───────────────────────────────────────────────

    public void onRoll(Player player, Vec3 velocity) {
        RollTypeConfig cfg = RollConfig.get().getConfig(rollType);
        movementTicks = cfg.movement_ticks;
        rolling = true;
        invulnerable = false;
        animationTicks = cfg.animation_ticks;
        iframeTicks = cfg.iframes;
        startupTicks = cfg.startup_delay;
        cooldownTicks = ROLL_COOLDOWN;
        rollVelocity = velocity;

        // i-frames для normal и no_roll стартуют сразу
        if (startupTicks <= 0 && rollType != RollType.FAT_ROLL) {
            invulnerable = iframeTicks > 0;
        }
    }

    // ── тик ───────────────────────────────────────────────────────

    public void tick(Player player) {
        if (rolling) {
            // задержка fat_roll
            if (startupTicks > 0) {
                startupTicks--;
                if (startupTicks == 0) {
                    invulnerable = iframeTicks > 0;
                }
            }

            // применяем velocity каждый тик пока идёт ролл и задержка прошла
            if (startupTicks == 0 && movementTicks > 0) {
                player.setDeltaMovement(
                        rollVelocity.x,
                        player.getDeltaMovement().y,
                        rollVelocity.z
                );
                movementTicks--;
            }

            // отсчёт i-frames
            if (invulnerable && iframeTicks > 0) {
                iframeTicks--;
                if (iframeTicks <= 0) {
                    invulnerable = false;
                }
            }

            // отсчёт анимации
            animationTicks--;
            if (animationTicks <= 0) {
                rolling = false;
                invulnerable = false;
                animationTicks = 0;
                movementTicks = 0;
                iframeTicks = 0;
                rollVelocity = Vec3.ZERO;
            }
        }

        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
    }
}
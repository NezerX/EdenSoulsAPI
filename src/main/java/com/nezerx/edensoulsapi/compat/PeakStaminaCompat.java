package com.nezerx.edensoulsapi.compat;

import com.nezerx.edensoulsapi.config.RollConfig;
import com.peakstamina.capabilities.StaminaCapability;
import com.peakstamina.config.StaminaConfig;
import com.peakstamina.handlers.ServerStaminaHandler;
import com.peakstamina.network.PacketSyncStamina;
import com.peakstamina.network.StaminaNetwork;
import com.peakstamina.registry.StaminaAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class PeakStaminaCompat {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    // Вызывается на клиенте из MinecraftClientMixin — Player, не ServerPlayer
    public static boolean hasEnoughStamina(Player player) {
        float required = RollConfig.get().roll_stamina_required;
        return player.getCapability(StaminaCapability.INSTANCE)
                .map(cap -> cap.stamina >= required)
                .orElse(true); // если capability недоступен — не блокируем
    }

    // Вызывается на сервере из RollManager — списываем стамину
    public static void onRoll(ServerPlayer player) {
        float cost = RollConfig.get().roll_stamina_cost;
        float required = RollConfig.get().roll_stamina_required;

        player.getCapability(StaminaCapability.INSTANCE).ifPresent(cap -> {

            if (cap.stamina < required) {
                return;
            }

            double usageMult = 1.0;
            AttributeInstance usageAttr = player.getAttribute(
                    (Attribute) StaminaAttributes.STAMINA_USAGE.get()
            );
            if (usageAttr != null) usageMult = usageAttr.getValue();

            float finalCost = cost * (float) usageMult;
            ServerStaminaHandler.consumeStamina(cap, finalCost);

            if (cap.stamina < 0f) cap.stamina = 0f;
            if (cap.stamina > cap.maxStamina) cap.stamina = cap.maxStamina;

            int baseDelay = (Integer) StaminaConfig.COMMON.recoveryDelay.get();
            double delayMult = 1.0;
            AttributeInstance delayAttr = player.getAttribute(
                    (Attribute) StaminaAttributes.REGEN_DELAY_MULTIPLIER.get()
            );
            if (delayAttr != null) delayMult = delayAttr.getValue();
            cap.staminaRegenDelay = (int) (baseDelay * delayMult);

            StaminaNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PacketSyncStamina(
                            cap.stamina, cap.maxStamina,
                            cap.fatiguePenalty, cap.currentHungerPenalty,
                            cap.poisonPenalty, cap.weightPenalty,
                            cap.exhaustionCooldown, cap.bonusStamina,
                            cap.penaltyValues
                    )
            );
        });
    }
}
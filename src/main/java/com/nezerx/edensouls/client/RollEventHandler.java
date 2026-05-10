package com.nezerx.edensouls.client;

import com.nezerx.edensouls.EdenSouls;
import com.nezerx.edensouls.roll.RollingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EdenSouls.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RollEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        RollingEntity rollingPlayer = (RollingEntity) player;
        if (rollingPlayer.getRollManager().isInvulnerable()) {
            event.setCanceled(true);
        }
    }
}
package com.nezerx.edensoulsapi.client;

import com.nezerx.edensoulsapi.EdenSoulsAPI;
import com.nezerx.edensoulsapi.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EdenSoulsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EdenSoulsClient {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.ROLL);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RollAnimationHandler.registerLayer();
    }
}
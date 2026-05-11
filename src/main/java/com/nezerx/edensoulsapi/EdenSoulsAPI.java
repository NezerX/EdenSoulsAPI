package com.nezerx.edensoulsapi;

import com.mojang.logging.LogUtils;
import com.nezerx.edensoulsapi.config.RollConfig;
import com.nezerx.edensoulsapi.network.NetworkHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EdenSoulsAPI.MOD_ID)
public class EdenSoulsAPI {
    public static final String MOD_ID = "edensoulsapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EdenSoulsAPI() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AutoConfig.register(RollConfig.class, GsonConfigSerializer::new);
        NetworkHandler.register();
        ModSounds.SOUNDS.register(modEventBus);
    }
}
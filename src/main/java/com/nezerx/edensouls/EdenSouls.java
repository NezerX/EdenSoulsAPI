package com.nezerx.edensouls;

import com.mojang.logging.LogUtils;
import com.nezerx.edensouls.config.RollConfig;
import com.nezerx.edensouls.network.NetworkHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EdenSouls.MOD_ID)
public class EdenSouls {
    public static final String MOD_ID = "edensouls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EdenSouls() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AutoConfig.register(RollConfig.class, GsonConfigSerializer::new);
        NetworkHandler.register();
        ModSounds.SOUNDS.register(modEventBus);
    }
}
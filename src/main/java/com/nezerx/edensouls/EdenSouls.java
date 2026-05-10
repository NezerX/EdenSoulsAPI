package com.nezerx.edensouls;

import com.mojang.logging.LogUtils;
import com.nezerx.edensouls.config.RollConfig;
import com.nezerx.edensouls.network.NetworkHandler;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.fml.common.Mod;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.slf4j.Logger;

@Mod(EdenSouls.MOD_ID)
public class EdenSouls {
    public static final String MOD_ID = "edensouls";
    public static final Logger LOGGER = LogUtils.getLogger();
    public EdenSouls() {
        AutoConfig.register(RollConfig.class, GsonConfigSerializer::new);
        NetworkHandler.register();
    }

}
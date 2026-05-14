package com.nezerx.edensoulsapi.network;

import com.nezerx.edensoulsapi.EdenSoulsAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EdenSoulsAPI.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, SyncRollTypePacket.class,
                SyncRollTypePacket::encode,
                SyncRollTypePacket::decode,
                SyncRollTypePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(1, ServerboundRollPacket.class,
                ServerboundRollPacket::encode,
                ServerboundRollPacket::decode,
                ServerboundRollPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(2, ClientboundRollAnimationPacket.class,
                ClientboundRollAnimationPacket::encode,
                ClientboundRollAnimationPacket::decode,
                ClientboundRollAnimationPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }
}
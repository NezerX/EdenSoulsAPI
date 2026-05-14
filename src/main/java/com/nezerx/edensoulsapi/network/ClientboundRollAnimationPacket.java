package com.nezerx.edensoulsapi.network;

import com.nezerx.edensoulsapi.client.RollAnimationHandler;
import com.nezerx.edensoulsapi.roll.RollType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundRollAnimationPacket {

    private final UUID playerUUID;
    private final RollType rollType;

    public ClientboundRollAnimationPacket(UUID playerUUID, RollType rollType) {
        this.playerUUID = playerUUID;
        this.rollType = rollType;
    }

    public static ClientboundRollAnimationPacket decode(FriendlyByteBuf buf) {
        return new ClientboundRollAnimationPacket(buf.readUUID(), buf.readEnum(RollType.class));
    }

    public static void encode(ClientboundRollAnimationPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerUUID);
        buf.writeEnum(packet.rollType);
    }

    public static void handle(ClientboundRollAnimationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            Player player = mc.level.getPlayerByUUID(packet.playerUUID);
            if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

            // Локальный игрок уже запустил анимацию сам — пропускаем
            if (mc.player != null && mc.player.getUUID().equals(packet.playerUUID)) return;

            RollAnimationHandler.playAnimation(clientPlayer, packet.rollType);
        });
        ctx.get().setPacketHandled(true);
    }
}
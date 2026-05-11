package com.nezerx.edensoulsapi.network;

import com.nezerx.edensoulsapi.roll.RollManager;
import com.nezerx.edensoulsapi.roll.RollType;
import com.nezerx.edensoulsapi.roll.RollingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncRollTypePacket {

    private final RollType rollType;

    public SyncRollTypePacket(RollType rollType) {
        this.rollType = rollType;
    }

    public static SyncRollTypePacket decode(FriendlyByteBuf buf) {
        return new SyncRollTypePacket(buf.readEnum(RollType.class));
    }

    public static void encode(SyncRollTypePacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.rollType);
    }

    public static void handle(SyncRollTypePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // выполняется на клиенте
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            RollManager rollManager = ((RollingEntity) mc.player).getRollManager();
            rollManager.setRollType(packet.rollType);
        });
        ctx.get().setPacketHandled(true);
    }
}
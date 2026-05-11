package com.nezerx.edensoulsapi.network;

import com.nezerx.edensoulsapi.roll.RollManager;
import com.nezerx.edensoulsapi.roll.RollingEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundRollPacket {

    private final double dirX;
    private final double dirZ;

    public ServerboundRollPacket(Vec3 direction) {
        this.dirX = direction.x;
        this.dirZ = direction.z;
    }

    public static ServerboundRollPacket decode(FriendlyByteBuf buf) {
        return new ServerboundRollPacket(new Vec3(buf.readDouble(), 0, buf.readDouble()));
    }

    public static void encode(ServerboundRollPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.dirX);
        buf.writeDouble(packet.dirZ);
    }

    public static void handle(ServerboundRollPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            RollManager rollManager = ((RollingEntity) player).getRollManager();
            if (!rollManager.isRollAvailable(player)) return;

            // Сервер запускает onRoll — выставляет rolling, invulnerable, cooldown
            Vec3 direction = new Vec3(packet.dirX, 0, packet.dirZ);
            rollManager.onRoll(player, direction);

            // Peak Stamina — списываем стамину
            if (net.minecraftforge.fml.ModList.get().isLoaded("peakstamina")) {
                try {
                    Class<?> compat = Class.forName(
                            "com.nezerx.edensoulsapi.compat.PeakStaminaCompat"
                    );
                    compat.getMethod("onRoll", ServerPlayer.class)
                            .invoke(null, player);
                } catch (Exception ignored) {}
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
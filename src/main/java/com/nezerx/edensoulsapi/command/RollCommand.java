package com.nezerx.edensoulsapi.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.nezerx.edensoulsapi.EdenSoulsAPI;
import com.nezerx.edensoulsapi.network.NetworkHandler;
import com.nezerx.edensoulsapi.network.SyncRollTypePacket;
import com.nezerx.edensoulsapi.roll.RollManager;
import com.nezerx.edensoulsapi.roll.RollType;
import com.nezerx.edensoulsapi.roll.RollingEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = EdenSoulsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RollCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("rolltype")
                        .requires(source -> source.hasPermission(2)) // только с читами
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("normal");
                                    builder.suggest("fat_roll");
                                    builder.suggest("no_roll");
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String typeArg = StringArgumentType.getString(ctx, "type");
                                    CommandSourceStack source = ctx.getSource();

                                    RollType type;
                                    try {
                                        type = RollType.valueOf(typeArg.toUpperCase());
                                    } catch (IllegalArgumentException e) {
                                        source.sendFailure(Component.literal("Неизвестный тип: " + typeArg + ". Доступны: normal, fat_roll, no_roll"));
                                        return 0;
                                    }

                                    Player player = source.getPlayer();
                                    if (player == null) {
                                        source.sendFailure(Component.literal("Только для игроков!"));
                                        return 0;
                                    }

                                    RollManager rollManager = ((RollingEntity) player).getRollManager();
                                    rollManager.setRollType(type);
                                    NetworkHandler.CHANNEL.send(
                                            PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player),
                                            new SyncRollTypePacket(type)
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
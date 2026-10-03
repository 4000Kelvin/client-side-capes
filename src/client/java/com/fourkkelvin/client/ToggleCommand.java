package com.fourkkelvin.client;


import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.io.IOException;
import java.util.Collection;

public class ToggleCommand {

    public static boolean isModEnabled = true;

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            dispatcher.register(ClientCommands.literal("customcapes")
                    .then(ClientCommands.argument("true", BoolArgumentType.bool())
                            .executes(ToggleCommand::runModToggle)));
        });
    }

    private static int runPlayerToggle(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException, IOException {

        boolean bool = BoolArgumentType.getBool(context, "true");
        String name = StringArgumentType.getString(context, "username");
        String output = "";
        if (bool) {
            output = "Enabled Custom Cape for ";

        } else {
            output = "Disabled Custom Cape for ";
        }
        context.getSource().sendFeedback(Component.literal(output.concat(name)));
        System.out.println(bool);
        PlayerDataReader.readData();
        return 1;
    }


    // Eventually add more arguments for toggling a specific player's cape

    private static int runModToggle(CommandContext<FabricClientCommandSource> context) {
        boolean bool = BoolArgumentType.getBool(context, "true");
        String output = "";
        if (bool) {
            output = "Enabled Client Side Capes";
            ToggleCommand.isModEnabled = true;

        } else {
            output = "Disabled Client Side Capes";
            ToggleCommand.isModEnabled = false;
        }
        context.getSource().sendFeedback(Component.literal(output));
        System.out.println(bool);
        //PlayerDataReader.readData();

        return 1;
    }
}

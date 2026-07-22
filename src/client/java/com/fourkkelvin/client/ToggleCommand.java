package com.fourkkelvin.client;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class ToggleCommand {

    public static boolean isModEnabled = true;

    public static void register() {

        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            dispatcher.register(ClientCommands.literal("customcapes")

                    .then(ClientCommands.argument("true", BoolArgumentType.bool()).executes(ToggleCommand::runToggleCommand)));

                    //.then(ClientCommands.argument("username", StringArgumentType.string()).executes(ToggleCommand::runToggleCommand)));
        });
    }

    // Eventually add more arguments for toggling a specific player's cape

    private static int runToggleCommand(CommandContext<FabricClientCommandSource> context) {
        boolean bool = BoolArgumentType.getBool(context, "true");
        //String username = StringArgumentType.getString(context, "username");
        String output = "";
        if (bool) {
            output = "Enabled Client Side Cape Rendering";
            ToggleCommand.isModEnabled = true;

        } else {
            output = "Disabled Client Side Cape Rendering";
            ToggleCommand.isModEnabled = false;
        }
        context.getSource().sendFeedback(Component.literal(output));
        System.out.println(bool);
        //System.out.println(username);
        return 1;
    }
}

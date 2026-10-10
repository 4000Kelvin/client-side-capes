package com.fourkkelvin.client;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ToggleCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            // Make playername suggestion list
            var playerSuggestions = (com.mojang.brigadier.suggestion.SuggestionProvider<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource>) (context, builder) -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.getConnection() != null) {
                    ClientPacketListener connection = minecraft.getConnection();
                    for (PlayerInfo playerInfo : connection.getListedOnlinePlayers()) {
                        String playerUsername = playerInfo.getProfile().name();
                        if (playerUsername.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                            builder.suggest(playerUsername);
                        }
                    }
                }
                return builder.buildFuture();
            };
            dispatcher.register(ClientCommands.literal("customcapes")
                    // ENABLE BRANCH
                    .then(ClientCommands.literal("enable")
                            .executes(context -> {
                                FabricClientCommandSource source = context.getSource();
                                try {
                                    ToggleCommand.runModToggle(true, source, null);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return 1;
                            })
                            // Execute only if playername given
                            .then(ClientCommands.argument("playerName", StringArgumentType.string())
                                    .suggests(playerSuggestions)
                                    .executes(context -> {
                                        FabricClientCommandSource source = context.getSource();
                                        String targetPlayer = StringArgumentType.getString(context, "playerName");
                                        try {
                                            ToggleCommand.runModToggle(true, source, targetPlayer);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return 1;
                                    })
                            )
                    )
                    // DISABLE BRANCH
                    .then(ClientCommands.literal("disable")
                            .executes(context -> {
                                FabricClientCommandSource source = context.getSource();
                                try {
                                    ToggleCommand.runModToggle(false, source, null);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return 1;
                            })
                            // Execute only if playername given
                            .then(ClientCommands.argument("playerName", StringArgumentType.string())
                                    .suggests(playerSuggestions)
                                    .executes(context -> {
                                        FabricClientCommandSource source = context.getSource();
                                        String targetPlayer = StringArgumentType.getString(context, "playerName");
                                        try {
                                            ToggleCommand.runModToggle(false, source, targetPlayer);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }

    private static void runModToggle(boolean enabled, FabricClientCommandSource source, String targetPlayer) throws IOException, CommandSyntaxException {
        String output = "";
        MutableComponent outPrefix = Component.literal("[").append(Component.literal("ClientSideCapes").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal("]: ").withStyle(ChatFormatting.WHITE)));

        ModData currentData = ModDataReader.getCurrentData();

        if (targetPlayer != null) {
            // Grab the uuid of the target player currently online (a little redundant but...whatever)
            UUID targetUuid = null;
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getConnection() != null) {
                ClientPacketListener connection = minecraft.getConnection();
                targetUuid = Objects.requireNonNull(connection.getPlayerInfo(targetPlayer)).getProfile().id();
            }

            ArrayList<ArrayList<Object>> playerDataList = currentData.getPlayerData();

            // See if targetUuid is found in config
            if (targetUuid != null) {
                for (int row = 0; row < playerDataList.size(); ++row) {
                    ArrayList<Object> currentRow = playerDataList.get(row);
                    if (targetUuid.toString().equals(currentRow.getFirst().toString())) {
                        boolean playerEnabled = (boolean) playerDataList.get(row).get(1);
                        // Found targetUuid in config, so now we do our toggle logic
                        if (enabled) {
                            if (playerEnabled) {
                                output = "Custom cape for " + targetPlayer + " already enabled";
                            } else {
                                output = "Custom cape for " + targetPlayer + " enabled";
                                playerDataList.get(row).set(1, true);
                                currentData.setPlayerData(playerDataList);
                                currentData.rebuildCache();
                                ModDataReader.save();
                            }
                        } else {
                            if (playerEnabled) {
                                output = "Custom cape for " + targetPlayer + " disabled";
                                playerDataList.get(row).set(1, false);
                                currentData.setPlayerData(playerDataList);
                                currentData.rebuildCache();
                                ModDataReader.save();
                            } else {
                                output = "Custom cape for " + targetPlayer + " already disabled";
                            }
                        }

                        source.sendFeedback(outPrefix.append(Component.literal(output)));
                        // Send an additional message specifically if player cape is enabled but mod-wide toggle is disabled to clear up confusion
                        if (!currentData.isModEnabled() && playerEnabled) {
                            MutableComponent specialPlayerToggleMsg = Component.literal("")
                                    .append(Component.literal("You've enabled a custom player cape, but disabled Client Side Capes completely! To enable it, run the command: ").withStyle(ChatFormatting.DARK_GREEN)
                                    .append(Component.literal("/customcapes enable").withStyle(ChatFormatting.DARK_AQUA)));

                            source.sendFeedback(specialPlayerToggleMsg);
                        }
                        break;
                    }
                }
            }

        } else {
            // Mod-wide Toggling
            if (enabled) {
                if (currentData.isModEnabled()) {
                    output = "Client Side Capes already enabled";
                } else {
                    output = "Enabled Client Side Capes";
                    currentData.setModEnabled(true);
                    currentData.rebuildCache();
                    ModDataReader.save();
                }
            } else {
                if (currentData.isModEnabled()) {
                    output = "Disabled Client Side Capes";
                    currentData.setModEnabled(false);
                    currentData.rebuildCache();
                    ModDataReader.save();
                } else {
                    output = "Client Side Capes already disabled";
                }
            }
            source.sendFeedback(outPrefix.append(Component.literal(output)));
        }
    }
}

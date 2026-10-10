package com.fourkkelvin.client;

import com.fasterxml.jackson.databind.node.ArrayNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class ClientSideCapesClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			client.execute(() -> {

				// Load File Paths
				Path configPath = FabricLoader.getInstance().getConfigDir().resolve( "client-side-capes.json");
				File capesFolder = new File(Minecraft.getInstance().gameDirectory, "custom_capes");

				// Ensure cape folder exists
				if (!capesFolder.exists()) {
					capesFolder.mkdirs();
				}

				// If files in folder, we are going to ensure that config is updated
				File[] files = capesFolder.listFiles();
				if (files != null && files.length > 0) {
					System.out.println("[Client-Side-Capes] Files found in custom cape directory");

					// Load config first and get list of uuids currently stored
                    try {
                        ModDataReader.loadExistingData();
                    } catch (IOException e) {
						throw new RuntimeException(e);
					}
					ModData currentData = ModDataReader.getCurrentData();
					ArrayList<ArrayList<Object>> playerDataList = currentData.getPlayerData();
					ArrayList<Object> configUuidList = new ArrayList<>();
					for (int row = 0; row < playerDataList.size(); ++row) {
						ArrayList<Object> currentRow = playerDataList.get(row);
						configUuidList.add(currentRow.getFirst());
					}

                    for (File file : files) {
						// Read uuid for each file
						if (file.isFile() && file.getName().endsWith(".png")) {
							String fileName = file.getName().replace(".png", "");
							try {
								UUID playerUuid = UUID.fromString(fileName);
								// If uuid not found in configUuidList, append an entry to playerDataList
								// Note that, right now, we are not removing potentially old UUIDs saved in the config for which we no longer have capes stored.
								// Currently, this is not an issue for the mod or to me personally, but just note that the config will keep endlessly appending UUIDs that we may no longer care about.
								if (!configUuidList.contains(playerUuid.toString())) {
									ArrayList<Object> newRow = new ArrayList<>();
									newRow.add(playerUuid);
									newRow.add(true); // Default new entry to true
									playerDataList.add(newRow);
								}

							} catch (IllegalArgumentException e) {
								System.out.println("[Client-Side-Capes] Skipped non-UUID string file format: " + file.getName());
							}
						}
						ModDataReader.save();
					}
				} else {
					// If no files, we will re/set config to empty JSON
					System.out.println("[Client-Side-Capes] No files found in custom cape directory");
					try {
						ModDataReader.loadDefaultData(configPath);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				};

				System.out.println("[Client-Side-Capes] World loaded. Initiating custom cape asset stream...");
				assert client.player != null;
				System.out.println(client.player.getStringUUID());

				// Final stuff to do
                CustomCapeRegistry.scanAndRegisterCapes();
				ToggleCommand.register();
            });

		});

	}

}
package com.fourkkelvin.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;


public class ClientSideCapesClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		//AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

		// Don't understand the details of this, but we can't just call on CustomCapeRegistry because on the client initialization, the cape files are not properly cached prior to loading a world.
		// These extra lines ensure that we get them when we join a world
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			client.execute(() -> {
				System.out.println("[ClientSideCapes] World loaded. Initiating custom cape asset stream...");
				assert client.player != null;
				System.out.println(client.player.getStringUUID());
				CustomCapeRegistry.scanAndRegisterCapes();
				ToggleCommand.register();
			});

		});

	}

}

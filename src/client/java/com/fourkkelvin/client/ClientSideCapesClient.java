package com.fourkkelvin.client;

import com.fourkkelvin.client.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class ClientSideCapesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

	}

}

package com.fourkkelvin.client.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.network.chat.Component;

public class ModMenuImplementation implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Returns a lambda factory linking your parent screen to the custom screen
        return parentScreen -> new CustomCapeConfigurationScreen((Component) parentScreen);
    }
}


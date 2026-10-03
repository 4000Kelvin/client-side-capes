package com.fourkkelvin.client.gui_and_config.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "client-side-capes")
public class ModConfig implements ConfigData {

    //@ConfigEntry.Gui.CollapsibleObject
    public List<InnerStuff> userCategories = new ArrayList<>();

    static class InnerStuff {
        public boolean enablecape = true;
        public String name = "placeholder_name";
        public String uuid = "placeholder_uuid";
        public String file = "placeholder.png";

    }
}

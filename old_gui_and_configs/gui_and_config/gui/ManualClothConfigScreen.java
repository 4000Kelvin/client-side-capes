package com.fourkkelvin.client.gui_and_config.gui;

import com.fourkkelvin.client.gui_and_config.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ManualClothConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Custom Cape Configuration"));
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("Category 1"));

        for (int i = 0; i < 5; i++) {
            SubCategoryBuilder subCategory = MakePlayerEntry(builder, Integer.toString(i));
            general.addEntry(subCategory.build());
        }

        SubCategoryBuilder subCategory = MakePlayerEntry(builder, "New");
        general.addEntry(subCategory.build());

        return builder.build();
    }

    public static SubCategoryBuilder MakePlayerEntry(ConfigBuilder builder, String i) {
        String toggleBtnLabel = "Enable Custom Cape";
        String playerNameFieldLabel = "Player Name (Optional)";
        String playerNameToolTip = "Player Name (Optional)";
        String uuidLabel = "Player UUID";
        String uuidToolTip = "Player UUID (Required)";
        String capeFileLabel = "Custom Cape File";

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            final String[] currentValue = {"testvalue"};
            List<String> dropdownlist = new ArrayList<>();
            // Add elements
            dropdownlist.add("Alice");
            dropdownlist.add("Bob");

            String playerCategory = "Player " + i;
            String playerNameDefault = "Player " + i;


            SubCategoryBuilder subCategory = entryBuilder.startSubCategory(
                    Component.literal(playerCategory));

            subCategory.add(entryBuilder.startBooleanToggle(
                    Component.literal(toggleBtnLabel), true).build());

            subCategory.add(entryBuilder.startStrField(Component.literal(playerNameFieldLabel), currentValue[0])
                    .setTooltip(Component.literal(playerNameToolTip)) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> currentValue[0] = newValue)
                    .setDefaultValue(playerNameDefault) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            subCategory.add(entryBuilder.startStrField(Component.literal(uuidLabel), currentValue[0])
                    .setTooltip(Component.literal(uuidToolTip)) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> currentValue[0] = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            subCategory.add(entryBuilder.startDropdownMenu(
                            Component.literal(capeFileLabel), // Text title
                            currentValue[0], // Currently selected file string
                            fileString -> fileString, // Mapper: Object -> String (in this case, it's already a string)
                            fileString -> Component.literal(fileString)  // Mapper: String -> Object (or validation if needed)
                    )
                    .setDefaultValue("") // Recommended for reset buttons
                    .setSelections(dropdownlist) // Pass your list of filename strings here
                    .setSaveConsumer(newValue -> currentValue[0] = (String) newValue) // Save to your config object
                    .build());

            return subCategory;
        }
}

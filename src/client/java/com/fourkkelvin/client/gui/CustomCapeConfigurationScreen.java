package com.fourkkelvin.client.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.core.jackson.Log4jXmlObjectMapper;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// #region screen
public class CustomCapeConfigurationScreen extends Screen {
    // #endregion screen
    // #region return_to_previous_screen
    public Screen parent;
    private EditBox searchBox;
    private int searchBoxWidth;
    private int searchBoxX;
    private int searchBoxY;
    private final Path dataFilePath = FabricLoader.getInstance().getConfigDir().resolve("clientsidecapes_data.json");
    public CustomCapeConfigurationScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    // #endregion return_to_previous_screen
    // #region screen
    public CustomCapeConfigurationScreen(Component title) {
        super(title);
    }

    public void buildScrollableListWidgets() {

    }

    public void addInputField(String defaultValue) {
        this.searchBox = new EditBox(this.font,
                this.searchBoxX, this.searchBoxY,
                this.searchBoxWidth,
                20,
                this.searchBox,
                Component.literal("Enter Player UUID Here") // This is just something for the Narrator lol
        );
        if (defaultValue.isEmpty()) {
            this.searchBox.setValue("");
            this.searchBox.setHint(Component.literal("Enter Player UUID Here"));
        } else {
            this.searchBox.setValue(defaultValue);
        }
        this.addRenderableWidget(this.searchBox);
        this.searchBoxY += 25;
    }


    //String value = rootNode.get(key).toString();
    //value = value.replaceAll("^\"|\"$", ""); // Just removing quotes from string. I don't understand REGEX lol Source - https://stackoverflow.com/a/2608682 / Posted by BalusC, modified by community. See post 'Timeline' for change history / Retrieved 2026-06-13, License - CC BY-SA 2.5




    private String loadJsonData() throws IOException {
        return new String(Files.readAllBytes(Paths.get(this.dataFilePath.toString())));
    }

    private boolean checkForCapeData() throws IOException {
        System.out.println(this.dataFilePath);
        if (Files.exists(this.dataFilePath)) {
            String jsonstring = this.loadJsonData();
            return !jsonstring.equals("{}");
        } else {
            Files.writeString(this.dataFilePath, "{}");
            return false;
        }

    }

    @Override
    protected void init() {
        // Set starting widget placements
        this.searchBoxX =  40;
        this.searchBoxWidth = 250;
        this.searchBoxY = 40 + 25;

        try {
            if (this.checkForCapeData()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(this.loadJsonData());
                Iterator<String> keys = rootNode.fieldNames();

                while (keys.hasNext()) {
                    String key = keys.next();
                    this.addInputField(key);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Button capeFileDropdown = Button.builder(Component.literal("Custom Cape"), (button) -> this.minecraft.setScreen(new CustomCapeConfigurationScreen(title, this))).bounds(40, 40 + 25, 150, 20).build();

        Button addCapeBtn = Button.builder(Component.literal("Add Custom Cape"), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.
            this.addInputField("");
        }).bounds(40, 40, 120, 20).build();
        // x, y, width, height
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button
        // textures.

        // Register the button widget.
        this.addRenderableWidget(addCapeBtn);

    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // font, text, x, y, color, hasShadow
        graphics.text(this.font, "Custom Cape Configuration", 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}
// #endregion screen

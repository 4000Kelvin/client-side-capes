package com.fourkkelvin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.Files.readAllLines;

public class ModDataReader {


    private static final ObjectMapper mapper = new ObjectMapper();
    private static ModData currentData = new ModData();

    public static void loadDefaultData(Path jsonPath) throws IOException {
        // If file doesn't exist, or we want to reset, save default config
        if (Files.notExists(jsonPath)) {
            Files.createDirectories(jsonPath.getParent());
        }
            currentData.setModEnabled(true);
            ArrayList<ArrayList<Object>> emptyPlayerData = new ArrayList<>();
            currentData.setPlayerData(emptyPlayerData);
            save();
            System.out.println("[Client-Side-Capes] Generated default Client-Side-Capes configuration file.");
        // Now load config data
        currentData.rebuildCache(); // <-- Add this
        return;
    }

    public static void loadExistingData() throws IOException {
        Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve( "client-side-capes.json");
        try (BufferedReader reader = Files.newBufferedReader(jsonPath)) {
            currentData = mapper.readValue(reader, ModData.class);
        }
        currentData.rebuildCache(); // <-- Add this
    }

    public static ModData getCurrentData() {
        return currentData;
    }

    public static void save() {
        try {
            // Converts object to string and writes it to disk
            Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve( "client-side-capes.json");
            try (BufferedWriter writer = Files.newBufferedWriter(jsonPath)) {
                mapper.writeValue(writer, currentData);
            }
        } catch (IOException e) {
            System.err.println("[Client-Side-Capes] Critical Error: Failed to save config to disk! " + e.getMessage());
        }
    }

}

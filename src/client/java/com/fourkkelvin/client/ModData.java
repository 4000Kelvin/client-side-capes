package com.fourkkelvin.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

// Define root class
public class ModData {
    private boolean ModEnabled = true;
    private ArrayList<ArrayList<Object>> playerData = new ArrayList<>();

    // The keyword 'volatile' guarantees the Render thread instantly sees changes made by the main thread
    private transient volatile HashSet<UUID> disabledUuidCache = new HashSet<>();

    // Define getters and setters for root class
    public boolean isModEnabled() {
        return ModEnabled;
    }

    public ArrayList<ArrayList<Object>> getPlayerData() {
        return playerData;
    }

    public void setModEnabled(boolean isModEnabled) {
        this.ModEnabled = isModEnabled;
    }
    public void setPlayerData(ArrayList<ArrayList<Object>> playerData) {
        this.playerData = playerData;
        rebuildCache(); // Rebuild whenever data is set or loaded
    }

    public boolean isPlayerDisabled(UUID uuid) {
        return disabledUuidCache.contains(uuid);
    }


    // AI: Hard for me to grasp caching and updating textures in multiple threads. well, I got some of the way there without copy/pasting...whatever
    public void rebuildCache() {
        HashSet<UUID> newCache = new HashSet<>();
        if (playerData != null) {
            for (ArrayList<Object> row : playerData) {
                if (row.size() >= 2 && Boolean.FALSE.equals(row.getLast())) {
                    try {
                        // Safely parse whatever format the UUID is stored as (String or UUID)
                        Object firstElement = row.getFirst();
                        UUID uuid = (firstElement instanceof UUID) ? (UUID) firstElement : UUID.fromString(firstElement.toString());
                        newCache.add(uuid);
                    } catch (IllegalArgumentException e) {
                        System.err.println("[Client-Side-Capes] Invalid UUID string in config format: " + row.getFirst());
                    }
                }
            }
        }
        this.disabledUuidCache = newCache;
    }
}


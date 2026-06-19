package com.fourkkelvin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.core.ClientAsset;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomCapeRegistry {

    public static final Map<UUID, ClientAsset.ResourceTexture> UUID_CAPE_MAP = new HashMap<>();

    public static void scanAndRegisterCapes() {
        try {
            File gameDir = Minecraft.getInstance().gameDirectory;
            File capesFolder = new File(gameDir, "custom_capes");

            if (!capesFolder.exists()) {
                capesFolder.mkdirs();
            }

            File[] files = capesFolder.listFiles();
            if (files == null) return;

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".png")) {
                    String fileName = file.getName().replace(".png", "");

                    try {
                        // This is where we get into territory that I really don't understand at this time. But Google AI says we apparently need to do all this in order to properly find and load the cape file into memory for a given UUID
                        // I've dealt with learning how to convert various image formats between "in storage" vs "in use/memory" formats in another project, and it was a pain in the ass.
                        // So I'm letting AI help here. In short, we are converting to image formats that java/minecraft can understand at runtime??

                        UUID playerUuid = UUID.fromString(fileName);

                        // 1. Establish a clean rendering cache tracking lookup key
                        Identifier capeId = Identifier.fromNamespaceAndPath("client-side-capes", "external_cape_" + fileName);

                        // 2. Stream physical disk data directly into internal memory arrays
                        try (FileInputStream stream = new FileInputStream(file)) {
                            NativeImage nativeImage = NativeImage.read(stream);

                            // Adjust constructor variables based on your mapping's Supplier criteria if needed
                            DynamicTexture texture = new DynamicTexture(() -> "external_cape_" + fileName, nativeImage);

                            // 3. Force-register the bitmap asset directly into active texture buffer memory
                            Minecraft.getInstance().getTextureManager().register(capeId, texture);
                        }

                        ClientAsset.ResourceTexture standardRecordAsset = new ClientAsset.ResourceTexture(capeId, capeId);

                        // Cache the record into our active map directory index
                        UUID_CAPE_MAP.put(playerUuid, standardRecordAsset);
                        System.out.println("[Mod] Successfully loaded dynamic cape record for UUID: " + playerUuid);

                    } catch (IllegalArgumentException e) {
                        System.out.println("[Mod] Skipped non-UUID string file format: " + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


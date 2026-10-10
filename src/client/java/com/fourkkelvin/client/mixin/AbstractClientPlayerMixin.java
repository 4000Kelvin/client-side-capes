package com.fourkkelvin.client.mixin;

import com.fourkkelvin.client.CustomCapeRegistry;
import com.fourkkelvin.client.ModData;
import com.fourkkelvin.client.ModDataReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    // Okay, so prior to injecting the cape file into our skin, minecraft wants us to set up both a "lookup key" and a "variable storing the path" and map them together
    // so the game knows where/what to reference for accessing a given file. Makes enough sense...

    @Shadow
    @Final
    private ClientAvatarState clientAvatarState;
    // lookup key
    private static final Identifier CAPE_ID =
            Identifier.fromNamespaceAndPath("client-side-capes", "custom_cape");

    // actual path to file
    private static final Identifier CAPE_PATH =
            Identifier.fromNamespaceAndPath("client-side-capes", "custom_capes/custom_cape.png");

    // Combine
    private static final ClientAsset.ResourceTexture CUSTOM_CAPE_ASSET =
            new ClientAsset.ResourceTexture(CAPE_ID, CAPE_PATH);

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void injectCustomCape(CallbackInfoReturnable<PlayerSkin> cir) throws IOException, CommandSyntaxException {
        PlayerSkin originalTextures = cir.getReturnValue();

        if (originalTextures == null) return;

        // Interesting lesson learned here. The Cape and player rendering is (of course) a constant process occurring (looping??) all the time in game.
        // So all we need is a simple conditional to our isModEnabled boolean to toggle the mod on/off such that, on the next available tick?, we update the player models. Neat, we don't even need to connect our command logic.
        ModData currentData = ModDataReader.getCurrentData();
        if (!currentData.isModEnabled()) {
            return;
        }

        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        UUID playerUuid = player.getUUID();

        if (currentData.isPlayerDisabled(playerUuid)) {
            return;
        }

        // This is where we are getting our cape data for a given UUID
        ClientAsset.ResourceTexture matchedCape = CustomCapeRegistry.UUID_CAPE_MAP.get(playerUuid);

        // Overlay the cape leave all other skin elements unchanged
        if (matchedCape != null) {
                PlayerSkin.Patch capePatch = new PlayerSkin.Patch(
                        Optional.empty(),                    // Body skin layer
                        Optional.of(matchedCape),            // cape goes here
                        Optional.empty(),                    // Elytra
                        Optional.empty()                     // Player size
                );

                cir.setReturnValue(originalTextures.with(capePatch));

        }
    }
}
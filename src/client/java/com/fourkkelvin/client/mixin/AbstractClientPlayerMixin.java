package com.fourkkelvin.client.mixin;

import com.fourkkelvin.client.CustomCapeRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.resources.Identifier;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    // Okay, so prior to injecting the cape file into our skin, minecraft wants us to set up both a "lookup key" and a "variable storing the path" and map them together
    // so the game knows where/what to reference for accessing a given file. Makes enough sense...

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
    private void injectCustomCape(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerSkin originalTextures = cir.getReturnValue();

        if (originalTextures == null) return;
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        UUID playerUuid = player.getUUID();

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
package com.fourkkelvin.client.mixin;

import com.fourkkelvin.client.gui.CustomCapeClothConfigScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SkinCustomizationScreen.class)
public abstract class CustomCapeButtonMixin extends OptionsSubScreen {

    public CustomCapeButtonMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(at = @At("TAIL"), method = "addOptions")
    protected void addOptions(CallbackInfo ci) {
        List<AbstractWidget> widgets = new ArrayList<>();

        Screen configScreen = CustomCapeClothConfigScreen.create(this);

        widgets.add(Button.builder(Component.literal("Custom Cape"), (button) -> this.minecraft.setScreen(configScreen)).bounds(this.width / 2 - 100, 150, 150, 20).build());
        assert this.list != null;
        this.list.addSmall(widgets);
    }

}

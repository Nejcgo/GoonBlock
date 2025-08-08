package com.github.nejcgo.goonblock.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

// We implement LayerRenderer for the AbstractClientPlayer class
public class LayerCustomSkin implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;
    private final ResourceLocation newSkin = new ResourceLocation("goonblock:textures/entity/peter_griffin1.png");

    public LayerCustomSkin(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        // Check our main class to see if this player's skin should be changed.
        if (NPCSkinChanger.isToggled(player.getUniqueID())) {

            // Bind our custom texture
            Minecraft.getMinecraft().getTextureManager().bindTexture(newSkin);

            // Get the main player model from the renderer
            // and render it with our texture bound.
            this.playerRenderer.getMainModel().render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        // We are using a completely separate texture, so this should be false.
        return false;
    }
}
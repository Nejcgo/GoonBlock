package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.util.BloodRushManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui; // Make sure this is imported
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class LobotomyRenderer extends Gui { // Ensure you are extending Gui

    private final Minecraft mc;
    private final BloodRushManager manager;

    public LobotomyRenderer(BloodRushManager manager) {
        this.mc = Minecraft.getMinecraft();
        this.manager = manager;
        // MinecraftForge.EVENT_BUS.register(this); // Only if tick handler is in this class AND not already registered
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (mc.theWorld != null) {
                manager.updateLobotomyEffectFade();
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL &&
                event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE &&
                event.type != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (manager.isLobotomyEffectActive()) {
            ResourceLocation imageToDraw = manager.getCurrentLobotomyImage();
            if (imageToDraw == null) {
                return;
            }

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            float alpha = manager.getLobotomyEffectAlpha();

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha); // Apply alpha for fading

            mc.getTextureManager().bindTexture(imageToDraw);

            // --- MODIFIED DRAWING CODE FOR STRETCHING ---
            // x, y (screen position to draw at - top left)
            // u, v (texture position to start sampling from - top left of image)
            // uWidth, vHeight (width and height of the *portion of the texture* to draw)
            //    To use the whole image, these should be the image's actual dimensions.
            //    If you don't know the image's actual dimensions here, using screenWidth/Height
            //    effectively tells it to map the [0,1] UV range to the screen.
            // targetWidth, targetHeight (how big to draw it on screen)
            // textureSheetWidth, textureSheetHeight (the full dimensions of the entire texture file being bound)
            //    Again, if unknown, using screenWidth/Height works for the stretch effect.

            Gui.drawScaledCustomSizeModalRect(
                    0,                // screenX
                    0,                // screenY
                    0,                // textureU (start at top-left of image)
                    0,                // textureV (start at top-left of image)
                    screenWidth,      // uWidth (take a portion of texture of this size) - effectively using screen coords for UV scale
                    screenHeight,     // vHeight (take a portion of texture of this size) - effectively using screen coords for UV scale
                    screenWidth,      // target width on screen
                    screenHeight,     // target height on screen
                    screenWidth,      // assumed full texture width (for UV scaling)
                    screenHeight      // assumed full texture height (for UV scaling)
            );
            // This configuration of drawScaledCustomSizeModalRect will stretch
            // the entire bound texture (from UV 0,0 to 1,1) to cover the
            // specified screen area (0,0 to screenWidth, screenHeight).

            // Reset color tint (especially alpha)
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
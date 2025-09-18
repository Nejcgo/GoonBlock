package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.GoonBlock;
import com.github.nejcgo.goonblock.util.BloodRushManager;
import com.github.nejcgo.goonblock.util.RoastProfile; // Import RoastProfile
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import java.util.List;
import java.util.ArrayList;

public class WaifuOverlayRenderer extends Gui {

    private final Minecraft mc;
    private final BloodRushManager manager;

    public WaifuOverlayRenderer(BloodRushManager manager) {
        this.mc = Minecraft.getMinecraft();
        this.manager = manager;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT && event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (manager.shouldShowWaifu()) {
            RoastProfile roastProfile = manager.getCurrentRoastProfile();
            if (roastProfile == null) return;

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            ResourceLocation currentExpressionTexture = new ResourceLocation(
                    GoonBlock.MODID,
                    "textures/gui/" + manager.getCurrentWaifuName() + "/" + roastProfile.getExpressionKey() + ".png"
            );

            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            mc.getTextureManager().bindTexture(currentExpressionTexture);

            int waifuWidth = 128;
            int waifuHeight = 256;
            int waifuX = screenWidth - waifuWidth - 20;
            int waifuY = screenHeight / 2 - waifuHeight / 2;

            Gui.drawScaledCustomSizeModalRect(waifuX, waifuY, 0, 0, waifuWidth, waifuHeight, waifuWidth, waifuHeight, waifuWidth, waifuHeight);

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();

            String roastText = roastProfile.getText();
            if (roastText != null && !roastText.isEmpty()) {
                int textMaxWidth = screenWidth / 3;
                int textX = waifuX - 10;

                List<String> lines = new ArrayList<>();
                String[] words = roastText.split(" ");
                StringBuilder currentLine = new StringBuilder();
                for (String word : words) {
                    if (mc.fontRendererObj.getStringWidth(currentLine.toString() + word + " ") > textMaxWidth && currentLine.length() > 0) {
                        lines.add(currentLine.toString().trim());
                        currentLine = new StringBuilder();
                    }
                    currentLine.append(word).append(" ");
                }
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString().trim());
                }

                int totalTextHeight = lines.size() * (mc.fontRendererObj.FONT_HEIGHT + 2);
                int textY = waifuY + (waifuHeight / 2) - (totalTextHeight / 2);


                // Speech bubble effect
                if (!lines.isEmpty()) {
                    int widestLine = 0;
                    for (String line : lines) {
                        widestLine = Math.max(widestLine, mc.fontRendererObj.getStringWidth(line));
                    }
                    int padding = 5;
                    int bubbleX = textX - widestLine - padding; // Adjusted for right-alignment
                    int bubbleY = textY - padding;
                    int bubbleWidth = widestLine + (padding * 2);
                    int bubbleHeight = totalTextHeight + (padding * 2) - (lines.size() > 1 ? 2 * (lines.size()-1) : 0) ;

                    drawRect(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 0xCC000000);
                }


                int currentY = textY;
                for (String line : lines) {
                    int lineWidth = mc.fontRendererObj.getStringWidth(line);
                    mc.fontRendererObj.drawStringWithShadow(line, textX - lineWidth, currentY, 0xFFFFFF);
                    currentY += mc.fontRendererObj.FONT_HEIGHT + 2;
                }
            }
        }
    }
}
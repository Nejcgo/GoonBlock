package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.classes.Script;
import com.github.nejcgo.goonblock.classes.ScriptPage;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import com.github.nejcgo.goonblock.util.VisualNovelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Random;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;


public class ScriptRenderer {
    private Script script;
    private int currentPageIndex;
    private ResourceLocation sprite;

    private long startTime;
    private int phase; // 0 - coming up,  1 - script

    private final double transitionLength = 2000;
    private long currentTransitionTime;

    private final ResourceLocation textBox;

    private final int textBoxWidth = 358;
    private final int textBoxHeight = 96;

    private String fullTextForPage;
    private String visibleText;
    private int characterIndex;
    private int tickCounter;
    private final int TICKS_PER_CHARACTER = 2; // Lower is faster, higher is slower
    private boolean isPageFinished;

    Random random = new Random();

    // The sound to play when a character appears
    private final String soundEffect = "random.orb";

    private final Minecraft mc;

    public ScriptRenderer(Script script){
        this.script = script;
        this.currentPageIndex = 0;
        this.visibleText = "";
        this.startTime = System.currentTimeMillis();
        this.currentTransitionTime = 0;
        this.phase = 0;

        ScriptPage currentPage = script.getPages().get(currentPageIndex);
        this.fullTextForPage = currentPage.getText();

        this.mc = Minecraft.getMinecraft();

        this.textBox = new ResourceLocation("goonblock", "textures/gui/visual/textBox.png");
        this.sprite = new ResourceLocation("goonblock", "textures/gui/visual/" + currentPage.getSprite());
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();

        if(phase == 0) {
            mc.getTextureManager().bindTexture(textBox);

            drawScaledCustomSizeModalRect(
                    (screenWidth - textBoxWidth) / 2,
                    (screenHeight / 2) + 280 - (int)(GoonblockFunctions.easeOutBack((System.currentTimeMillis() - startTime)/transitionLength) * 200),
                    0,
                    0,
                    textBoxWidth,
                    textBoxHeight,
                    textBoxWidth,
                    textBoxHeight,
                    textBoxWidth,
                    textBoxHeight
            );
        } else {
            mc.getTextureManager().bindTexture(sprite);

            drawScaledCustomSizeModalRect(
                    (screenWidth) / 2 - 63,
                    (screenHeight / 2) - 40,
                    0,
                    0,
                    126,
                    221,
                    126,
                    221,
                    126,
                    221
            );

            mc.getTextureManager().bindTexture(textBox);
            drawScaledCustomSizeModalRect(
                    (screenWidth - textBoxWidth) / 2,
                    (screenHeight / 2) + 80,
                    0,
                    0,
                    textBoxWidth,
                    textBoxHeight,
                    textBoxWidth,
                    textBoxHeight,
                    textBoxWidth,
                    textBoxHeight
            );

            FontRenderer font = mc.fontRendererObj;
            if (visibleText != null) {
                drawStringWithLineBreaks(visibleText, ((float) (screenWidth - textBoxWidth) / 2) + 48, ((float) screenHeight / 2) + 100, 0xFFFFFF, font, 264); // Example coordinates and color
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Only run on the end of a tick to avoid running twice
        if (event.phase != TickEvent.Phase.END) return;

        if(phase == 1) {
            // If the current page is already fully displayed, do nothing.
            if (isPageFinished) return;

            tickCounter++;
            if (tickCounter >= TICKS_PER_CHARACTER) {
                tickCounter = 0; // Reset the timer

                // Add the next character from the full text to the visible text
                characterIndex++;
                visibleText = fullTextForPage.substring(0, characterIndex);

                // Play the typing sound (but not for spaces)
                try {
                    if (characterIndex < fullTextForPage.length() && fullTextForPage.charAt(characterIndex - 1) != ' ') {
                        mc.thePlayer.playSound(soundEffect, 0.5F, 1F + random.nextFloat()/2);
                    }
                } catch(Exception e) {
                    finish();
                }

                // Check if we have now revealed the entire string
                if (characterIndex >= fullTextForPage.length()) {
                    isPageFinished = true;
                }
            }
        }

        if(phase == 0 && System.currentTimeMillis() - startTime > transitionLength) {
            phase = 1;
        }
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        if (event.button == 0 && event.buttonstate) {
            if (isPageFinished) {
                // If the page is finished, a click advances to the next page
                advanceToNextPage();
            } else {
                // If the page is still typing, a click instantly finishes it
                finishCurrentPage();
            }
        }
    }

    public void advanceToNextPage(){
        currentPageIndex++;

        // Check if the script is over
        if (currentPageIndex >= script.getPages().size()) {
            finish(); // The script is done, clean up.
            return;
        }

        // --- RESET THE STATE FOR THE NEW PAGE ---
        ScriptPage currentPage = script.getPages().get(currentPageIndex);
        this.fullTextForPage = currentPage.getText();
        this.visibleText = "";
        this.characterIndex = 0;
        this.tickCounter = 0;
        this.isPageFinished = false;
        this.sprite = new ResourceLocation("goonblock", "textures/gui/visual/" + currentPage.getSprite());
    }

    private void finishCurrentPage() {
        // Instantly reveal the full text and set the flag
        this.isPageFinished = true;
        this.visibleText = this.fullTextForPage;
        this.characterIndex = this.fullTextForPage.length();
    }

    private void drawStringWithLineBreaks(String string, float x, float y, int colour, FontRenderer font, int width) {
        List<String> lines = font.listFormattedStringToWidth(string, width);

        for(String line : lines) {
            font.drawStringWithShadow(line, x, y, colour);

            y += font.FONT_HEIGHT;
        }
    }

    /** Call this method to finish the script renderer and clean up.
     *
     */
    public void finish() {
        MinecraftForge.EVENT_BUS.unregister(this);

        if (VisualNovelManager.currentRenderer == this) {
            VisualNovelManager.currentRenderer = null;
        }
    }
}

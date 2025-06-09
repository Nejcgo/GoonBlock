package com.github.nejcgo.goonblock.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class JumpscareRenderer {

    public final ResourceLocation ANIMATION_SPRITE_SHEET = new ResourceLocation("goonblock", "textures/gui/jumpscare/foxy.png");
    private final int SPRITE_SHEET_WIDTH_PX = 4000;  // Example: Total width of your sheet.png
    private final int SPRITE_SHEET_HEIGHT_PX = 3000; // Example: Total height of your sheet.png

    private final int FRAME_WIDTH_PX = 1000;         // Width of one animation frame
    private final int FRAME_HEIGHT_PX = 750;        // Height of one animation frame
    private final int TOTAL_ANIMATION_FRAMES = 14;  // Total frames in this animation
    private final int FRAMES_PER_ROW = 4;           // How many frames fit horizontally in your sheet

    private final int ANIMATION_FPS = 24;           // Desired playback speed (frames per second)
    private final long MS_PER_FRAME = 1000 / ANIMATION_FPS;

    // --- Animation Playback State ---
    private int currentAnimationFrame = 0;
    private long lastFrameSwitchTime = 0;
    private boolean isAnimationActive = false; // To control when it plays

    private final Minecraft mc = Minecraft.getMinecraft();

    public void startSpriteSheetAnimation() {
        this.isAnimationActive = true;
        this.currentAnimationFrame = 0;
        this.lastFrameSwitchTime = System.currentTimeMillis();
        System.out.println("Jumpscare activated!");

        if (mc.thePlayer != null) { // Always good to check if the player exists
            // The sound event name is "your_mod_id:sound_key_from_sounds.json"
            String soundEventName = "goonblock:effects.jumpscareSfx";
            float volume = 0.8F;

            float pitch = 1.1F;

            mc.thePlayer.playSound(soundEventName, volume, pitch);
            System.out.println("[GoonBlock] Playing sound: " + soundEventName);
        }
    }

    public void stopSpriteSheetAnimation() {
        this.isAnimationActive = false;
        this.currentAnimationFrame = 0;
        System.out.println("Jumpscare deactivated!");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(mc.thePlayer != null) {
            if (mc.thePlayer.ticksExisted % 20 == 0) {
                // System.out.println("isAnimationActive is: " + isAnimationActive);
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

        if (!isAnimationActive) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();


        // 1. Update current frame based on time
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameSwitchTime >= MS_PER_FRAME) {
            if(currentAnimationFrame + 1 == TOTAL_ANIMATION_FRAMES){
                stopSpriteSheetAnimation();
                return;
            }
            currentAnimationFrame = (currentAnimationFrame + 1) % TOTAL_ANIMATION_FRAMES;
            lastFrameSwitchTime = currentTime;
        }

        // 2. Calculate UV coordinates for the current frame on the sprite sheet
        //    u, v are the top-left pixel coordinates on the texture sheet.
        int frameRow = currentAnimationFrame / FRAMES_PER_ROW;
        int frameCol = currentAnimationFrame % FRAMES_PER_ROW;



        float u = (float)(frameCol * FRAME_WIDTH_PX) / SPRITE_SHEET_WIDTH_PX;
        float v = (float)(frameRow * FRAME_HEIGHT_PX) / SPRITE_SHEET_HEIGHT_PX;
        float uWidth = (float)FRAME_WIDTH_PX / SPRITE_SHEET_WIDTH_PX;
        float vHeight = (float)FRAME_HEIGHT_PX / SPRITE_SHEET_HEIGHT_PX;

        // 3. Bind the sprite sheet texture
        mc.getTextureManager().bindTexture(ANIMATION_SPRITE_SHEET);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Reset color tint

        // 4. Draw the current frame
        Gui.drawScaledCustomSizeModalRect(
                0, 0,                       // Position on screen
                frameCol * FRAME_WIDTH_PX,              // u_pixel_offset on texture sheet
                frameRow * FRAME_HEIGHT_PX,              // v_pixel_offset on texture sheet
                FRAME_WIDTH_PX, FRAME_HEIGHT_PX,        // Width and height of one frame on the texture sheet
                screenWidth, screenHeight,            // Desired width and height on screen
                SPRITE_SHEET_WIDTH_PX, SPRITE_SHEET_HEIGHT_PX // Full actual size of the texture sheet file
        );
    }
}

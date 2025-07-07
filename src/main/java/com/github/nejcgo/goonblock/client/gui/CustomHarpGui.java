package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.github.nejcgo.goonblock.classes.HarpNote;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import java.io.IOException;

public class CustomHarpGui extends GuiScreen {

    private final CustomSong song;
    private long songStartTime;

    // GUI layout constants
    private final int TRACK_COUNT = 9;
    private int trackWidth;
    private int tracksStartX;
    private int targetLineY;
    private int noteSpawnY;
    private int stepCount;
    private int stepTime;
    private int waitTime;

    // harp texture is 176*186
    int xSize = 176;
    int ySize = 186;

    private final ResourceLocation chestTexture = new ResourceLocation("goonblock", "textures/gui/harp/customHarpGui.png");

    public CustomHarpGui(CustomSong songToPlay) {
        this.song = songToPlay;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.songStartTime = System.currentTimeMillis();

        // Setup layout based on screen size
        this.trackWidth = 18; // Add some padding on sides
        this.tracksStartX = this.width / 2 - (trackWidth * TRACK_COUNT) / 2;
        this.targetLineY = this.height - 233;
        this.noteSpawnY = 26;
        this.stepCount = 8;
        this.stepTime = Math.round((60f/(song.bpm * 2)) * 1000); // time between steps in ms
        this.waitTime = 2000;


        for (HarpNote note : song.notes) {
            note.hit = false;
            note.missed = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(); // Darken the background

        long elapsedTime = System.currentTimeMillis() - songStartTime;

        drawTracks();
        drawFallingNotes(elapsedTime - waitTime);

        // Draw song info
        String title = song.name + " - by " + song.author;
        drawCenteredString(this.fontRendererObj, title, this.width / 2, 10, 0xFFFFFF);
    }

    private void drawTracks() {
        mc.getTextureManager().bindTexture(chestTexture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawScaledCustomSizeModalRect(
                (this.width - this.xSize) / 2,
                (this.height - this.ySize) / 2,
                0,
                0,
                xSize,
                ySize,
                xSize,
                ySize,
                xSize,
                ySize
        );

        // Draw the target line
        drawRect(tracksStartX, targetLineY - 1, tracksStartX + trackWidth * TRACK_COUNT, targetLineY + 1, Color.CYAN.getRGB());
    }

    private void drawFallingNotes(long elapsedTime) {
        double curStep = Math.floor(elapsedTime / stepTime);

        for (HarpNote note : song.notes) {
            if (note.hit) continue; // Don't draw notes that have been successfully hit

            long noteOffsetMs = note.offset * 50L;
            long timeUntilHit = noteOffsetMs - elapsedTime;
            double noteStep = Math.floor(noteOffsetMs / stepTime) - curStep;

            // Check if note should be visible
            if (timeUntilHit <= song.noteDurationMs && timeUntilHit > -200) { // Keep on screen briefly after passing

                // Calculate position
                float progress = 1.0f - ((float)timeUntilHit / song.noteDurationMs);
                int y = (int) Math.round(targetLineY - (noteStep * 18));
                int x = tracksStartX + note.track * trackWidth;

                // Draw the note
                int noteColor = note.missed ? Color.RED.getRGB() : Color.GREEN.getRGB();
                drawRect(x + 2, y - 9, x + trackWidth - 2, y + 9, noteColor);

                // Auto-play and mark as missed if player doesn't hit it in time
                if (timeUntilHit < -100 && !note.missed) { // 100ms tolerance for being "late"
                    note.missed = true;
                    playNoteSound(note.pitch); // Play sound so user hears the missed note
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) { // Left click
            long elapsedTime = System.currentTimeMillis() - songStartTime;

            // Is the click in the "hittable" vertical area?
            if (mouseY > targetLineY - 30 && mouseY < targetLineY + 30) {
                // Which track was clicked?
                if (mouseX > tracksStartX && mouseX < tracksStartX + trackWidth * TRACK_COUNT) {
                    HarpNote bestNoteToHit = getHarpNote(mouseX, elapsedTime);

                    if (bestNoteToHit != null) {
                        bestNoteToHit.hit = true;
                        playNoteSound(bestNoteToHit.pitch);
                        // You could add particle effects or "Perfect!" text here
                    }
                }
            }
        }
    }

    private @Nullable HarpNote getHarpNote(int mouseX, long elapsedTime) {
        int clickedTrack = (mouseX - tracksStartX) / trackWidth;

        // Find the closest note in this track that can be hit
        HarpNote bestNoteToHit = null;
        long smallestTimeDiff = Long.MAX_VALUE;

        for (HarpNote note : song.notes) {
            if (note.track == clickedTrack && !note.hit && !note.missed) {
                long timeDiff = Math.abs(elapsedTime - note.offset);
                if (timeDiff < 150 && timeDiff < smallestTimeDiff) { // 150ms hit window
                    smallestTimeDiff = timeDiff;
                    bestNoteToHit = note;
                }
            }
        }
        return bestNoteToHit;
    }

    private void playNoteSound(int noteIndex) {
        float pitch = (float) Math.pow(2.0, (noteIndex - 12.0) / 12.0);
        this.mc.thePlayer.playSound("note.harp", 1.5f, pitch);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // So the world keeps ticking (and time flows)
    }

    @Override
    public void onGuiClosed() {
        // Cleanup if needed
        System.out.println("Custom Harp GUI Closed.");
    }
}
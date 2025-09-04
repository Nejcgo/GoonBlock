package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.github.nejcgo.goonblock.classes.HarpNote;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomHarpGui extends GuiScreen {

    private final CustomSong song;
    private long songStartTime;

    // GUI layout constants
    private final int TRACK_COUNT = 7;
    private int trackWidth;
    private int tracksStartX;
    private int targetLineY;
    private int noteSpawnY;
    private int stepCount;
    private int stepTime;
    private int waitTime;
    private long songLengthMs;

    private RenderItem itemRenderer;

    private List<ItemStack> woolList = new ArrayList<>();
    private List<ItemStack> paneList = new ArrayList<>();
    private ItemStack quartzBlock;

    private Color backgroundColour = new Color(144, 144, 144);

    private Boolean isAuto = true;

    enum colours {
        pink,
        yellow,
        lime,
        green,
        purple,
        blue,
        lightBlue,
        black
    }

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
        this.tracksStartX = this.width / 2 - (trackWidth * TRACK_COUNT) / 2 + 1;
        this.targetLineY = (this.height / 2) + (ySize / 2) - 25;
        this.noteSpawnY = 26;
        this.stepCount = 8;
        this.stepTime = Math.round((60f/(song.bpm * 2)) * 1000); // time between steps in ms
        this.waitTime = 2000;

        this.itemRenderer = mc.getRenderItem();

        woolList.add(new ItemStack(Blocks.wool, 1, 6));
        woolList.add(new ItemStack(Blocks.wool, 1, 4));
        woolList.add(new ItemStack(Blocks.wool, 1, 5));
        woolList.add(new ItemStack(Blocks.wool, 1, 13));
        woolList.add(new ItemStack(Blocks.wool, 1, 10));
        woolList.add(new ItemStack(Blocks.wool, 1, 11));
        woolList.add(new ItemStack(Blocks.wool, 1, 3));
        woolList.add(new ItemStack(Blocks.wool, 1, 15));

        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 6));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 4));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 5));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 13));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 10));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 11));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 3));
        paneList.add(new ItemStack(Blocks.stained_glass_pane, 1, 15));

        quartzBlock = new ItemStack(Blocks.quartz_block);

        songLengthMs = 0L;

        for (HarpNote note : song.notes) {
            note.hit = false;
            note.missed = false;
            if(songLengthMs < note.offset){
                songLengthMs = note.offset;
            }
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

    @Override
    public void updateScreen(){
        long elapsedTime = System.currentTimeMillis() - songStartTime;

        if(elapsedTime > songLengthMs + waitTime + 2000){
            this.mc.thePlayer.playSound("random.levelup", 3f, 1f);
            GoonblockFunctions.sendChatMessage(mc,"§d[Harp] §aSong completed!");
            GoonblockFunctions.sendChatMessage(mc,"§d[Harp] §fYou suck §a§lass §fmy boy!");

            mc.displayGuiScreen(null);
        }
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

        //drawRect(tracksStartX, targetLineY - 1, tracksStartX + trackWidth * TRACK_COUNT, targetLineY + 1, Color.CYAN.getRGB());
    }

    private void drawFallingNotes(long elapsedTime) {
        double curStep = Math.floor((double) elapsedTime / stepTime);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        for (HarpNote note : song.notes) {
            if (note.hit) continue; // Don't draw notes that have been successfully hit

            long noteOffsetMs = note.offset;
            long timeUntilHit = noteOffsetMs - elapsedTime;
            double noteStep = Math.floor((double) noteOffsetMs / stepTime) - curStep;

            // Check if note should be visible
            if (timeUntilHit <= song.noteDurationMs && timeUntilHit > -200 && noteStep < 8) { // Keep on screen briefly after passing

                // Calculate position
                float progress = 1.0f - ((float)timeUntilHit / song.noteDurationMs);
                int y = (int) (((double) (this.height - this.xSize) / 2) + 147 - (noteStep * 18));
                int x = tracksStartX + (note.track * trackWidth) ;

                // Draw the note bg
                drawRect(x, y - 8, x + 16, y + 8, backgroundColour.getRGB());

                int woolColour = note.track < 7 ? note.track : 0;
                if (noteStep != 0) {
                    itemRenderer.renderItemIntoGUI(woolList.get(woolColour), x, y - 9);
                } else {
                    itemRenderer.renderItemIntoGUI(quartzBlock, x, y - 9);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) { // Left click
            System.out.println("Clicked inside harp GUI!");
            long elapsedTime = System.currentTimeMillis() - songStartTime - waitTime;

            if (mouseX > tracksStartX && mouseX < tracksStartX + trackWidth * TRACK_COUNT) {
                HarpNote bestNoteToHit = getHarpNote(mouseX, elapsedTime);
                if (bestNoteToHit != null) {
                    bestNoteToHit.hit = true;
                    playNoteSound(bestNoteToHit.pitch, bestNoteToHit.customSound);
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
                long timeDiff = Math.abs(elapsedTime - note.offset + 50); // +50ms to make late hits more lenient
                if (timeDiff < 250 && timeDiff < smallestTimeDiff) { // "250ms" hit window, uhuh, totally not a 200ms-300ms
                    smallestTimeDiff = timeDiff;
                    bestNoteToHit = note;
                }
            }
        }
        return bestNoteToHit;
    }

    private void playNoteSound(int noteIndex, String customSound) {
        float pitch = (float) Math.pow(2.0, (noteIndex - 12.0) / 12.0);
        if(customSound == null) {
            this.mc.thePlayer.playSound("note.harp", 2f, pitch);
        } else {
            this.mc.thePlayer.playSound(customSound, 2f, pitch);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // So the world keeps ticking (and time flows) (hard ass line)
    }

    @Override
    public void onGuiClosed() {
        // Cleanup if needed
        System.out.println("Custom Harp GUI Closed.");
    }
}
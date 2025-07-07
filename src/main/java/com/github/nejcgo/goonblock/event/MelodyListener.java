package com.github.nejcgo.goonblock.event;// In your MelodyListener.java (or a new dedicated HarpGuiListener.java)

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.github.nejcgo.goonblock.client.gui.CustomHarpGui;
import com.github.nejcgo.goonblock.util.CustomSongManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class MelodyListener {

    private final String NOTE = "♫";
    private final int CUSTOM_SONG_BUTTON_ID = 1337; // A unique ID for our button

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        // Check if this is the Harp song selection GUI
        GuiChest chestGui = (GuiChest) event.gui;
        String guiName = ((ContainerChest)chestGui.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();

        if (guiName.equals("Harp - Select Song") || guiName.equals("Melody " + NOTE) ) {
            // It's the right GUI! Let's add our button.
            int guiLeft = (chestGui.width - 176) / 2; // Standard chest GUI position calculations
            int guiTop = (chestGui.height - 222) / 2;

            // Place the button somewhere sensible. Example: Top right corner of the GUI.
            int buttonX = guiLeft + 176 - 125; // centered on the chest GUI
            int buttonY = guiTop - 15;        // 25 pixels above the GUI

            // Create and add the button
            GuiButton customSongButton = new GuiButton(CUSTOM_SONG_BUTTON_ID, buttonX, buttonY, 80, 20, "Custom Songs");
            event.buttonList.add(customSongButton);
        }
    }

    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        // Check if our custom button was the one that was clicked
        if (event.button.id == CUSTOM_SONG_BUTTON_ID) {
            System.out.println("Custom Songs button clicked!");

            // Here you would open another GUI to select WHICH custom song,
            // but for now, let's hardcode one to open our Fake Harp GUI.

            // Assume CustomSongManager has loaded our songs.
            CustomSong songToPlay = CustomSongManager.getSongById("mesmerizer");
            if (songToPlay != null) {
                // We don't need to close the current screen, displayGuiScreen does it for us.
                Minecraft.getMinecraft().displayGuiScreen(new CustomHarpGui(songToPlay));
            } else {
                System.err.println("Could not find song 'my_first_song' to play!");
            }
        }
    }
    // ...
}
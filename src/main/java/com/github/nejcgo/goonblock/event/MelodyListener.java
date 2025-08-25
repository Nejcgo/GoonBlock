package com.github.nejcgo.goonblock.event;// In your MelodyListener.java (or a new dedicated HarpGuiListener.java)

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.github.nejcgo.goonblock.client.gui.CustomHarpGui;
import com.github.nejcgo.goonblock.util.CustomSongManager;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import com.github.nejcgo.goonblock.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MelodyListener {

    private final String NOTE = "♫";
    private final int CUSTOM_SONG_BUTTON_ID = 1337; // A unique ID for our button

    private ItemStack customSongButton = new ItemStack(Items.writable_book);

    private boolean isSongSelectionGui = false;

    private Minecraft mc = Minecraft.getMinecraft();
    private RenderItem itemRenderer = mc.getRenderItem();

    private GuiHelper guiHelper = new GuiHelper();

    int buttonX = 0;
    int buttonY = 0;

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        // Check if this is the Harp song selection GUI
        GuiChest chestGui = (GuiChest) event.gui;
        String guiName = ((ContainerChest)chestGui.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();

        if (guiName.equals("Harp - Select Song") || guiName.equals("Melody " + NOTE) ) {
            isSongSelectionGui = true;
            // It's the right GUI! Let's add our button.
            int guiLeft = (chestGui.width - 176) / 2; // Standard chest GUI position calculations
            int guiTop = (chestGui.height - 222) / 2;

            // Place the button somewhere sensible. Example: Top right corner of the GUI.
            int buttonX = guiLeft + 176 - 125; // centered on the chest GUI
            int buttonY = guiTop - 15;        // 25 pixels above the GUI

            // Create and add the button
            //GuiButton customSongButton = new GuiButton(CUSTOM_SONG_BUTTON_ID, buttonX, buttonY, 80, 20, "Custom Songs");
            //event.buttonList.add(customSongButton);

            GoonblockFunctions.setDisplayName(customSongButton,
                    "§b§lCustom Songs §r§8(click)",
                    Arrays.asList("",
                            "§7Open the custom songs menu",
                            "§7These are §cnot §7officially made by Hypixel, so might be §oa bit §r§7difficult",
                            "§7Enjoy a ping-free harp!")
            );
        } else {
            isSongSelectionGui = false;
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
                System.err.println("Could not find song 'mesmerizer' to play!");
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        int mouseX = Mouse.getEventX() * event.gui.width / event.gui.mc.displayWidth;
        int mouseY = event.gui.height - Mouse.getEventY() * event.gui.height / event.gui.mc.displayHeight - 1;

        if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()){
            if(mouseX > buttonX && mouseX < buttonX+16 &&
                    mouseY > buttonY && mouseY < buttonY+16) {
                CustomSong songToPlay = CustomSongManager.getSongById("mesmerizer");
                if (songToPlay != null) {
                    // We don't need to close the current screen, displayGuiScreen does it for us.
                    Minecraft.getMinecraft().displayGuiScreen(new CustomHarpGui(songToPlay));
                } else {
                    System.err.println("Could not find song 'mesmerizer' to play!");
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiUpdate(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        if(isSongSelectionGui) {
            buttonX = (event.gui.width - 176)/2 + 8 + 2*18;
            buttonY = (event.gui.height - 222)/2 + 27 + 4*18;

            float ogZLevel = itemRenderer.zLevel;
            itemRenderer.zLevel = 300.0f;

            itemRenderer.renderItemIntoGUI(customSongButton, buttonX, buttonY);

            itemRenderer.zLevel = ogZLevel;

            if(event.mouseX > buttonX && event.mouseX < buttonX+16 &&
               event.mouseY > buttonY && event.mouseY < buttonY+16) {

                List<String> tooltipLines = customSongButton.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

                guiHelper.drawHoveringText(tooltipLines, event.mouseX, event.mouseY);
            }
        }
    }
}
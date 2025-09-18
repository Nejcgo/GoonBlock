package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.classes.CustomSong;
import com.github.nejcgo.goonblock.util.CustomSongManager;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import com.github.nejcgo.goonblock.util.GuiHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSongSelect extends GuiScreen {
    private Map<ItemStack, CustomSong> songButtons = new HashMap<>();
    private Map<Integer, ItemStack> buttonLocations = new HashMap<>();
    private final ResourceLocation CHEST_GUI = new ResourceLocation("minecraft","textures/gui/container/generic_54.png");
    private final ItemStack GLASS_PANE = new ItemStack(Blocks.stained_glass_pane, 1, 15);
    private final List<Integer> GLASS_PANE_SLOTS = Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53);

    private RenderItem itemRenderer;

    public CustomSongSelect(){}

    @Override
    public void initGui() {
        super.initGui();
        this.itemRenderer = mc.getRenderItem();

        CustomSong[] allSongs = CustomSongManager.getAllSongs().toArray(new CustomSong[0]);
        int i = 10; // start at the second slot on the 2nd row
        for (CustomSong song : allSongs) {
            ItemStack item = new ItemStack(Items.book);

            List<String> description = Arrays.asList("§8" + song.difficulty,
                    "",
                    "§7" + song.description,
                    "",
                    "§7Rewards:",
                    "§9+5ǂ Carpal Tunnel",
                    "",
                    "§eClick to Play!"
                    );

            GoonblockFunctions.setDisplayName(item, "§a" + song.name, description);

            songButtons.put(item, song);
            buttonLocations.put(i, item);

            i++;

            if ((i + 2) % 9 == 0) {
                i += 2;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        mc.getTextureManager().bindTexture(CHEST_GUI);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (this.width - 176) / 2;
        int y = (this.height - 221) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, 176, 221);

        for (int slot : GLASS_PANE_SLOTS) {
            GoonblockFunctions.renderItemIntoChestGuiSlot(GLASS_PANE, itemRenderer, this.width, this.height, slot, 6);
        }

        for (int slot : buttonLocations.keySet()) {
            GoonblockFunctions.renderItemIntoChestGuiSlot(buttonLocations.get(slot), itemRenderer, this.width, this.height, slot, 6);
        }

        Integer hoveredSlot = GoonblockFunctions.getSlotFromMousePosition(mouseX, mouseY, this.width, this.height, 6);
        if (hoveredSlot != null) {
            ItemStack hoveredButton = buttonLocations.get(hoveredSlot);
            if (hoveredButton != null) {
                List<String> tooltipLines = hoveredButton.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
                drawHoveringText(tooltipLines, mouseX, mouseY);
            }

            drawCenteredString(this.fontRendererObj ,hoveredSlot.toString(), this.width/2, 100, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SubscribeEvent
    public void onMouseClick (GuiScreenEvent.MouseInputEvent.Pre event) {
        int mouseX = Mouse.getEventX() * event.gui.width / event.gui.mc.displayWidth;
        int mouseY = event.gui.height - Mouse.getEventY() * event.gui.height / event.gui.mc.displayHeight - 1;

        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()){
            Integer hoveredSlot = GoonblockFunctions.getSlotFromMousePosition(mouseX, mouseY, this.width, this.height, 6);
            if (hoveredSlot != null) {
                ItemStack hoveredButton = buttonLocations.get(hoveredSlot);
                if (hoveredButton != null) {
                    CustomSong songToPlay = songButtons.get(hoveredButton);
                    if (songToPlay != null){
                        mc.displayGuiScreen(new CustomHarpGui(songToPlay));
                    }
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        MinecraftForge.EVENT_BUS.unregister(this);
        System.out.println("Custom Harp select Closed.");
    }
}

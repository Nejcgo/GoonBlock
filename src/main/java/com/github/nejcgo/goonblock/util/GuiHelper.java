package com.github.nejcgo.goonblock.util; // Or your util package

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import java.util.List;

// This class extends GuiScreen, so it inherits all its public and protected methods.
public class GuiHelper extends GuiScreen {

    public void drawHoveringText(List<String> textLines, int x, int y) {
        // This GuiHelper instance needs access to the game's width and height to draw correctly.
        this.mc = Minecraft.getMinecraft();
        this.fontRendererObj = this.mc.fontRendererObj;
        this.width = this.mc.currentScreen.width;
        this.height = this.mc.currentScreen.height;

        super.drawHoveringText(textLines, x, y);
    }
}

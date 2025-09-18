package com.github.nejcgo.goonblock.commands;

import com.github.nejcgo.goonblock.GoonBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ConfigCommand extends CommandBase {
    // --- Helper class to safely open the GUI on the next tick ---
    private static class GuiOpener {
        private GuiScreen screenToOpen = null;

        public GuiOpener() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        public void openGui(GuiScreen screen) {
            this.screenToOpen = screen;
        }

        @SubscribeEvent
        public void onTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END && screenToOpen != null) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpen);
                screenToOpen = null;
                // Unregister self to stop listening to ticks
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
    // -----------------------------------------------------------

    @Override
    public String getCommandName() {
        return "goonblock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/goonblock";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // Use the helper to schedule the GUI to be opened.
        // We create a new instance each time to ensure it registers and unregisters correctly.
        new GuiOpener().openGui(GoonBlock.configGui);
    }
}

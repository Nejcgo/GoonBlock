package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.classes.DialogueScript;
import com.github.nejcgo.goonblock.client.gui.DialogueRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DialogueManager {
    private static DialogueRenderer activeDialogue = null;
    private static final DialogueManager INSTANCE = new DialogueManager();

    // Private constructor to prevent multiple instances
    private DialogueManager() {}

    // Call this once from your main mod's init method
    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    /**
     * Starts a new dialogue, cancelling any that is currently active.
     * @param script The dialogue script to play.
     */
    public static void startDialogue(DialogueScript script) {
        if (script == null) return;
        activeDialogue = new DialogueRenderer(script);
    }

    /**
     * Stops the currently active dialogue.
     */
    public static void stopDialogue() {
        activeDialogue = null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && activeDialogue != null) {
            activeDialogue.update();
        }
    }
}

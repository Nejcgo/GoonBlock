package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.classes.DialogueLine;
import com.github.nejcgo.goonblock.classes.DialogueScript;
import com.github.nejcgo.goonblock.util.DialogueManager;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class DialogueRenderer {
    private final DialogueScript script;
    private int currentLineIndex;
    private long nextLineTimestamp;
    private final Minecraft mc = Minecraft.getMinecraft();

    public DialogueRenderer(DialogueScript script) {
        this.script = script;
        this.currentLineIndex = 0;
        // Start the first line immediately
        this.nextLineTimestamp = System.currentTimeMillis();
    }

    /**
     * This method is called every tick by the DialogueManager.
     * It checks if it's time to display the next line.
     */
    public void update() {
        if (mc.thePlayer == null) {
            DialogueManager.stopDialogue(); // Stop if the player leaves the world
            return;
        }

        // Check if enough time has passed to show the next line
        if (System.currentTimeMillis() >= this.nextLineTimestamp) {
            // Check if there are still lines left to say
            if (this.currentLineIndex < this.script.lines.size()) {
                DialogueLine currentLine = this.script.lines.get(this.currentLineIndex);

                // Format and send the chat message
                String formattedLine = String.format("§d[NPC] %s: §f%s", currentLine.speaker, currentLine.line);
                mc.thePlayer.addChatMessage(new ChatComponentText(formattedLine));

                // Set the timer for the NEXT line
                this.nextLineTimestamp = System.currentTimeMillis() + currentLine.delayMs;
                this.currentLineIndex++;
            } else {
                // We've finished all the lines, so show the clickable choices
                if (this.script.choices != null && !this.script.choices.isEmpty()) {
                    // Assumes your script ID is in the format "npcId_scriptName"
                    // e.g., "melody_intro" -> npcId = "melody"
                    String npcId = this.script.id.split("_")[0];

                    GoonblockFunctions.sendClickableChoices(npcId, this.script.choices);
                }

                // This script is finished. Stop the dialogue.
                DialogueManager.stopDialogue();
            }
        }
    }
}

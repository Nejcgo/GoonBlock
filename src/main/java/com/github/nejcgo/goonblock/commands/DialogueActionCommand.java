package com.github.nejcgo.goonblock.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import com.github.nejcgo.goonblock.util.MelodyRiftQuestManager;

public class DialogueActionCommand extends CommandBase {
    @Override
    public String getCommandName() {
        // A unique, internal name for your command.
        return "goonblockdialogue";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/goonblockdialogue <npcId> <action>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Accessible to everyone.
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) return; // Ignore if command is malformed.

        String npcId = args[0];
        String action = args[1];

        // Now, you pass this information to your main quest manager to handle the logic.
        // This keeps your command class clean and simple.
        MelodyRiftQuestManager.handleDialogueAction(npcId, action);
    }
}

package com.github.nejcgo.goonblock.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

import java.util.Collection;

public class GoonblockFunctions {

    // Easily show a message in chat
    public static ChatComponentText sendChatMessage(Minecraft mc, String messageString){
        if(mc.thePlayer == null){
            return null;
        }

        ChatComponentText chatComponent = new ChatComponentText(messageString);

        mc.thePlayer.addChatMessage(chatComponent);

        return chatComponent;
    }

    /** Returns true if the player is playing skyblock, returns false otherwise
     *
     * @param mc The minecraft instance
     * @return bool
     */
    public static boolean getSkyblock(Minecraft mc){
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1); // Slot 1 is the sidebar

            if (sidebarObjective != null) {
                String name = sidebarObjective.getName().replaceAll("[^a-zA-Z0-9\\s()-]", ""); // Keeps letters, numbers, spaces, parentheses, hyphens. Removes others.
                System.out.println("[GoonBlock SCOREDUMP SANITIZED] " + name); // See what this looks like

                if(name.contains("SBScoreboard")){
                    System.out.println("You are ON SKYBLOCK!");
                    return true;
                }
            }
        } catch (Exception e) {
            // Silently ignore errors, as scoreboard can be unstable during transitions
            // System.err.println("Error checking scoreboard: " + e.getMessage());
        }
        return false;
    }

    /** Returns true if player is currently on hypixel, returns false otherwise
     *
     * @param mc The minecraft instance
     * @return bool
     */
    public static boolean getHypixel(Minecraft mc){
        if (mc.isSingleplayer()) {
            return false;
        }
        ServerData serverData = mc.getCurrentServerData();
        if (serverData != null && serverData.serverIP != null) {
            if(serverData.serverIP.toLowerCase().contains("hypixel.net")){
                System.out.println("You are ON HYPIXEL!");
                return true;
            }
        }
        return false;
    }

    /** Returns the position in an ease out back position, starting at 0, and ending at 1.
     *
     * @param x Ranges from 0 to 1
     * @return double
     */
    public static double easeOutBack(double x){
        if(x > 1) return 1;
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }
}

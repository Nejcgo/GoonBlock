package com.github.nejcgo.goonblock.event; // Your package

import com.github.nejcgo.goonblock.util.BloodRushManager; // Your package
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting; // For stripping colors from scoreboard
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent; // For scoreboard checking

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class BloodRushListener {

    private BloodRushManager manager;
    private final String SKULL_ICON = "\u2620"; // Skull unicode character
    private boolean inCatacombs = false; // Flag to check if player is in dungeons

    // --- CHAT MESSAGES FOR BLOOD RUSH ---
    // START of the actual "Blood Rush" phase (after Blood Door is opened, Watcher initiates)
    private static final String BLOOD_RUSH_START_WATCHER_1 = "[BOSS] The Watcher: Things feel a little more roomy now, eh?";
    private static final String BLOOD_RUSH_START_WATCHER_2 = "[BOSS] The Watcher: I've knocked down those pillars"; // Contains should work
    private static final String BLOOD_RUSH_START_WATCHER_3 = "[BOSS] The Watcher: You have awoken me"; // Add other known Watcher start lines
    private static final String BLOOD_RUSH_START_WATCHER_4 = "[BOSS] The Watcher: Let's see how you handle this!"; // Example, verify in game

    // END of the "Blood Rush" phase (when all mobs in that room are cleared)
    // YOU NEED TO FIND THESE MESSAGES BY COMPLETING A BLOOD ROOM
    private static final String BLOOD_RUSH_END_MSG_1 = "The Watcher: You have proven yourself worthy."; // Example
    private static final String BLOOD_RUSH_END_MSG_2 = "All blood mobs have been cleared!"; // Example
    private static final String BLOOD_RUSH_END_MSG_F7_M7_BOSS_DOOR = "The giant's door has been unlocked!"; // From before, often after blood clear on F7/M7

    // Message indicating the blood door itself has opened (useful for state, but not the timer start for blood rush)
    // private static final String BLOOD_DOOR_OPENED_MSG = "The BLOOD DOOR has been opened!";


    public BloodRushListener(BloodRushManager manager) {
        this.manager = manager;
        MinecraftForge.EVENT_BUS.register(this); // Register for TickEvents if not done elsewhere
    }

    // Periodically check scoreboard for dungeon status
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.theWorld != null && mc.thePlayer != null) {
                // Check roughly every 2 seconds
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    this.inCatacombs = checkScoreboardForCatacombs(mc);
                }
            } else {
                this.inCatacombs = false; // Reset if not in world
            }
        }
    }

    private boolean checkScoreboardForCatacombs(Minecraft mc) {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1); // Slot 1 is the sidebar

            if (sidebarObjective != null) {
                Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
                for (Score score : scores) {
                    ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                    String line = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
                    String cleanedLine = StringUtils.stripControlCodes(line); // Remove color codes

                    // Example: "◎ The Catacombs (F7)"
                    if (cleanedLine.contains("The Catacombs")) {
                        // You could even extract the floor (e.g., "(F7)") here if needed
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // Silently ignore errors, as scoreboard can be unstable during transitions
            // System.err.println("Error checking scoreboard: " + e.getMessage());
        }
        return false;
    }


    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        // type 0 is standard chat/system messages from server, type 2 is often action bar
        if (event.type == 0 || event.type == 2) { // Some system messages are type 0
            String rawMessage = event.message.getUnformattedText();
            String cleanMessage = StringUtils.stripControlCodes(rawMessage);

            // Essential: Only operate if we're confirmed to be in The Catacombs
            if (!this.inCatacombs) {
                if (manager.isInBloodRush()) {
                    // If player somehow leaves dungeons while a rush was active (e.g. warp out), cancel it.
                    System.out.println("Left Catacombs, cancelling potential blood rush.");
                    manager.forceEndBloodRush(); // You'd need to add a method like forceEnd or reset to BloodRushManager
                }
                return;
            }

            // ---- BLOOD RUSH START ----
            if (cleanMessage.startsWith("§e[NPC] §bMort§f:")) {
                if (cleanMessage.contains("Good luck.") ||
                        cleanMessage.contains("I've knocked down those pillars") ||
                        cleanMessage.contains("You have awoken me") || // Add more specific Watcher lines
                        cleanMessage.contains("Let's see how you handle this!")) {
                    manager.startBloodRush();
                }
            }

            // ---- BLOOD RUSH END ----
            // YOU WILL NEED TO DISCOVER THE EXACT MESSAGE(S) FOR THIS PART
            // It could be from The Watcher, a generic server message, or a door unlocking.
            else if (manager.isInBloodRush() &&
                    (cleanMessage.contains(BLOOD_RUSH_END_MSG_1) || // Replace with actual messages
                            cleanMessage.contains(BLOOD_RUSH_END_MSG_2) ||
                            cleanMessage.contains(BLOOD_RUSH_END_MSG_F7_M7_BOSS_DOOR))) { // This is a common one after blood for F7/M7
                manager.endBloodRush();
            }


            // ---- DEATH DETECTION ----
            if (manager.isInBloodRush() && Minecraft.getMinecraft().thePlayer != null) {
                if (cleanMessage.startsWith(SKULL_ICON + " You ") &&
                        (cleanMessage.contains("died") || cleanMessage.contains("were killed by") || cleanMessage.contains("fell")) &&
                        !rawMessage.matches(".*§r§7<.*>.*") && // Exclude common player "<Name>: " prefixes
                        !rawMessage.matches(".*\\[.*\\] .*:") && // Exclude common rank "[Rank] Name:" prefixes
                        !cleanMessage.toLowerCase().contains(Minecraft.getMinecraft().thePlayer.getName().toLowerCase() + ":") // further exclude if player name is part of a chat message
                ) {
                    manager.incrementDeaths();
                    System.out.println("Player death detected during blood rush: " + cleanMessage);
                }
            }
        }
    }
}
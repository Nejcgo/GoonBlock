package com.github.nejcgo.goonblock.event;

import com.github.nejcgo.goonblock.util.MelodyRiftQuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;

public class RiftEnterListener {

    public boolean inRift = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.theWorld != null && mc.thePlayer != null) {
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    if (checkScoreboardForRift(mc) && !this.inRift) { MinecraftForge.EVENT_BUS.register(new MelodyRiftQuestManager()); }
                    this.inRift = checkScoreboardForRift(mc);
                }
            } else {
                this.inRift = false;
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.inRift = false;
    }

    private boolean checkScoreboardForRift(Minecraft mc) {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1); // Slot 1 is the sidebar

            if (sidebarObjective != null) {
                Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
                for (Score score : scores) {
                    ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                    String line = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
                    String cleanedLine = StringUtils.stripControlCodes(line); // Remove color codes
                    String sanitizedLine = cleanedLine.replaceAll("[^a-zA-Z0-9\\s()-]", ""); // Keeps letters, numbers, spaces, parentheses, hyphens. Removes others.
                    // System.out.println("[GoonBlock SCOREDUMP SANITIZED] " + sanitizedLine); // See what this looks like

                    if (sanitizedLine.contains("Rift Dimension")) {
                        System.out.println("[GoonBlock] 'Rift Dimension' (Sanitized) FOUND in scoreboard line!");
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
}

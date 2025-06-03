package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.GoonBlock; // Import your main mod class for MODID
import net.minecraft.client.Minecraft; // For playing sound

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BloodRushManager {
    private boolean inBloodRush = false;
    private long bloodRushStartTime = 0;
    private int deathsDuringRush = 0;

    private boolean showWaifu = false;
    private long waifuDisplayStartTime = 0;
    private final long WAIFU_DISPLAY_DURATION_MS = 7000; // Increased to allow for voice line
    private RoastProfile currentRoastProfile = null; // Changed from String

    private Random random = new Random();

    // --- Waifu Configuration ---
    // For now, let's hardcode one waifu. Later, this can come from a config.
    private String currentWaifuName = "asuna"; // This will be the folder name for textures/sounds

    // Example roast categories with expressions and sound keys
    private List<RoastProfile> goodRoasts = new ArrayList<>();
    private List<RoastProfile> mediumRoasts = new ArrayList<>();
    private List<RoastProfile> badRoasts = new ArrayList<>();
    private List<RoastProfile> deathRoasts = new ArrayList<>();

    public BloodRushManager() {
        // Populate your roasts
        // For "asuna":
        // Expressions: "happy.png", "neutral.png", "annoyed.png", "angry.png", "condescending.png"
        // Sounds: "good_1.ogg", "medium_1.ogg", "bad_1.ogg", "death_1.ogg" (soundEventKey part)

        goodRoasts.add(new RoastProfile("Hmph. Acceptable for a change.", "happy", "good_1"));
        goodRoasts.add(new RoastProfile("Not entirely terrible. I've seen worse... from you.", "neutral", "good_2"));

        mediumRoasts.add(new RoastProfile("Took you long enough. Were you napping?", "annoyed", "medium_1"));
        mediumRoasts.add(new RoastProfile("That was... adequate. Barely.", "condescending", "medium_2"));

        badRoasts.add(new RoastProfile("Pathetic. Do you even know which way the enemy is?", "angry", "bad_1"));
        badRoasts.add(new RoastProfile("Are you trying to make me look bad by association?", "condescending", "bad_2"));

        deathRoasts.add(new RoastProfile("Died again? Honestly, just stay down next time.", "angry", "death_1"));
        deathRoasts.add(new RoastProfile("Oh, look, a ghost. How very original of you.", "condescending", "death_2"));
    }

    public void startBloodRush() {
        if (!inBloodRush) {
            this.inBloodRush = true;
            this.bloodRushStartTime = System.currentTimeMillis();
            this.deathsDuringRush = 0;
            this.showWaifu = false;
            System.out.println("Blood Rush Started!");
        }
    }

    public void endBloodRush() {
        if (inBloodRush) {
            this.inBloodRush = false;
            long durationMs = System.currentTimeMillis() - bloodRushStartTime;
            System.out.println("Blood Rush Ended! Time: " + durationMs + "ms, Deaths: " + deathsDuringRush);

            judgePerformance(durationMs, deathsDuringRush); // This will set currentRoastProfile

            if (this.currentRoastProfile != null) {
                this.showWaifu = true;
                this.waifuDisplayStartTime = System.currentTimeMillis();

                // Play sound
                String soundEventName = GoonBlock.MODID + ":" + currentWaifuName + "." + currentRoastProfile.getSoundEventKey();
                try {
                    // Play as a non-positional UI sound
                    Minecraft.getMinecraft().thePlayer.playSound(soundEventName, 1.0F, 1.0F);
                    System.out.println("Playing sound: " + soundEventName);
                } catch (Exception e) {
                    System.err.println("Error playing sound " + soundEventName + ": " + e.getMessage());
                }
            }
        }
    }

    public void forceEndBloodRush() {
        this.inBloodRush = false;
    }

    public void incrementDeaths() {
        if (inBloodRush) {
            this.deathsDuringRush++;
        }
    }

    private void judgePerformance(long durationMs, int deaths) {
        if (deaths > 0) { // Prioritize death roasts if any deaths occurred
            currentRoastProfile = deathRoasts.get(random.nextInt(deathRoasts.size()));
        } else if (durationMs > 45000) { // Over 45 seconds
            currentRoastProfile = badRoasts.get(random.nextInt(badRoasts.size()));
        } else if (durationMs > 30000) { // Over 30 seconds
            currentRoastProfile = mediumRoasts.get(random.nextInt(mediumRoasts.size()));
        } else {
            currentRoastProfile = goodRoasts.get(random.nextInt(goodRoasts.size()));
        }
    }

    public boolean shouldShowWaifu() {
        if (showWaifu) {
            if (System.currentTimeMillis() - waifuDisplayStartTime > WAIFU_DISPLAY_DURATION_MS) {
                showWaifu = false;
                currentRoastProfile = null; // Clear the profile
                return false;
            }
            return true;
        }
        return false;
    }

    public RoastProfile getCurrentRoastProfile() {
        return currentRoastProfile;
    }

    public String getCurrentWaifuName() {
        return currentWaifuName;
    }

    public boolean isInBloodRush() {
        return inBloodRush;
    }

    // Method to change waifu (e.g., via command or config GUI later)
    public void setCurrentWaifuName(String waifuName) {
        this.currentWaifuName = waifuName;
        // Potentially reload roast lists if they are waifu-specific and loaded from files
        System.out.println("Current Waifu set to: " + waifuName);
    }
}
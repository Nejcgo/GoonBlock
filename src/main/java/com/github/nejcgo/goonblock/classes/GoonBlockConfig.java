package com.github.nejcgo.goonblock.classes;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.*;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GoonBlockConfig extends Config {
    public static Configuration config;

    public static String gender;
    public static Boolean hasShownFirstTimeMessage;

    @Expose
    @Category(name = "Chat", desc = "How the mod interacts with chat.")
    public ChatCategory chatCategory = new ChatCategory();

    public static class ChatCategory {

        @Expose
        @ConfigOption(
                name = "Player Pronoun",
                desc = " Choose the pronoun for custom messages."
        )
        @ConfigEditorDropdown(
                values = {"Male", "Female", "Pet"}
        )
        public int genderIndex = 0;

        @ConfigOption(
                name = "Chat Replacement",
                desc = " Enable or disable all chat modification features."
        )
        @ConfigEditorBoolean
        @Expose
        public boolean enableChatModification = true;

        @ConfigOption(
                name = "Positive reinforcement",
                desc = " Enable or disable certain messages added in chat."
        )
        @ConfigEditorBoolean
        @Expose
        public boolean enableGoodBoy = true;

        @ConfigOption(
                name = "Visual novel",
                desc = " Enable or disable visual novel mechanics as a whole."
        )
        @ConfigEditorBoolean
        @Expose
        public boolean enableVisualNovel = true;

        @Expose
        @ConfigOption(
                name = "Tips",
                desc = " Control how frequently quick tips show up."
        )
        @ConfigEditorDropdown(
                values = {"None", "Rare", "Frequent"}
        )
        public int tipFrequencyIndex = 0;
    }

    public static void initialize(File file) {
        config = new Configuration(file);
        config.load();
        gender = config.getString("Gender", "General", "pet", "Set your preferred gender");
        hasShownFirstTimeMessage = config.getBoolean("hasShownFirstTimeMessage", "general", false, "Set to true after the first-time welcome message has been shown.");
        config.save();
    }
}

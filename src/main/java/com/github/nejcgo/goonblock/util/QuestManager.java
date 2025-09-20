package com.github.nejcgo.goonblock.util;

public class QuestManager {
    public enum MelodyRiftQuestState {
        NOT_STARTED,
        SPOKE_TO_MELODY,          // After first dialogue
        COLLECTING_PIECES,      // After giving her the lily pad
        SPOKE_TO_SIRIUS,          // After finding all pieces and talking to Melody again
        READY_TO_UNLOCK,        // After Sirius dialogue
        UNLOCKED
    }

    public static MelodyRiftQuestState melodyRiftQuestState = MelodyRiftQuestState.NOT_STARTED;
}

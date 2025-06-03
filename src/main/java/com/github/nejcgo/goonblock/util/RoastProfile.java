package com.github.nejcgo.goonblock.util; // Or a new sub-package like 'data'

public class RoastProfile {
    private String text;
    private String expressionKey; // e.g., "happy", "angry", "condescending"
    private String soundEventKey; // e.g., "roast_good_1", "roast_bad_death" (will be prefixed)

    public RoastProfile(String text, String expressionKey, String soundEventKey) {
        this.text = text;
        this.expressionKey = expressionKey;
        this.soundEventKey = soundEventKey;
    }

    public String getText() {
        return text;
    }

    public String getExpressionKey() {
        return expressionKey;
    }

    public String getSoundEventKey() {
        return soundEventKey;
    }
}
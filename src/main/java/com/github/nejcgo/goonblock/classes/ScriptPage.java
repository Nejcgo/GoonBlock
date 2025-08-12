package com.github.nejcgo.goonblock.classes;

public class ScriptPage {
    private String name;
    private String text;
    private String sprite;
    private String sound;

    public String getSprite() {
        return sprite;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getSound() {
        return sound;
    }

    @Override
    public String toString() {
        return "ScriptPage{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

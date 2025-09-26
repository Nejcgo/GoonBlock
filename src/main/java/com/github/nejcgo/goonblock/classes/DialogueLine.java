package com.github.nejcgo.goonblock.classes;

public class DialogueLine {
    public String speaker;
    public String line;
    public long delayMs; // The delay AFTER this line is shown

    public DialogueLine(String speaker, String line, long delayMs) {
        this.speaker = speaker;
        this.line = line;
        this.delayMs = delayMs;
    }
}

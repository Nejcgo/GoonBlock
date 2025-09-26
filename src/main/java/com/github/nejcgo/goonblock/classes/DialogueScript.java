package com.github.nejcgo.goonblock.classes;

import java.util.List;
import java.util.Map;

public class DialogueScript {
    public String id; // A unique ID for this script
    public List<DialogueLine> lines;
    public Map<String, String> choices; // Key: Display Text, Value: Command Action

    public DialogueScript(String id, List<DialogueLine> lines, Map<String, String> choices) {
        this.id = id;
        this.lines = lines;
        this.choices = choices;
    }
}

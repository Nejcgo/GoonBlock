package com.github.nejcgo.goonblock.classes;

public class HarpNote {
    public long offset;
    public int track;
    public int pitch;

    // Add flags for gameplay logic
    public transient boolean hit = false;
    public transient boolean missed = false;
}
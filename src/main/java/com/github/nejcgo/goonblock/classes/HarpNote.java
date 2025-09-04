package com.github.nejcgo.goonblock.classes;

public class HarpNote {
    public long offset;
    public int track;
    public int pitch;

    public String customSound = null;

    public transient boolean hit = false;
    public transient boolean missed = false;
}
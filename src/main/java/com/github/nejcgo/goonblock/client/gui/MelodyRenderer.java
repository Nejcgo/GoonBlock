package com.github.nejcgo.goonblock.client.gui;

import com.github.nejcgo.goonblock.event.MelodyListener;
import net.minecraft.client.Minecraft;

public class MelodyRenderer {

    private final Minecraft mc;
    private final MelodyListener manager;

    public MelodyRenderer(MelodyListener manager){
        this.mc = Minecraft.getMinecraft();
        this.manager = manager;
    }
}

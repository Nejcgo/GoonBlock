package com.github.nejcgo.goonblock.event;

import com.github.nejcgo.goonblock.client.gui.JumpscareRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class JumpscareListener {

    public final Minecraft mc;

    private final Random random = new Random();
    private final JumpscareRenderer jumpscareRenderer;

    public JumpscareListener(JumpscareRenderer rendererToUse) {
        this.mc = Minecraft.getMinecraft();
        this.jumpscareRenderer = rendererToUse;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(mc.thePlayer != null) {
            if (mc.thePlayer.ticksExisted % 20 == 0) {
                if (random.nextInt(100000) == 0) {
                    jumpscareRenderer.startSpriteSheetAnimation();
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Keyboard.getEventKey() gives the LWJGL key code of the key that caused the event
        // Keyboard.getEventKeyState() is true if pressed, false if released
        // Keyboard.isKeyDown(keyCode) checks if a specific key is currently held down

        if (Keyboard.isKeyDown(Keyboard.KEY_G) && Keyboard.getEventKeyState()) { // Check if G was just pressed
            System.out.println("G key pressed!");
            // Check specific key just pressed: if (Keyboard.getEventKey() == Keyboard.KEY_H && Keyboard.getEventKeyState())
            jumpscareRenderer.startSpriteSheetAnimation();
        }
    }
}

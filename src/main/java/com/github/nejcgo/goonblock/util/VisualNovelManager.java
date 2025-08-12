package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.GoonBlock;
import com.github.nejcgo.goonblock.classes.Script;
import com.github.nejcgo.goonblock.client.gui.ScriptRenderer;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VisualNovelManager {
    private Gson gson = new Gson();
    private IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
    private ResourceLocation scriptLocation = new ResourceLocation("goonblock", "scripts/welcome.json");

    public Script script;
    public static ScriptRenderer currentRenderer = null;

    private boolean isAlreadyRunning = false;

        @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        if(isAlreadyRunning){
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if(!GoonBlock.hasShownFirstTimeMessage){
            System.out.println("This is the first time logging on!");
            new Thread(() -> {
                try {
                    // Scoreboard takes some time to init
                    Thread.sleep(3000);

                    if(GoonblockFunctions.getSkyblock(mc) && !isAlreadyRunning) {
                        isAlreadyRunning = true;

                        mc.addScheduledTask(this::showWelcomeScript);

                        GoonBlock.hasShownFirstTimeMessage = true;

                        //GoonBlock.config.get("general", "hasShownFirstTimeMessage", false).set(true);
                        //GoonBlock.config.save();
                    }
                } catch (Exception e) {
                    System.err.println("Uh oh, something happened! Reason: " + e);
                }
            }).start();
        }
    }

    public void showWelcomeScript() {
        try {
            String scriptPath = "/assets/goonblock/scripts/welcome.json";

            Script welcomeScript = Script.loadFromFile(scriptPath);

            displayScript(welcomeScript);

        } catch (Exception e) {
            // Always handle potential errors, like a missing or malformed file
            System.err.println("Failed to load or display the welcome script!");
            e.printStackTrace();
        }
    }

    private void displayScript(Script script){
        if(currentRenderer != null){
            currentRenderer.finish();
        }

        currentRenderer = new ScriptRenderer(script);
        MinecraftForge.EVENT_BUS.register(currentRenderer);

        System.out.println("Creating Text box instance!");
    }
}

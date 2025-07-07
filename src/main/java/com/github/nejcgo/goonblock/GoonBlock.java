package com.github.nejcgo.goonblock;

import com.github.nejcgo.goonblock.client.gui.*;
import com.github.nejcgo.goonblock.event.JumpscareListener;
import com.github.nejcgo.goonblock.event.MelodyListener;
import com.github.nejcgo.goonblock.util.CustomSongManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.github.nejcgo.goonblock.event.BloodRushListener;
import com.github.nejcgo.goonblock.util.BloodRushManager;

@Mod(modid = GoonBlock.MODID, version = GoonBlock.VERSION, name = GoonBlock.NAME, clientSideOnly = true)
public class GoonBlock {
    public static final String MODID = "goonblock";
    public static final String VERSION = "1.0";
    public static final String NAME = "GoonBlock Skyblock Helper";

    public static BloodRushManager bloodRushManager;
    public static JumpscareRenderer jumpscareRenderer;
    public static MelodyListener melodyListener;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Config loading could go here
        CustomSongManager.initialize(event.getModConfigurationDirectory());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("GoonBlock Initializing!");
        bloodRushManager = new BloodRushManager();
        jumpscareRenderer = new JumpscareRenderer();
        melodyListener = new MelodyListener();

        // Register event listeners
        MinecraftForge.EVENT_BUS.register(new BloodRushListener(bloodRushManager));
        MinecraftForge.EVENT_BUS.register(new WaifuOverlayRenderer(bloodRushManager));
        MinecraftForge.EVENT_BUS.register(new LobotomyRenderer(bloodRushManager));
        MinecraftForge.EVENT_BUS.register(jumpscareRenderer);
        MinecraftForge.EVENT_BUS.register(new JumpscareListener(jumpscareRenderer));
        MinecraftForge.EVENT_BUS.register(melodyListener);
        MinecraftForge.EVENT_BUS.register(new MelodyRenderer(melodyListener));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Preload the jumpscare texture on the client side
        if (event.getSide().isClient()) { // Good practice, though less critical for clientSideOnly mods
            System.out.println("GoonBlock: Preloading jumpscare sprite sheet...");
            try {
                // Calling bindTexture will prompt the TextureManager to load it if it hasn't already.
                Minecraft.getMinecraft().getTextureManager().bindTexture(jumpscareRenderer.ANIMATION_SPRITE_SHEET);
                System.out.println("GoonBlock: Jumpscare sprite sheet preloading initiated for: " + jumpscareRenderer.ANIMATION_SPRITE_SHEET);
            } catch (Exception e) {
                System.err.println("GoonBlock: Error during jumpscare sprite sheet preloading.");
                e.printStackTrace();
            }
        }
    }
}
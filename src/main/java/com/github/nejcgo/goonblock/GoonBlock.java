package com.github.nejcgo.goonblock;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.github.nejcgo.goonblock.event.BloodRushListener;
import com.github.nejcgo.goonblock.client.gui.WaifuOverlayRenderer;
import com.github.nejcgo.goonblock.util.BloodRushManager;

@Mod(modid = GoonBlock.MODID, version = GoonBlock.VERSION, name = GoonBlock.NAME, clientSideOnly = true)
public class GoonBlock {
    public static final String MODID = "goonblock";
    public static final String VERSION = "1.0";
    public static final String NAME = "GoonBlock Skyblock Helper";

    public static BloodRushManager bloodRushManager; // To manage state

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Config loading could go here
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("GoonBlock Initializing!");
        bloodRushManager = new BloodRushManager();

        // Register event listeners
        MinecraftForge.EVENT_BUS.register(new BloodRushListener(bloodRushManager));
        MinecraftForge.EVENT_BUS.register(new WaifuOverlayRenderer(bloodRushManager));
    }
}
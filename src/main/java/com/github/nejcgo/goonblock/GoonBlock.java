package com.github.nejcgo.goonblock;

import com.github.nejcgo.goonblock.classes.GoonBlockConfig;
import com.github.nejcgo.goonblock.client.gui.*;
import com.github.nejcgo.goonblock.commands.ConfigCommand;
import com.github.nejcgo.goonblock.event.GoodJobListener;
import com.github.nejcgo.goonblock.event.JumpscareListener;
import com.github.nejcgo.goonblock.event.MelodyListener;
import com.github.nejcgo.goonblock.util.CustomSongManager;
import com.github.nejcgo.goonblock.util.NPCSkinChanger;
import com.github.nejcgo.goonblock.util.VisualNovelManager;
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.github.nejcgo.goonblock.event.BloodRushListener;
import com.github.nejcgo.goonblock.util.BloodRushManager;

import java.io.File;

@Mod(modid = GoonBlock.MODID, version = GoonBlock.VERSION, name = GoonBlock.NAME, clientSideOnly = true)
public class GoonBlock {
    public static final String MODID = "goonblock";
    public static final String VERSION = "1.0";
    public static final String NAME = "GoonBlock Skyblock Helper";

    public static BloodRushManager bloodRushManager;
    public static JumpscareRenderer jumpscareRenderer;
    public static MelodyListener melodyListener;

    public static Configuration config;
    public static boolean hasShownFirstTimeMessage;

    public static GoonBlockConfig goonBlockConfig = new GoonBlockConfig();

    // 2. Create a static field for the GuiScreen that will be created.
    public static GuiScreen configGui = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Config loading could go here
        CustomSongManager.initialize();

        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        config.load();

        // Define your property
        hasShownFirstTimeMessage = config.getBoolean("hasShownFirstTimeMessage", "general", false, "Set to true after the first-time welcome message has been shown.");
        GoonBlockConfig.initialize(event.getSuggestedConfigurationFile());

        // Save the configuration if it has changed
        if (config.hasChanged()) {
            config.save();
        }
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
        MinecraftForge.EVENT_BUS.register(new NPCSkinChanger());
        MinecraftForge.EVENT_BUS.register(new VisualNovelManager());
        MinecraftForge.EVENT_BUS.register(new GoodJobListener());

        MoulConfigProcessor<GoonBlockConfig> processor = new MoulConfigProcessor<>(goonBlockConfig);

        // This line is optional but good practice. It adds built-in editors for common types.
        BuiltinMoulConfigGuis.addProcessors(processor);

        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.processConfig(goonBlockConfig); // This populates the processor with your config's fields.

        // Create the editor GUI, wrapped in a standard GuiScreen.
        MoulConfigEditor<GoonBlockConfig> editor = new MoulConfigEditor<>(processor);
        configGui = new GuiScreenElementWrapper(editor);

        // 4. Register your command to open the GUI.
        ClientCommandHandler.instance.registerCommand(new ConfigCommand());
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
package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.classes.Script;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VisualNovelManager {
    private Gson gson = new Gson();
    private IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
    private ResourceLocation scriptLocation = new ResourceLocation("goonblock", "visual_novel_scripts/welcome.json");

    private void loadScript() {
        try {
            IResource resource = resourceManager.getResource(scriptLocation);
            try (InputStream inputStream = resource.getInputStream();
                 InputStreamReader reader = new InputStreamReader(inputStream)) {
                Script script = gson.fromJson(reader, Script.class);
            }
        } catch (IOException e) {
            System.err.println("GoonBlock: CRITICAL - Failed to load internal script from " + scriptLocation);
            e.printStackTrace();
        }
    }
}

package com.github.nejcgo.goonblock.util;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NPCSkinChanger {

    private final Minecraft mc = Minecraft.getMinecraft();
    private static final Set<UUID> toggledNpcs = Sets.newHashSet();

    // The deobfuscated field name for the layer list in RenderLivingBase.
    // This is stable and correct for the 1.8.9 modding environment.
    private final String LAYER_RENDERERS_FIELD_NAME = "layerRenderers";

    public static boolean isToggled(UUID uuid) {
        return toggledNpcs.contains(uuid);
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.button == 2 && event.buttonstate) {
            MovingObjectPosition objectMouseOver = mc.objectMouseOver;
            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                Entity targetEntity = objectMouseOver.entityHit;

                if (targetEntity instanceof AbstractClientPlayer && isHypixelNPC(targetEntity)) {
                    UUID entityId = targetEntity.getUniqueID();
                    if (toggledNpcs.contains(entityId)) {
                        toggledNpcs.remove(entityId);
                    } else {
                        toggledNpcs.add(entityId);
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    private boolean isHypixelNPC(Entity entity) {
        return mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(entity.getUniqueID()) == null;
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        // Add our layer to the renderer if it doesn't already have it.
        addLayerToRenderer(event.renderer);

        // If this specific NPC is toggled, hide the main model.
        // Our custom layer will do the rendering instead.

            event.renderer.getMainModel().setInvisible(true);

    }

    // This helper method uses reflection to safely add our layer only once.
    private void addLayerToRenderer(RenderPlayer renderer) {
        try {
            // We must look in the superclass (RenderLivingBase) for the field.
            Field layerRenderersField = renderer.getClass().getSuperclass().getDeclaredField(LAYER_RENDERERS_FIELD_NAME);
            layerRenderersField.setAccessible(true); // Make the protected field accessible.
            List<LayerRenderer> layers = (List<LayerRenderer>) layerRenderersField.get(renderer);

            // Check if an instance of our layer is already in the list.
            boolean hasLayer = layers.stream().anyMatch(layer -> layer instanceof LayerCustomSkin);

            if (!hasLayer) {
                // If not, add a new instance of our layer.
                layers.add(new LayerCustomSkin(renderer));
                System.out.println("GoonBlock Mod: Successfully injected custom skin layer.");
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // This would only happen if the mod is used in a very different environment.
            System.err.println("GoonBlock Mod: CRITICAL - Could not access layer renderers field!");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        // This is crucial. ALWAYS set the model visibility back to false after rendering.
        event.renderer.getMainModel().setInvisible(false);
    }
}
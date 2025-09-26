package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.classes.DialogueLine;
import com.github.nejcgo.goonblock.classes.DialogueScript;
import com.github.nejcgo.goonblock.classes.NpcData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class MelodyRiftQuestManager {
    private final BlockPos melodyPos;
    private final BlockPos harp1Pos;
    private final Minecraft mc;

    private List<NpcData> npcList = new ArrayList<>();

    private float targetYaw = 0.0F;
    private float targetPitch = 0.0F;

    private static MelodyRiftQuestManager INSTANCE;

    public MelodyRiftQuestManager() {
        this.melodyPos = new BlockPos(20, 70, -50);
        this.harp1Pos = new BlockPos(27, 71, -79);
        this.mc = Minecraft.getMinecraft();

        INSTANCE = this;

        spawnMelodyNpc();
        spawnHarpNpcs();
    }

    public MelodyRiftQuestManager getInstance() {
        return INSTANCE;
    }

    public void spawnMelodyNpc() {
        NpcData melodyData = GoonblockFunctions.createNpc("50b40119-7a7e-41ae-8bc9-deee8d52d40d",
                "Melody, but depressed",
                melodyPos,
                Arrays.asList("§dMelody", "§e§lCLICK"),
                "ewogICJ0aW1lc3RhbXAiIDogMTc1ODI5MTc4MTc2NywKICAicHJvZmlsZUlkIiA6ICI2NDU4Mjc0MjEyNDg0MDY0YTRkMDBlNDdjZWM4ZjcyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaDNtMXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI0Mjg4ZjkwYWY5YjNjZWE5NmJkYjRmNmI3YTZhOTNhMTk2MDIxZWY0MzcwZTg1NWY3ZjY1MDBlNjZjYmIwYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                "FQCzX0iN0YivabET+e0IZnKE6Xyio2lquPX1/S4TqyX7s3sTahzhm8xuR1ys+HAUbr270uChN4F+TCmzph+DSOKo1v7jUd1ftdCeG/DjFNYbfxPscbeGIAvtfpBc4rR5n1Jh3f88j+IrvJd1dHuLQ8tTYic3lxmThdV2VnppIVfo2oD/8XmWiN8AzZRSVxhE4sY5Rkqw+k5GvCCqOnSzdkKSmS1Zp/IG5HI8FqmoMA6s3uQ0ZHEx3rU/xVoSVo43Z58l5pz8XfupFfC06tD5W+y6xneRvwtbxcZT+DjXq71cy/WX0CxRl5E4UnciFO89X0Hn2HpeqsdmRmAVmH9eCRHxRx+A1jP6cKcb1dFHZBHMpu1zmraMIJ07WVScZn/Xc+dh4ea7AoOw8c2GbIVIThEnqCh3bqI4Td+frl/FV7/bU+5xbs6Uq5JUosPdB4rUdxYI2kp94ujY2D+rbprwHZt4NKjG61EEifda9rispXJpZ+BH18f/kuD7pbsFxO96C+tNR8DW2MULlCMv4D2R+nCfHLjVc352JOcZGipb3WvVUJXo2SpMhIsJdjxOMvhEuSSR7bVRaYMs+x54aCv09EV5QSwdMsC9DWmspyI1KloS93C7iq+Lf2lwswZhjWbnDlAbQGNohhsv/wNHZjf/GSq5rN9ozN37VTyorlr5+CM="
                );

        if (melodyData != null) npcList.add(melodyData);

        System.out.println("GoonBlock: Spawned client-side Melody NPC.");
    }

    public void spawnHarpNpcs() {
        // ---------------- HARP PIECE NUMBER 1: BAZAAR -------------------------------

        NpcData harp1Data = GoonblockFunctions.createNpc("e0023b79-d542-41d6-8a21-149dd0fb467e",
                "Bazaar Merchant",
                harp1Pos,
                Arrays.asList("Bazaar Merchant", "§e§lCLICK"),
                "ewogICJ0aW1lc3RhbXAiIDogMTc1ODI5MTc4MTc2NywKICAicHJvZmlsZUlkIiA6ICI2NDU4Mjc0MjEyNDg0MDY0YTRkMDBlNDdjZWM4ZjcyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaDNtMXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI0Mjg4ZjkwYWY5YjNjZWE5NmJkYjRmNmI3YTZhOTNhMTk2MDIxZWY0MzcwZTg1NWY3ZjY1MDBlNjZjYmIwYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                "HEfhsz5oYVRfN1eo/p+DRvmSjBUFYfTq1Lr0AWRdeJDBWGttC5vuX/4tixY65F5/P5771nUzG38mAQBzlSkgn332sCjRfUfFiehnDHdINKPqIhqx1qo/IA2/zvOyL5HevNhxaVn8njW3dggRqvyVaHS+nAQbOtHg9fPMTPRKBXvyEHFdDKuHs9yLiR3XtTehB+y6qvZfSjTt8FXM4ViZD2irhFebvt0EBYDPIaEuft5Yma/2anIKJ5/CGveEwToRJ7wT5IT94rnELojafUObWZgGG66cBigMANFMlUNOMRE73uG4HwLcLEorwjfaE/6Exe8Ppha5l3910kKJqzDZiUzKuN0YtvqGTpdkAox/s253Z6U4K3+2iG756GkslWRFWs2On9f1+eoDxkbE4je5m2CfDDiLwuTlwOj2VFnBmhnPOB9okGP080GWfzVnb8QSdVA9o8SpzzzpA8cgOA2CWkD9wCki3wBZYIc2OkhO1acXWT4sOKj2agWXMXMMQFP/N9CwFt3wAe2rw5cfmCuEHSuMsynRwcPng4yhJIDXO54fggGKT7sM7W14yQFKSBqhGiOoPGK2rBu+ya3Rr6ZhATb+hckodjWe7HtoFuqlR3AEVhyBbwhjSlM10e49cpWzJ9fV+6C3wrNVnnoqDWC36ekH9cpewKwVIU1EouMxu+I="
        );

        if (harp1Data != null) npcList.add(harp1Data);

        System.out.println("GoonBlock: Spawned client-side Harp 1 NPC.");

        // ---------------- HARP PIECE NUMBER 2: VENDING MACHINE -------------------------------

    }

    public void despawnMelodyNpc() {
        for (NpcData npcData : npcList) {
            if (npcData != null && npcData.entity != null) {
                GoonblockFunctions.removePlayerInfo(mc.getNetHandler(), npcData.entity.getUniqueID());
            }
        }

        this.npcList.clear();

        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Entity hitEntity = GoonblockFunctions.performRayCast(3, 1, npcList);
            System.out.println("Right clicked!");
            for (NpcData npcData : npcList) {
                if(npcData.entity.equals(hitEntity)) {
                    System.out.println("Right clicked an NPC!!");
                    event.setCanceled(true);
                    startDialogue("melody");
                }
            }
        }
    }

    public static void handleDialogueAction(String npcId, String action) {

    }

    public void startDialogue(String npcId) {
        if (npcId.equals("melody")) {
            if (QuestManager.melodyRiftQuestState == QuestManager.MelodyRiftQuestState.NOT_STARTED) {
                // 1. Define the dialogue script
                List<DialogueLine> lines = Arrays.asList(
                        new DialogueLine("Melody", "Oh... hello.", 1500),
                        new DialogueLine("Melody", "Could you?- N-nevermind...", 2000),
                        new DialogueLine("Melody", "You see, my harp got blown to pieces when that Wizardman guy flew past!", 2500),
                        new DialogueLine("Melody", "Now the pieces don't know what to think!", 2000),
                        new DialogueLine("Melody", "They don't even KNOW they're part of a harp anymore... It's so sad.", 2500),
                        new DialogueLine("Melody", "You seem like someone who could convince them. Could you please help me?", 2000)
                );
                Map<String, String> choices = new LinkedHashMap<>();
                choices.put("§a§l[HELP HER]", "acceptQuest");
                choices.put("§c§l[LEAVE]", "leave");

                DialogueScript script = new DialogueScript("melody_intro", lines, choices);

                // 2. Tell the manager to start playing it
                DialogueManager.startDialogue(script);
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        despawnMelodyNpc();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || this.npcList.isEmpty()) { return; }

        for (NpcData npcData : npcList) {
            EntityOtherPlayerMP entity = npcData.entity;
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            entity.prevRotationYawHead = entity.rotationYawHead;
            entity.prevRenderYawOffset = entity.renderYawOffset;
            if (mc.thePlayer.getDistanceToEntity(entity) < 10) {
                calculateTargetLookDirection(entity);
                entity.rotationYawHead = interpolateRotation(entity.rotationYawHead, this.targetYaw, 5F);
                entity.renderYawOffset = interpolateRotation(entity.renderYawOffset, this.targetYaw, 20F);
                entity.rotationYaw = interpolateRotation(entity.rotationYaw, this.targetYaw, 20F);
                entity.rotationPitch = interpolateRotation(entity.rotationPitch, this.targetPitch, 5F);
            }
        }
    }

    private void calculateTargetLookDirection(EntityOtherPlayerMP entity) {
        Vec3 playerEyePos = mc.thePlayer.getPositionEyes(1.0F); // 1.0F is partialTicks
        Vec3 npcPos = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);

        // Step 2: Calculate vector
        double dx = playerEyePos.xCoord - npcPos.xCoord;
        double dy = playerEyePos.yCoord - npcPos.yCoord;
        double dz = playerEyePos.zCoord - npcPos.zCoord;

        // Step 3: Convert to yaw and pitch
        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dy, horizontalDistance) * (180.0D / Math.PI)));

        this.targetYaw = yaw;
        this.targetPitch = pitch;
    }

    //@SubscribeEvent
    //public void onRenderEntity(RenderLivingEvent.Pre<EntityOtherPlayerMP> event) {
    //    if (event.entity.equals(this.melodyEntity)) {
//
    //        event.entity.rotationYawHead = this.melodyEntity.rotationYawHead;
    //        event.entity.renderYawOffset = this.melodyEntity.renderYawOffset;
    //        event.entity.rotationYaw = this.melodyEntity.rotationYaw;
    //        event.entity.rotationPitch = this.melodyEntity.rotationPitch;
    //    }
    //}

    @SubscribeEvent
    public void onRenderNametag(RenderLivingEvent.Specials.Pre<EntityOtherPlayerMP> event) {
        for (NpcData npcData : npcList) {
            EntityOtherPlayerMP entity = npcData.entity;
            if (event.entity.equals(entity)) {
                event.setCanceled(true);
            }
        }
    }

    private float interpolateRotation(float current, float target, float speed) {
        float diff = MathHelper.wrapAngleTo180_float(target - current);
        return current + diff / speed;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!this.npcList.isEmpty()) {
            for (NpcData npcData : npcList) {
                EntityOtherPlayerMP entity = npcData.entity;

                BlockPos entityPos = new BlockPos(entity.posX, entity.posY, entity.posZ);
                int lightValue = mc.theWorld.getCombinedLight(entityPos, 0);
                int lightX = lightValue % 65536;
                int lightY = lightValue / 65536;

                // 2. Apply this light level to the rendering engine.
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) lightX, (float) lightY);
                mc.getRenderManager().renderEntitySimple(entity, event.partialTicks);

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);

                drawCustomNametag(entity, npcData.nametag, event.partialTicks);
            }
        }
    }

    private void drawCustomNametag(EntityOtherPlayerMP entity, List<String> lines, float partialTicks) {
        double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        double textY = entityY + entity.height + 0.5;

        double cameraX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
        double cameraY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
        double cameraZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

        RenderManager renderManager = mc.getRenderManager();
        FontRenderer fontRenderer = renderManager.getFontRenderer();

        GlStateManager.pushMatrix();

        GlStateManager.translate((float)(entityX - cameraX), (float)(textY - cameraY), (float)(entityZ - cameraZ));

        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int lineHeight = fontRenderer.FONT_HEIGHT + 1;
        // 2. Start drawing at the top (y=0 in our translated space).
        int currentY = 0;

        // 3. Loop through each line in the list.
        for (String line : lines) {
            // Center each line individually.
            int textWidth = fontRenderer.getStringWidth(line) / 2;
            fontRenderer.drawString(line, -textWidth, currentY, 0xFFFFFFFF);

            // Move down to the position for the next line.
            currentY += lineHeight;
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}

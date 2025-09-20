package com.github.nejcgo.goonblock.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;

public class MelodyRiftQuestManager {
    private final BlockPos melodyPos;
    private Minecraft mc;

    private EntityOtherPlayerMP melodyEntity = null;
    private NetworkPlayerInfo melodyPlayerInfo = null;

    private float targetYaw = 0.0F;
    private float targetPitch = 0.0F;

    public MelodyRiftQuestManager() {
        this.melodyPos = new BlockPos(20, 70, -50); // Set her actual target coordinates
        this.mc = Minecraft.getMinecraft();

        spawnMelodyNpc();
    }

    public void spawnMelodyNpc() {
        // Check if she already exists to prevent creating duplicates
        if (this.melodyEntity != null) {
            return;
        }

        UUID melodyUUID = UUID.fromString("50b40119-7a7e-41ae-8bc9-deee8d52d40d");
        GameProfile melodyProfile = new GameProfile(melodyUUID, "Melody");

        String textureValue = "ewogICJ0aW1lc3RhbXAiIDogMTc1ODI5MTc4MTc2NywKICAicHJvZmlsZUlkIiA6ICI2NDU4Mjc0MjEyNDg0MDY0YTRkMDBlNDdjZWM4ZjcyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaDNtMXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI0Mjg4ZjkwYWY5YjNjZWE5NmJkYjRmNmI3YTZhOTNhMTk2MDIxZWY0MzcwZTg1NWY3ZjY1MDBlNjZjYmIwYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
        String signature = "FQCzX0iN0YivabET+e0IZnKE6Xyio2lquPX1/S4TqyX7s3sTahzhm8xuR1ys+HAUbr270uChN4F+TCmzph+DSOKo1v7jUd1ftdCeG/DjFNYbfxPscbeGIAvtfpBc4rR5n1Jh3f88j+IrvJd1dHuLQ8tTYic3lxmThdV2VnppIVfo2oD/8XmWiN8AzZRSVxhE4sY5Rkqw+k5GvCCqOnSzdkKSmS1Zp/IG5HI8FqmoMA6s3uQ0ZHEx3rU/xVoSVo43Z58l5pz8XfupFfC06tD5W+y6xneRvwtbxcZT+DjXq71cy/WX0CxRl5E4UnciFO89X0Hn2HpeqsdmRmAVmH9eCRHxRx+A1jP6cKcb1dFHZBHMpu1zmraMIJ07WVScZn/Xc+dh4ea7AoOw8c2GbIVIThEnqCh3bqI4Td+frl/FV7/bU+5xbs6Uq5JUosPdB4rUdxYI2kp94ujY2D+rbprwHZt4NKjG61EEifda9rispXJpZ+BH18f/kuD7pbsFxO96C+tNR8DW2MULlCMv4D2R+nCfHLjVc352JOcZGipb3WvVUJXo2SpMhIsJdjxOMvhEuSSR7bVRaYMs+x54aCv09EV5QSwdMsC9DWmspyI1KloS93C7iq+Lf2lwswZhjWbnDlAbQGNohhsv/wNHZjf/GSq5rN9ozN37VTyorlr5+CM=";
        melodyProfile.getProperties().put("textures", new Property("textures", textureValue, signature));

        this.melodyPlayerInfo = new NetworkPlayerInfo(melodyProfile);

        this.melodyEntity = new EntityOtherPlayerMP(mc.theWorld, melodyProfile);

        GoonblockFunctions.addPlayerInfo(mc.getNetHandler(), melodyEntity.getUniqueID(), this.melodyPlayerInfo);

        this.melodyEntity.setPosition(melodyPos.getX() + 0.5, melodyPos.getY(), melodyPos.getZ() + 0.5);
        this.melodyEntity.setCustomNameTag("Melody, but depressed"); // You can use this for the name too
        this.melodyEntity.setAlwaysRenderNameTag(true);

        System.out.println("GoonBlock: Spawned client-side Melody NPC.");
    }

    public void despawnMelodyNpc() {
        GoonblockFunctions.removePlayerInfo(mc.getNetHandler(), melodyEntity.getUniqueID());

        this.melodyEntity = null;
        this.melodyPlayerInfo = null;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || this.melodyEntity == null) { return; }

        this.melodyEntity.lastTickPosX = this.melodyEntity.posX;
        this.melodyEntity.lastTickPosY = this.melodyEntity.posY;
        this.melodyEntity.lastTickPosZ = this.melodyEntity.posZ;

        this.melodyEntity.prevRotationYaw = this.melodyEntity.rotationYaw;
        this.melodyEntity.prevRotationPitch = this.melodyEntity.rotationPitch;
        this.melodyEntity.prevRotationYawHead = this.melodyEntity.rotationYawHead;
        this.melodyEntity.prevRenderYawOffset = this.melodyEntity.renderYawOffset;

        if (mc.thePlayer.getDistanceToEntity(this.melodyEntity) < 10) {
            calculateTargetLookDirection();
        }

        this.melodyEntity.rotationYawHead = interpolateRotation(this.melodyEntity.rotationYawHead, this.targetYaw, 20F);
        this.melodyEntity.renderYawOffset = interpolateRotation(this.melodyEntity.renderYawOffset, this.targetYaw, 20F);
        this.melodyEntity.rotationYaw = interpolateRotation(this.melodyEntity.rotationYaw, this.targetYaw, 20F);
        this.melodyEntity.rotationPitch = interpolateRotation(this.melodyEntity.rotationPitch, this.targetPitch, 20F);
    }

    private void calculateTargetLookDirection() {
        Vec3 playerEyePos = mc.thePlayer.getPositionEyes(1.0F); // 1.0F is partialTicks
        Vec3 npcPos = new Vec3(melodyEntity.posX, melodyEntity.posY + melodyEntity.getEyeHeight(), melodyEntity.posZ);

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

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Pre<EntityOtherPlayerMP> event) {
        if (event.entity.equals(this.melodyEntity)) {

            event.entity.rotationYawHead = this.melodyEntity.rotationYawHead;
            event.entity.renderYawOffset = this.melodyEntity.renderYawOffset;
            event.entity.rotationYaw = this.melodyEntity.rotationYaw;
            event.entity.rotationPitch = this.melodyEntity.rotationPitch;
        }
    }
    @SubscribeEvent
    public void onRenderNametag(RenderLivingEvent.Specials.Pre<EntityOtherPlayerMP> event) {
        if (event.entity.equals(this.melodyEntity)) {
            event.setCanceled(true);
        }
    }

    private void updateNpcLookDirection() {
        Vec3 playerEyePos = mc.thePlayer.getPositionEyes(1.0F); // 1.0F is partialTicks
        Vec3 npcPos = new Vec3(melodyEntity.posX, melodyEntity.posY + melodyEntity.getEyeHeight(), melodyEntity.posZ);

        double dx = playerEyePos.xCoord - npcPos.xCoord;
        double dy = playerEyePos.yCoord - npcPos.yCoord;
        double dz = playerEyePos.zCoord - npcPos.zCoord;

        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dy, horizontalDistance) * (180.0D / Math.PI)));

        this.melodyEntity.rotationYawHead = interpolateRotation(this.melodyEntity.rotationYawHead, yaw, 5f);
        this.melodyEntity.renderYawOffset = interpolateRotation(this.melodyEntity.renderYawOffset, yaw, 5f);
        this.melodyEntity.rotationYaw = interpolateRotation(this.melodyEntity.rotationYaw, yaw, 30F);
        this.melodyEntity.rotationPitch = interpolateRotation(this.melodyEntity.rotationPitch, pitch, 5f);
    }

    private float interpolateRotation(float current, float target, float speed) {
        float diff = MathHelper.wrapAngleTo180_float(target - current);
        return current + diff / speed;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (melodyEntity != null) {
            mc.getRenderManager().renderEntitySimple(melodyEntity, event.partialTicks);

            drawCustomNametag(melodyEntity, Arrays.asList("§dMelody","§e§lCLICK"), event.partialTicks);
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

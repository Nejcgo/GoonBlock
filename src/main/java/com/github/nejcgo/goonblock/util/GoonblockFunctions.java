package com.github.nejcgo.goonblock.util;

import com.github.nejcgo.goonblock.classes.NpcData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import org.lwjgl.Sys;

import java.lang.reflect.Field;
import java.util.*;

public class GoonblockFunctions {

    // Easily show a message in chat
    public static ChatComponentText sendChatMessage(Minecraft mc, String messageString){
        if(mc.thePlayer == null){
            return null;
        }

        ChatComponentText chatComponent = new ChatComponentText(messageString);

        mc.thePlayer.addChatMessage(chatComponent);

        return chatComponent;
    }

    /** Returns true if the player is playing skyblock, returns false otherwise
     *
     * @param mc The minecraft instance
     * @return True if on Skyblock, false otherwise
     */
    public static boolean getSkyblock(Minecraft mc) {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1); // Slot 1 is the sidebar

            if (sidebarObjective != null) {
                String name = sidebarObjective.getName().replaceAll("[^a-zA-Z0-9\\s()-]", ""); // Keeps letters, numbers, spaces, parentheses, hyphens. Removes others.
                System.out.println("[GoonBlock SCOREDUMP SANITIZED] " + name); // See what this looks like

                if(name.contains("SBScoreboard")){
                    System.out.println("You are ON SKYBLOCK!");
                    return true;
                }
            }
        } catch (Exception e) {
            // Silently ignore errors, as scoreboard can be unstable during transitions
            // System.err.println("Error checking scoreboard: " + e.getMessage());
        }
        return false;
    }

    /** Returns true if player is currently on hypixel, returns false otherwise
     *
     * @param mc The minecraft instance
     * @return True if on Hypixel, false otherwise
     */
    public static boolean getHypixel(Minecraft mc) {
        if (mc.isSingleplayer()) {
            return false;
        }
        ServerData serverData = mc.getCurrentServerData();
        if (serverData != null && serverData.serverIP != null) {
            if(serverData.serverIP.toLowerCase().contains("hypixel.net")){
                System.out.println("You are ON HYPIXEL!");
                return true;
            }
        }
        return false;
    }

    /** Casts a ray from the player's eyes towards the direction they're facing and returns the hit entity.
     *
     * @param length length of the raycast
     * @param partialTicks
     * @return The closest Entity, or null if no entity is found.
     */
    public static Entity performRayCast(double length, float partialTicks, List<NpcData> customNpcList) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3 lookDir = mc.thePlayer.getLook(partialTicks);
        Vec3 startPoint = mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 endPoint = startPoint.add(scaleVector(lookDir, length));

        List<Entity> entityList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, new AxisAlignedBB(
                startPoint.xCoord, startPoint.yCoord, startPoint.zCoord,
                endPoint.xCoord, endPoint.yCoord, endPoint.zCoord));

        if (entityList.isEmpty() && customNpcList.isEmpty()) {
            System.out.println("No entities nearby :(");
            return null;
        }

        System.out.println("There IS an entity nearby!");

        for (NpcData npcData : customNpcList) {
            if (npcData != null) {
                if (mc.thePlayer.getDistanceToEntity(npcData.entity) < length + 5) {
                    entityList.add(npcData.entity);
                }
            }
        }

        double closestDiatance = Double.MAX_VALUE;
        Entity closestEntity = null;
        for (Entity entity : entityList) {
            if (entity instanceof EntityArmorStand) continue;

            float borderSize = entity.getCollisionBorderSize(); // Entities have a bit of a bigger hitbox than advertised
            AxisAlignedBB entityHitbox = entity.getEntityBoundingBox().expand(borderSize, borderSize, borderSize);
            MovingObjectPosition movingObjectPosition = entityHitbox.calculateIntercept(startPoint, endPoint);
            if (movingObjectPosition == null) continue;

            double distance = startPoint.distanceTo(movingObjectPosition.hitVec);

            if (distance < closestDiatance) {
                closestEntity = entity;
                closestDiatance = distance;
            }
        }

        return closestEntity;
    }

    public static Vec3 scaleVector(Vec3 vector, double scalar) {
        return new Vec3(vector.xCoord * scalar,vector.yCoord * scalar,vector.zCoord * scalar);
    }

    /**
     * Constructs and sends a single chat message composed of multiple clickable choices.
     * This is intended to be called at the end of a dialogue script.
     *
     * @param npcId   The unique ID of the NPC the dialogue belongs to (e.g., "melody").
     *                This is used to build the command for the click event.
     * @param choices A Map where the Key is the text displayed to the player (e.g., "[YES]")
     *                and the Value is the action argument for the command (e.g., "accept_quest").
     */
    public static void sendClickableChoices(String npcId, Map<String, String> choices) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || choices == null || choices.isEmpty()) {
            return;
        }

        // Start with an empty component that we will add all the choices to.
        IChatComponent finalChoicesComponent = new ChatComponentText("");

        boolean isFirstChoice = true;
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            String choiceText = entry.getKey();
            String choiceAction = entry.getValue();

            // Add a separator between choices so they aren't crammed together.
            if (!isFirstChoice) {
                finalChoicesComponent.appendSibling(new ChatComponentText("\n"));
            }

            // 1. Create the text component for this choice.
            IChatComponent choiceComponent = new ChatComponentText(choiceText);

            // 2. Create the style and the all-important ClickEvent.
            ChatStyle choiceStyle = new ChatStyle()
                    .setColor(EnumChatFormatting.AQUA) // A nice blue to indicate it's a link.
                    .setBold(true)
                    .setChatClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            // The command that will be executed when this text is clicked.
                            "/goonblockdialogue " + npcId + " " + choiceAction
                    ))
                    .setChatHoverEvent(new HoverEvent( // Optional: Add hover text for a better UX.
                            HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentText("Click to select this option.")
                    ));

            // 3. Apply the style to the component.
            choiceComponent.setChatStyle(choiceStyle);

            // 4. Add this completed choice to our main line.
            finalChoicesComponent.appendSibling(choiceComponent);
            isFirstChoice = false;
        }

        // 5. Send the final, assembled message to the player.
        mc.thePlayer.addChatMessage(finalChoicesComponent);
    }

    public static NBTTagCompound getNbtCompound(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound();
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            stack.setTagCompound(tag);
            return tag;
        }
    }

    public static ItemStack addEnchantmentGlint(ItemStack stack) {
        NBTTagCompound tag = getNbtCompound(stack);
        tag.setTag("ench", new NBTTagList());

        stack.setTagCompound(tag);

        return stack;
    }

    /** Sets the name and lore of an ItemStack
     *
     * @param stack The ItemStack you are setting the name of
     * @param name The desired item name
     * @param loreList List of lore lines on the item
     * @return ItemStack
     */
    public static ItemStack setDisplayName(ItemStack stack, String name, List<String> loreList) {
        NBTTagCompound mainTag = getNbtCompound(stack);

        NBTTagCompound displayTag;
        if (mainTag.hasKey("display", 10)) { // 10 is the NBT ID for a Compound Tag
            displayTag = mainTag.getCompoundTag("display");
        } else {
            displayTag = new NBTTagCompound();
        }

        displayTag.setString("Name", name);

        NBTTagList loreTag = new NBTTagList();
        for (String s : loreList) {
            loreTag.appendTag(new NBTTagString(s));
        }

        displayTag.setTag("Lore", loreTag);

        mainTag.setTag("display", displayTag);

        stack.setTagCompound(mainTag);

        return stack;
    }

    /** Renders the desired ItemStack into a slot in the standard chest GUI.
     *
     * @param item The item to render
     * @param itemRenderer
     * @param guiWidth
     * @param guiHeight
     * @param slot The slot to render the item to (0-53)
     * @param numRows The amount of rows in the chest GUI
     */
    public static void renderItemIntoChestGuiSlot(ItemStack item, RenderItem itemRenderer, int guiWidth, int guiHeight, int slot, int numRows) {
        int slotX = slot % 9;
        int slotY = slot / 9;

        // That's just how it is
        int itemX = (guiWidth - 176) / 2 + 8 + slotX * 18;
        int itemY = (guiHeight - 17 - 96 - numRows*18) / 2 + 18 + slotY * 18; // The chest GUI shifts slightly depending on the number of rows it has...

        float ogZLevel = itemRenderer.zLevel;
        itemRenderer.zLevel = 300.0f;

        itemRenderer.renderItemIntoGUI(item, itemX, itemY);

        itemRenderer.zLevel = ogZLevel;
    }

    /** Returns the slot the mouse is currently hovering over. Returns null if no slot is hovered.
     *
     * @param mouseX Precalculated X
     * @param mouseY Precalculated Y
     * @param guiWidth
     * @param guiHeight
     * @param numRows Number of rows in the chest GUI
     * @return int or null
     */
    public static Integer getSlotFromMousePosition(int mouseX, int mouseY, int guiWidth, int guiHeight, int numRows) {
        if(mouseX > (guiWidth - 176) / 2 + 6 && mouseY > (guiHeight - 17 - 96 - numRows*18) / 2 + 17) {
            int slotX = (mouseX - ((guiWidth - 176) / 2 + 6)) / 18;
            int slotY = (mouseY - ((guiHeight - 17 - 96 - numRows*18) / 2 + 17)) / 18;

            int slot = 9*slotY + slotX;

            return slot;
        }

        return null;
    }

    /** Ease Out Back easing function
     *
     * @param x Ranges from 0 to 1
     * @return The position in an ease out back position, starting at 0, and ending at 1.
     */
    public static double easeOutBack(double x) {
        if(x > 1) return 1;
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }

    /**
     * A more advanced version that correctly finds and replaces text within a complex
     * IChatComponent, even if the target text is only a part of a larger styled component.
     * It will split components if necessary to preserve surrounding text.
     *
     * @param sourceComponent The original chat component.
     * @param textToFind      The plain text to search for.
     * @param replacementText The plain text to replace it with.
     * @return A new, modified IChatComponent, or null if the text was not found.
     */
    public static IChatComponent findAndReplaceInComponent(IChatComponent sourceComponent, String textToFind, String replacementText) {
        // Step 1: Flatten and get the full text to find the match location.
        List<IChatComponent> flattenedParts = flattenComponent(sourceComponent);
        StringBuilder fullUnformattedText = new StringBuilder();
        for (IChatComponent part : flattenedParts) {
            fullUnformattedText.append(part.getUnformattedTextForChat());
        }

        int startIndex = fullUnformattedText.toString().indexOf(textToFind);
        if (startIndex == -1) {
            return null; // Target text not found.
        }
        int endIndex = startIndex + textToFind.length();

        // --- NEW, MORE ROBUST RECONSTRUCTION LOGIC ---
        IChatComponent finalMessage = new ChatComponentText("");
        int currentCharIndex = 0;
        boolean replacementAdded = false;

        for (IChatComponent part : flattenedParts) {
            String partText = part.getUnformattedTextForChat();
            int partStartIndex = currentCharIndex;
            int partEndIndex = currentCharIndex + partText.length();

            // Case 1: The entire component is before the replacement zone.
            if (partEndIndex <= startIndex) {
                finalMessage.appendSibling(part.createCopy());
            }
            // Case 2: The entire component is after the replacement zone.
            else if (partStartIndex >= endIndex) {
                finalMessage.appendSibling(part.createCopy());
            }
            // Case 3: This component intersects with the replacement zone. This is the complex case.
            else {
                // A. Add the part of the text that comes BEFORE the replacement zone.
                if (partStartIndex < startIndex) {
                    String beforeText = partText.substring(0, startIndex - partStartIndex);
                    IChatComponent beforeComponent = new ChatComponentText(beforeText);
                    beforeComponent.setChatStyle(part.getChatStyle());
                    finalMessage.appendSibling(beforeComponent);
                }

                // B. Add our new replacement text, but only ONCE.
                if (!replacementAdded) {
                    IChatComponent replacementComponent = new ChatComponentText(replacementText);
                    // Copy the style from the first component we're replacing to blend in.
                    replacementComponent.setChatStyle(part.getChatStyle());
                    finalMessage.appendSibling(replacementComponent);
                    replacementAdded = true;
                }

                // C. Add the part of the text that comes AFTER the replacement zone.
                if (partEndIndex > endIndex) {
                    String afterText = partText.substring(endIndex - partStartIndex);
                    IChatComponent afterComponent = new ChatComponentText(afterText);
                    afterComponent.setChatStyle(part.getChatStyle());
                    finalMessage.appendSibling(afterComponent);
                }
            }
            currentCharIndex = partEndIndex;
        }

        return finalMessage;
    }

    /**
     * Correctly deconstructs a complex, nested IChatComponent into a flat, ordered list
     * of its final, text-bearing parts. This uses the component's iterator, which is the
     * proper way to traverse the structure.
     *
     * @param component The root component to flatten.
     * @return A List of all styled components in the correct order.
     */
    public static List<IChatComponent> flattenComponent(IChatComponent component) {
        List<IChatComponent> parts = new ArrayList<>();
        // IChatComponent is Iterable, and its iterator correctly traverses the entire tree.
        for (IChatComponent part : (Iterable<IChatComponent>) component) {
            // We only care about the parts that actually have text, to avoid empty nodes.
            // We use getUnformattedTextForChat() as it's the most direct representation of a component's own text.
            if (part.getUnformattedTextForChat() != null && !part.getUnformattedTextForChat().isEmpty()) {
                parts.add(part);
            }
        }
        return parts;
    }

    // A static field to cache the Field object for performance
    private static Field playerInfoMapField = null;

    /**
     * Uses reflection to safely add a NetworkPlayerInfo object to the client's private playerInfoMap.
     * @param netHandler The client's NetHandlerPlayClient instance.
     * @param uuid The UUID of the player/NPC to add.
     * @param playerInfo The NetworkPlayerInfo object to add.
     */
    public static void addPlayerInfo(NetHandlerPlayClient netHandler, UUID uuid, NetworkPlayerInfo playerInfo) {
        try {
            // Find the private "playerInfoMap" field if we haven't already
            if (playerInfoMapField == null) {
                playerInfoMapField = NetHandlerPlayClient.class.getDeclaredField("playerInfoMap");
                // Make the private field accessible
                playerInfoMapField.setAccessible(true);
            }
            // Get the actual Map object from the NetHandler instance
            Map<UUID, NetworkPlayerInfo> map = (Map<UUID, NetworkPlayerInfo>) playerInfoMapField.get(netHandler);
            // Now, we can safely call .put() on the real map
            map.put(uuid, playerInfo);
        } catch (Exception e) {
            System.err.println("GoonBlock: Failed to add player info via reflection!");
            e.printStackTrace();
        }
    }

    /**
     * Uses reflection to safely remove a NetworkPlayerInfo object from the client's private playerInfoMap.
     * @param netHandler The client's NetHandlerPlayClient instance.
     * @param uuid The UUID of the player/NPC to remove.
     */
    public static void removePlayerInfo(NetHandlerPlayClient netHandler, UUID uuid) {
        try {
            // We assume the field is already cached by the add method, but check again just in case.
            if (playerInfoMapField == null) {
                playerInfoMapField = NetHandlerPlayClient.class.getDeclaredField("playerInfoMap");
                playerInfoMapField.setAccessible(true);
            }
            Map<UUID, NetworkPlayerInfo> map = (Map<UUID, NetworkPlayerInfo>) playerInfoMapField.get(netHandler);
            // Safely call .remove() on the real map
            map.remove(uuid);
        } catch (Exception e) {
            System.err.println("GoonBlock: Failed to remove player info via reflection!");
            e.printStackTrace();
        }
    }

    /**
     * Creates a fully-formed, client-side ghost NPC with a custom skin.
     * This is the central utility for spawning any custom NPC in the mod.
     *
     * @param uuidStr   The unique UUID for this NPC as a String.
     * @param name      The internal name for the GameProfile.
     * @param pos       The BlockPos where the NPC should spawn.
     * @param nametag   The list of strings for the custom nametag.
     * @param texture   The Base64 texture value from Mineskin.
     * @param signature The signature for the texture from Mineskin.
     * @return An NpcData object containing the created entity and its info.
     */
    public static NpcData createNpc(String uuidStr, String name, BlockPos pos, List<String> nametag, String texture, String signature) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            // Cannot create an entity if the world doesn't exist yet.
            System.err.println("GoonBlock: Attempted to create NPC before world was loaded!");
            return null;
        }

        GameProfile profile = new GameProfile(UUID.fromString(uuidStr), name);
        profile.getProperties().put("textures", new Property("textures", texture, signature));

        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(profile);
        EntityOtherPlayerMP entity = new EntityOtherPlayerMP(mc.theWorld, profile);

        addPlayerInfo(mc.getNetHandler(), entity.getUniqueID(), playerInfo);

        // Enable all skin layers.
        entity.getDataWatcher().updateObject(10, (byte) 0x7F);
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        System.out.println("GoonBlock: Spawned client-side NPC: " + name);

        return new NpcData(entity, playerInfo, nametag);
    }
}

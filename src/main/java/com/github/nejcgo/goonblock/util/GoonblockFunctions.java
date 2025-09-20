package com.github.nejcgo.goonblock.util;

import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

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
     * @return bool
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
     * @return bool
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

    /** Returns the position in an ease out back position, starting at 0, and ending at 1.
     *
     * @param x Ranges from 0 to 1
     * @return double
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
}

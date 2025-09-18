package com.github.nejcgo.goonblock.event;

import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

public class GoodJobListener {
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientMessageReceived(ClientChatReceivedEvent event) {
        if (event.type == 0 || event.type == 2) { // Some system messages are type 0
            String rawMessage = event.message.getUnformattedText();
            String cleanMessage = StringUtils.stripControlCodes(rawMessage);

            if (cleanMessage.contains("Good job!")) {
                String playerName = mc.getSession().getUsername();
                if (cleanMessage.contains(playerName)) {
                    String praise = "§d§lGood boy ❤";

                    event.message = GoonblockFunctions.findAndReplaceInComponent(event.message, "Good job!", praise);
                }
            }
        }
    }
}

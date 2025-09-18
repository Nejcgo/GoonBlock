package com.github.nejcgo.goonblock.event;

import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SlayerKillListener {
    
    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        // type 0 is standard chat/system messages from server, type 2 is often action bar
        if (event.type == 0 || event.type == 2) { // Some system messages are type 0
            String rawMessage = event.message.getUnformattedText();
            String cleanMessage = StringUtils.stripControlCodes(rawMessage);

            if(cleanMessage.contains("SLAYER QUEST COMPLETE!")) {

            }
        }
    }
}

package com.github.nejcgo.goonblock.event;

import com.github.nejcgo.goonblock.GoonBlock;
import com.github.nejcgo.goonblock.util.GoonblockFunctions;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DungeonSecretListener {
    private Set<BlockPos> openedChests = new HashSet<>();
    private static final List<Item> SECRET_ITEMS = Arrays.asList(
            Items.arrow,
            Items.skull
    );
    private Minecraft mc;

    public DungeonSecretListener() {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            BlockPos blockPos = event.pos;
            if (!openedChests.contains(blockPos)) {
                if (mc.theWorld.getBlockState(blockPos) instanceof BlockChest) {
                    openedChests.add(blockPos);
                    triggerSecretFound();
                }
            }
        }
    }

    public void triggerSecretFound() {
        this.mc.thePlayer.playSound("goonblock:effects/clickerSfx", 2f, 1f);
        GoonblockFunctions.sendChatMessage(mc, "§d§lGood boy, " + mc.thePlayer.getName() + "❤");
    }

    @SubscribeEvent
    public void onWorldUnload() {

    }
}

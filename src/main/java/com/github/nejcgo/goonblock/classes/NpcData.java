package com.github.nejcgo.goonblock.classes;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.List;

public class NpcData {
    public EntityOtherPlayerMP entity;
    public NetworkPlayerInfo playerInfo;
    public List<String> nametag;

    public NpcData(EntityOtherPlayerMP entity, NetworkPlayerInfo playerInfo, List<String> nametag) {
        this.entity = entity;
        this.playerInfo = playerInfo;
        this.nametag = nametag;
    }
}

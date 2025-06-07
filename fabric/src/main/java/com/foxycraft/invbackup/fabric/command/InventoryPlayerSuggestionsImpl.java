package com.foxycraft.invbackup.fabric.command;

import com.foxycraft.invbackup.Invbackup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

public class InventoryPlayerSuggestionsImpl {
    public static Iterable<ServerPlayer> getOnlinePlayers() {
        MinecraftServer server = Invbackup.getServerInstance();
        if (server == null) return Collections.emptyList();
        return server.getPlayerList().getPlayers();
    }




}

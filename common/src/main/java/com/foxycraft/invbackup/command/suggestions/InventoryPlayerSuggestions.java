package com.foxycraft.invbackup.command.suggestions;

import com.foxycraft.invbackup.Invbackup;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class InventoryPlayerSuggestions {

    public static CompletableFuture<Suggestions> suggestOnlinePlayerNames(com.mojang.brigadier.context.CommandContext<?> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (ServerPlayer player : getOnlinePlayers()) {
            String playerName = player.getName().getString();
            if (playerName.toLowerCase().startsWith(remaining)) {
                builder.suggest(playerName);
            }
        }
        return builder.buildFuture();
    }

    private static Collection<ServerPlayer> getOnlinePlayers() {
        MinecraftServer server = Invbackup.getServerInstance();
        if (server == null) {
            return java.util.Collections.emptyList();
        }
        return server.getPlayerList().getPlayers();
    }
}

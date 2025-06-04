package com.pocky.inv.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class Suggestors {
    public static final SuggestionProvider<CommandSourceStack> PLAYER_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(
                context.getSource().getServer().getPlayerList().getPlayers()
                        .stream().map(ServerPlayer::getGameProfile)
                        .map(GameProfile::getName)
                        .toList(),
                builder
        );
    };
}

package com.foxycraft.invbackup.command;

import com.foxycraft.invbackup.backup.InventoryBackupManager;
import com.foxycraft.invbackup.backup.PlayerBackup;
import com.foxycraft.invbackup.command.suggestions.InventoryPlayerSuggestions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class InventoryListCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("invbackup")
                .then(Commands.literal("list")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(InventoryPlayerSuggestions::suggestOnlinePlayerNames)
                                .executes(ctx -> execute(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "player"), null))
                                .then(Commands.argument("filter", StringArgumentType.string())
                                        .executes(ctx -> execute(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "player"),
                                                StringArgumentType.getString(ctx, "filter")))))
                ).then(Commands.literal("restore")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(InventoryPlayerSuggestions::suggestOnlinePlayerNames)
                                .then(Commands.argument("timestamp", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String playerName = StringArgumentType.getString(ctx, "player");
                                            String timestamp = StringArgumentType.getString(ctx, "timestamp");
                                            return executeRestore(ctx.getSource(), playerName, timestamp);
                                        })))));
    }

    private static int execute(CommandSourceStack source, String playerName, String filter) {
        UUID uuid = resolvePlayerUuid(source.getServer(), playerName);
        //source.sendSuccess(() -> Component.literal("[Debug] Resolved UUID: " + uuid), false);

        if (uuid == null) {
            source.sendFailure(Component.literal("Could not resolve UUID for player '" + playerName + "'."));
            return 0;
        }

        // Use your manager class
        List<PlayerBackup> backups = InventoryBackupManager.getBackupsForPlayer(uuid);
        //source.sendSuccess(() -> Component.literal("[Debug] Loaded " + backups.size() + " backups via InventoryBackupManager."), false);

        if (backups.isEmpty()) {
            source.sendFailure(Component.literal("No backups found for player '" + playerName + "' (" + uuid + ")."));
            return 1;
        }

        source.sendSuccess(() -> Component.literal("Backups for " + playerName + ":"), false);
        int count = 0;

        for (PlayerBackup backup : backups) {
            String timestamp = backup.getTimestamp();
            if (filter == null || timestamp.contains(filter)) {
                String line = timestamp + " (" + backup.getEventType() + ")";
                source.sendSuccess(() -> Component.literal(line), false);
                count++;
            }
        }



        if (count == 0) {
            source.sendSuccess(() -> Component.literal("No backups matched the filter: '" + filter + "'."), false);
        }

        return 1;
    }





    private static UUID resolvePlayerUuid(MinecraftServer server, String name) {
        // 1. Try cache (for online players)
        return server.getPlayerList().getPlayers().stream()
                .filter(p -> p.getGameProfile().getName().equalsIgnoreCase(name))
                .map(p -> p.getGameProfile().getId())
                .findFirst()
                .orElseGet(() -> {
                    // 2. Try offline name->UUID fallback
                    return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
                });
    }


    private static int executeRestore(CommandSourceStack source, String playerName, String timestamp) {
        UUID uuid = resolvePlayerUuid(source.getServer(), playerName);

        if (uuid == null) {
            source.sendFailure(Component.literal("Could not resolve UUID for player '" + playerName + "'"));
            return 0;
        }

        List<PlayerBackup> backups = InventoryBackupManager.getBackupsForPlayer(uuid);
        if (backups.isEmpty()) {
            source.sendFailure(Component.literal("No backups found for '" + playerName + "'"));
            return 0;
        }

        // Find the backup matching the exact timestamp
        PlayerBackup backupToRestore = null;
        for (PlayerBackup backup : backups) {
            if (backup.getTimestamp().equals(timestamp)) {
                backupToRestore = backup;
                break;
            }
        }

        if (backupToRestore == null) {
            source.sendFailure(Component.literal("No backup found for timestamp '" + timestamp + "'"));
            return 0;
        }

        ServerPlayer target = source.getServer().getPlayerList().getPlayer(uuid);
        if (target == null) {
            source.sendFailure(Component.literal("Player '" + playerName + "' must be online to restore inventory."));
            return 0;
        }

        InventoryBackupManager.restoreBackup(target, backupToRestore);
        source.sendSuccess(() -> Component.literal("Restored backup from '" + timestamp + "' for player '" + playerName + "'."), false);
        return 1;
    }

}

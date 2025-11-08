package com.pocky.invbackups.utils;

import com.mojang.authlib.GameProfile;
import com.pocky.invbackups.InventoryBackupsMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for resolving player information for both online and offline players
 */
public class PlayerResolver {

    /**
     * Represents a resolved player (online or offline)
     */
    public static class ResolvedPlayer {
        private final UUID uuid;
        private final String name;
        private final ServerPlayer onlinePlayer; // null if offline

        public ResolvedPlayer(UUID uuid, String name, ServerPlayer onlinePlayer) {
            this.uuid = uuid;
            this.name = name;
            this.onlinePlayer = onlinePlayer;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public ServerPlayer getOnlinePlayer() {
            return onlinePlayer;
        }

        public boolean isOnline() {
            return onlinePlayer != null;
        }
    }

    /**
     * Resolve player by name, works for both online and offline players
     * @param server The server instance
     * @param playerName The player name to resolve
     * @return Optional containing ResolvedPlayer if found, empty otherwise
     */
    public static Optional<ResolvedPlayer> resolvePlayer(MinecraftServer server, String playerName) {
        // First, try to find online player
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(playerName);
        if (onlinePlayer != null) {
            return Optional.of(new ResolvedPlayer(
                    onlinePlayer.getUUID(),
                    onlinePlayer.getScoreboardName(),
                    onlinePlayer
            ));
        }

        // If not online, try to find in profile cache
        GameProfileCache profileCache = server.getProfileCache();
        if (profileCache != null) {
            Optional<GameProfile> profileOpt = profileCache.get(playerName);
            if (profileOpt.isPresent()) {
                GameProfile profile = profileOpt.get();
                InventoryBackupsMod.LOGGER.debug("Found offline player {} with UUID {}",
                    profile.getName(), profile.getId());
                return Optional.of(new ResolvedPlayer(
                        profile.getId(),
                        profile.getName(),
                        null
                ));
            }
        }

        InventoryBackupsMod.LOGGER.warn("Could not resolve player: {}", playerName);
        return Optional.empty();
    }

    /**
     * Resolve player by UUID
     * @param server The server instance
     * @param uuid The player UUID
     * @return Optional containing ResolvedPlayer if found, empty otherwise
     */
    public static Optional<ResolvedPlayer> resolvePlayer(MinecraftServer server, UUID uuid) {
        // First, try to find online player
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayer(uuid);
        if (onlinePlayer != null) {
            return Optional.of(new ResolvedPlayer(
                    onlinePlayer.getUUID(),
                    onlinePlayer.getScoreboardName(),
                    onlinePlayer
            ));
        }

        // If not online, try to find in profile cache
        GameProfileCache profileCache = server.getProfileCache();
        if (profileCache != null) {
            Optional<GameProfile> profileOpt = profileCache.get(uuid);
            if (profileOpt.isPresent()) {
                GameProfile profile = profileOpt.get();
                return Optional.of(new ResolvedPlayer(
                        profile.getId(),
                        profile.getName(),
                        null
                ));
            }
        }

        InventoryBackupsMod.LOGGER.warn("Could not resolve player with UUID: {}", uuid);
        return Optional.empty();
    }
}

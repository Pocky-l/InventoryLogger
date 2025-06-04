package com.foxycraft.invbackup.backup;

import java.time.Instant;
import java.util.UUID;

public class PlayerBackup {
    private final UUID playerUUID;
    private final Instant backupTime;
    private final String serializedInventory; // You can store NBT as string or raw bytes

    private final String eventType;

    public PlayerBackup(UUID playerUUID, Instant backupTime, String serializedInventory, String eventType) {
        this.playerUUID = playerUUID;
        this.backupTime = backupTime;
        this.serializedInventory = serializedInventory;
        this.eventType = eventType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Instant getBackupTime() {
        return backupTime;
    }

    public String getSerializedInventory() {
        return serializedInventory;
    }

    public String getEventType() {
        return eventType;
    }
}


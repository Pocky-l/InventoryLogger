package com.foxycraft.invbackup.backup;

import net.minecraft.nbt.CompoundTag;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PlayerBackup {
    private final File file;
    private final CompoundTag data;

    public PlayerBackup(File file, CompoundTag data) {
        this.file = file;
        this.data = data;
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(data.getString("uuid"));
    }

    public String getPlayerName() {
        return data.getString("name");
    }

    public String getEventType() {
        return data.getString("reason");
    }

    public String getTimestamp() {
        return data.getString("timestamp");
    }

    public Instant getBackupTime() {
        try {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
                    .parse(getTimestamp(), Instant::from);
        } catch (Exception e) {
            return Instant.ofEpochMilli(file.lastModified());
        }
    }

    public CompoundTag getData() {
        return data;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "[" + getTimestamp() + "] " + getPlayerName() + " - " + getEventType();
    }


}

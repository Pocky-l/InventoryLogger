package com.foxycraft.invbackup.fabric.config;

import com.foxycraft.invbackup.configs.IBackupConfig;
import eu.midnightdust.lib.config.MidnightConfig;

import static com.sun.jna.Native.register;


public class FabricBackupConfig extends MidnightConfig implements IBackupConfig {
    public static final String CATEGORY_GENERAL = "general";

    @Comment(category = CATEGORY_GENERAL)
    public static Comment backupSettings;

    @Entry(category = CATEGORY_GENERAL, name = "Backup on death")
    public static boolean backupOnDeath = true;

    @Entry(category = CATEGORY_GENERAL, name = "Backup on player join")
    public static boolean backupOnJoin = true;

    @Entry(category = CATEGORY_GENERAL, name = "Backup interval ticks (20 ticks = 1 second)")
    public static int backupIntervalTicks = 24000; // 20 min default

    @Entry(category = CATEGORY_GENERAL, name = "Max backups per player")
    public static int maxBackupsPerPlayer = 50;

    static {
        // Required to load/save config automatically
        register("invbackup");
    }

    @Override
    public boolean backupOnDeath() {
        return FabricBackupConfig.backupOnDeath; // Your MidnightLib config class field
    }

    @Override
    public boolean backupOnJoin() {
        return FabricBackupConfig.backupOnJoin;
    }

    @Override
    public int backupIntervalTicks() {
        return FabricBackupConfig.backupIntervalTicks;
    }

    @Override
    public int maxBackupsPerPlayer() {
        return FabricBackupConfig.maxBackupsPerPlayer;
    }
}

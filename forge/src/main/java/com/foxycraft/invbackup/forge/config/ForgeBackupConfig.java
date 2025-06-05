package com.foxycraft.invbackup.forge.config;

import com.foxycraft.invbackup.configs.IBackupConfig;
import eu.midnightdust.lib.config.MidnightConfig;

public class ForgeBackupConfig extends MidnightConfig implements IBackupConfig {
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

    // Remove static block entirely

    @Override
    public boolean backupOnDeath() {
        return ForgeBackupConfig.backupOnDeath;
    }

    @Override
    public boolean backupOnJoin() {
        return ForgeBackupConfig.backupOnJoin;
    }

    @Override
    public int backupIntervalTicks() {
        return ForgeBackupConfig.backupIntervalTicks;
    }

    @Override
    public int maxBackupsPerPlayer() {
        return ForgeBackupConfig.maxBackupsPerPlayer;
    }
}

package com.foxycraft.invbackup.configs;

public interface IBackupConfig {
    boolean backupOnDeath();
    boolean backupOnJoin();
    int backupIntervalTicks();
    int maxBackupsPerPlayer();
    boolean Debug();
}

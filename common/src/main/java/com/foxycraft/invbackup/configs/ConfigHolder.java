package com.foxycraft.invbackup.configs;

public class ConfigHolder {
    private static IBackupConfig config;

    public static void setConfig(IBackupConfig configImpl) {
        config = configImpl;
    }

    public static IBackupConfig getConfig() {
        return config;
    }
}

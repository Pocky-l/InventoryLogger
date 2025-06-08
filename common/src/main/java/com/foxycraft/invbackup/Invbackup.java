package com.foxycraft.invbackup;


import com.foxycraft.invbackup.configs.CommonConfig;
import com.foxycraft.invbackup.event.PeriodicBackupTickEvent;
import com.foxycraft.invbackup.event.PlayerJoinBackupEvent;
import dev.JustRed23.abcm.Config;
import dev.JustRed23.abcm.exception.ConfigInitException;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public final class Invbackup {
    public static final String MOD_ID = "invbackup";
    private static MinecraftServer serverInstance;

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }
    public static final Logger LOGGER = LoggerFactory.getLogger("InvBackup");
    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }
    public static void init() {
        try {
            File configDir = new File("config");
            Config.setConfigDir(configDir);
            Config.addScannable(CommonConfig.class);
            Config.init();
        } catch (ConfigInitException e) {
            throw new RuntimeException("Failed to initialize ABCM config for Invbackup", e);
        }
        PeriodicBackupTickEvent.register();
        PlayerJoinBackupEvent.register();
    }

    public static void reloadConfig() throws ConfigInitException {
        Config.rescan(true);
    }
}

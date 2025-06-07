package com.foxycraft.invbackup;


import com.foxycraft.invbackup.event.PeriodicBackupTickEvent;
import com.foxycraft.invbackup.event.PlayerJoinBackupEvent;
import net.minecraft.server.MinecraftServer;


public final class Invbackup {
    public static final String MOD_ID = "invbackup";
    private static MinecraftServer serverInstance;

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }

    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }
    public static void init() {
        PeriodicBackupTickEvent.register();
        PlayerJoinBackupEvent.register();
    }
}

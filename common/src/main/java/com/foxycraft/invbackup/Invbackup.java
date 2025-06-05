package com.foxycraft.invbackup;


import com.foxycraft.invbackup.event.PeriodicBackupTickEvent;
import com.foxycraft.invbackup.event.PlayerDeathBackupEvent;
import com.foxycraft.invbackup.event.PlayerJoinBackupEvent;



public final class Invbackup {
    public static final String MOD_ID = "invbackup";

    public static void init() {

        PeriodicBackupTickEvent.register();
        PlayerDeathBackupEvent.register();
        PlayerJoinBackupEvent.register();
    }
}

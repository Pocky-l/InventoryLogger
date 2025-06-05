package com.foxycraft.invbackup;

import com.foxycraft.invbackup.config.BackupConfig;
import com.foxycraft.invbackup.event.PeriodicBackupTickEvent;
import com.foxycraft.invbackup.event.PlayerDeathBackupEvent;
import com.foxycraft.invbackup.event.PlayerJoinBackupEvent;
import com.foxycraft.invbackup.gui.InventoryBackupMenu;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.world.inventory.MenuType;

public final class Invbackup {
    public static final String MOD_ID = "invbackup";

    public static void init() {
        MidnightConfig.init("modid", BackupConfig.class);
        PeriodicBackupTickEvent.register();
        PlayerDeathBackupEvent.register();
        PlayerJoinBackupEvent.register();
    }
}

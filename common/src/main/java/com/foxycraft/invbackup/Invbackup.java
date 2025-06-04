package com.foxycraft.invbackup;

import com.foxycraft.invbackup.config.BackupConfig;
import com.foxycraft.invbackup.gui.InventoryBackupMenu;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.world.inventory.MenuType;

public final class Invbackup {
    public static final String MOD_ID = "invbackup";
    public static final MenuType<InventoryBackupMenu> INV_BACKUP_MENU = new MenuType<>(InventoryBackupMenu::new);
    public static void init() {
        MidnightConfig.init("modid", BackupConfig.class);
    }
}

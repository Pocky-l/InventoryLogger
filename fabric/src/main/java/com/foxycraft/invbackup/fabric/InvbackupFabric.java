package com.foxycraft.invbackup.fabric;

import com.foxycraft.invbackup.Invbackup;
import com.foxycraft.invbackup.configs.ConfigHolder;
import com.foxycraft.invbackup.fabric.config.FabricBackupConfig;
import net.fabricmc.api.ModInitializer;

public final class InvbackupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigHolder.setConfig(new FabricBackupConfig());
        Invbackup.init();
    }
}

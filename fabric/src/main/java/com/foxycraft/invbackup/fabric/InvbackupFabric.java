package com.foxycraft.invbackup.fabric;

import com.foxycraft.invbackup.Invbackup;
import com.foxycraft.invbackup.command.CommandInit;
import com.foxycraft.invbackup.configs.ConfigHolder;
import com.foxycraft.invbackup.fabric.config.FabricBackupConfig;
import com.foxycraft.invbackup.fabric.events.FabricPlayerDeathEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class InvbackupFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        FabricBackupConfig.init("invbackup");

        ConfigHolder.setConfig(new FabricBackupConfig());

        FabricPlayerDeathEvent.register();
        Invbackup.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommandInit.register(dispatcher);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(Invbackup::setServerInstance);
    }
}


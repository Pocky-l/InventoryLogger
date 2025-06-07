package com.foxycraft.invbackup.forge;

import com.foxycraft.invbackup.Invbackup;
import com.foxycraft.invbackup.command.CommandInit;
import com.foxycraft.invbackup.configs.ConfigHolder;
import com.foxycraft.invbackup.forge.config.ForgeBackupConfig;
import dev.architectury.platform.forge.EventBuses;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Invbackup.MOD_ID)
public final class InvbackupForge {

    public InvbackupForge() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        EventBuses.registerModEventBus(Invbackup.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ConfigHolder.setConfig(new ForgeBackupConfig());
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            CommandInit.register(event.getDispatcher());
        });
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);


    }
    private void onServerStarted(ServerStartedEvent event) {
        Invbackup.setServerInstance(event.getServer());
    }
    private void setup(final FMLCommonSetupEvent event) {
        MidnightConfig.init("invbackup", ForgeBackupConfig.class);
        Invbackup.init();
    }
}

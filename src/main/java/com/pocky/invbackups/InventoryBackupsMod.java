package com.pocky.invbackups;

import com.mojang.logging.LogUtils;
import com.pocky.invbackups.config.InventoryConfig;
import com.pocky.invbackups.events.CommandManager;
import com.pocky.invbackups.events.PlayerConnectionEvent;
import com.pocky.invbackups.events.PlayerDeadEvent;
import com.pocky.invbackups.events.PlayerTickHandler;
import com.pocky.invbackups.events.ServerTickHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(InventoryBackupsMod.MODID)
public class InventoryBackupsMod {

    public static final String MODID = "inventorybackups";

    public static final Logger LOGGER = LogUtils.getLogger();

    public InventoryBackupsMod(ModContainer container) {
        LOGGER.info("Initializing InventoryBackups mod...");

        container.registerConfig(ModConfig.Type.COMMON,
                InventoryConfig.COMMON_CONFIG, "inventory/InventoryBackups.toml");

        LOGGER.info("Registering event handlers...");
        NeoForge.EVENT_BUS.register(new CommandManager());
        NeoForge.EVENT_BUS.register(new PlayerTickHandler());
        NeoForge.EVENT_BUS.register(new PlayerDeadEvent());
        NeoForge.EVENT_BUS.register(new PlayerConnectionEvent());
        NeoForge.EVENT_BUS.register(new ServerTickHandler());

        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("InventoryBackups mod initialized successfully!");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("Server started, loading configuration...");
        PlayerDeadEvent.deadSaveEnabled = InventoryConfig.general.deadSaveEnabled.get();
        PlayerTickHandler.tickSaveEnabled = InventoryConfig.general.tickSaveEnabled.get();
        PlayerTickHandler.PERIOD = InventoryConfig.general.preservationPeriod.get();
        PlayerConnectionEvent.joinSaveEnabled = InventoryConfig.general.joinSaveEnabled.get();
        PlayerConnectionEvent.quitSaveEnabled = InventoryConfig.general.quitSaveEnabled.get();

        LOGGER.info("Configuration loaded:");
        LOGGER.info("  - Tick save enabled: {}", PlayerTickHandler.tickSaveEnabled);
        LOGGER.info("  - Save period: {} seconds", PlayerTickHandler.PERIOD);
        LOGGER.info("  - Death save enabled: {}", PlayerDeadEvent.deadSaveEnabled);
        LOGGER.info("  - Join save enabled: {}", PlayerConnectionEvent.joinSaveEnabled);
        LOGGER.info("  - Quit save enabled: {}", PlayerConnectionEvent.quitSaveEnabled);
    }
}

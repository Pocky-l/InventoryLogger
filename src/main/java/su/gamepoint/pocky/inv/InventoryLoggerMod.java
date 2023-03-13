package su.gamepoint.pocky.inv;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.events.CommandManager;
import su.gamepoint.pocky.inv.events.PlayerDeadEvent;
import su.gamepoint.pocky.inv.events.PlayerTickEvent;

@Mod(InventoryLoggerMod.MODID)
public class InventoryLoggerMod {

    public static final String MODID = "inv_logger";

    public static final Logger LOGGER = LogUtils.getLogger();

    public InventoryLoggerMod() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                InventoryConfig.COMMON_CONFIG, "inventory/InventoryLogger.toml");

        MinecraftForge.EVENT_BUS.register(new CommandManager());
        MinecraftForge.EVENT_BUS.register(new PlayerTickEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerDeadEvent());

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        PlayerDeadEvent.deadSaveEnabled = InventoryConfig.general.deadSaveEnabled.get();
        PlayerTickEvent.tickSaveEnabled = InventoryConfig.general.tickSaveEnabled.get();
        PlayerTickEvent.PERIOD = InventoryConfig.general.preservationPeriod.get();
    }
}

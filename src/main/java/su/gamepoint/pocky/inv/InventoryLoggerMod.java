package su.gamepoint.pocky.inv;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.events.CommandManager;
import su.gamepoint.pocky.inv.events.PlayerDeadEvent;
import su.gamepoint.pocky.inv.events.PlayerTickEvent;

@Mod(InventoryLoggerMod.MODID)
public class InventoryLoggerMod {

    public static final String MODID = "inv_logger";

    public static final Logger LOGGER = LogManager.getLogger("Inventory");

    public InventoryLoggerMod() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
                InventoryConfig.COMMON_CONFIG, "inventory/InventoryLogger.toml");

        MinecraftForge.EVENT_BUS.register(new CommandManager());
        MinecraftForge.EVENT_BUS.register(new PlayerTickEvent());
        MinecraftForge.EVENT_BUS.register(new PlayerDeadEvent());

        MinecraftForge.EVENT_BUS.register(this);
    }
}

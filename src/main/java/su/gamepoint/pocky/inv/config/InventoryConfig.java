package su.gamepoint.pocky.inv.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class InventoryConfig {
    public static final General general;
    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER;

    // Don't judge me! It's because of auto formatting moving the order around!
    static {
        COMMON_BUILDER = new ForgeConfigSpec.Builder();

        general = new General();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class General {

        public final ForgeConfigSpec.LongValue preservationPeriod;

        General() {
            COMMON_BUILDER.push("general");

            this.preservationPeriod = COMMON_BUILDER
                    .comment("Determines the frequency at which inventory will be saved. 60 - every minute, -1 - never.")
                    .defineInRange("preservationPeriod",60, -1, Long.MAX_VALUE);

            COMMON_BUILDER.pop();
        }
    }
}

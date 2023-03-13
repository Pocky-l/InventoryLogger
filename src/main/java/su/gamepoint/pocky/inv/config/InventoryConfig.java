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

        public final ForgeConfigSpec.BooleanValue tickSaveEnabled;
        public final ForgeConfigSpec.LongValue preservationPeriod;
        public final ForgeConfigSpec.BooleanValue deadSaveEnabled;

        General() {
            COMMON_BUILDER.push("general");

            this.tickSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory every N seconds.")
                    .define("tickSaveEnabled", true);

            this.preservationPeriod = COMMON_BUILDER
                    .comment("Determines the frequency at which inventory will be saved. 60 - every minute, -1 - never.")
                    .defineInRange("preservationPeriod",60, -1, Long.MAX_VALUE);

            this.deadSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory on death")
                    .define("deadSaveEnabled", true);

            COMMON_BUILDER.pop();
        }
    }
}

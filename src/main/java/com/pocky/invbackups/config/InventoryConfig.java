package com.pocky.invbackups.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class InventoryConfig {
    public static final General general;
    public static final ModConfigSpec COMMON_CONFIG;
    private static final ModConfigSpec.Builder COMMON_BUILDER;

    // Don't judge me! It's because of auto formatting moving the order around!
    static {
        COMMON_BUILDER = new ModConfigSpec.Builder();

        general = new General();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class General {

        public final ModConfigSpec.BooleanValue tickSaveEnabled;
        public final ModConfigSpec.LongValue preservationPeriod;
        public final ModConfigSpec.BooleanValue deadSaveEnabled;
        public final ModConfigSpec.BooleanValue joinSaveEnabled;
        public final ModConfigSpec.BooleanValue quitSaveEnabled;
        public final ModConfigSpec.IntValue retentionDays;

        General() {
            COMMON_BUILDER.push("general");

            this.tickSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory every N seconds.")
                    .define("tickSaveEnabled", true);

            this.preservationPeriod = COMMON_BUILDER
                    .comment("Determines the frequency at which inventory will be saved in seconds. 600 - every 10 minutes.")
                    .defineInRange("preservationPeriod", 600, -1, Long.MAX_VALUE);

            this.deadSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory on death")
                    .define("deadSaveEnabled", true);

            this.joinSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory when player joins the server")
                    .define("joinSaveEnabled", true);

            this.quitSaveEnabled = COMMON_BUILDER
                    .comment("true - saves inventory when player quits the server")
                    .define("quitSaveEnabled", true);

            this.retentionDays = COMMON_BUILDER
                    .comment("Number of days to keep backup files. Older backups will be automatically deleted. 7 - keep for 7 days.")
                    .defineInRange("retentionDays", 7, 1, 365);

            COMMON_BUILDER.pop();
        }
    }
}

package com.foxycraft.invbackup.utils;

import static com.foxycraft.invbackup.Invbackup.LOGGER;

public class LogUtil {
    private static final String PREFIX = "[invbackup]: ";

    public static void info(String msg, Object... args) {
        LOGGER.info(PREFIX + msg, args);
    }

    public static void debug(String msg, Object... args) {
        LOGGER.debug(PREFIX + msg, args);
    }

    public static void warn(String msg, Object... args) {
        LOGGER.warn(PREFIX + msg, args);
    }

    public static void error(String msg, Object... args) {
        LOGGER.error(PREFIX + msg, args);
    }
}

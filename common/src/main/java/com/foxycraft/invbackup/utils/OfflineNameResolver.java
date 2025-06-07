package com.foxycraft.invbackup.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfflineNameResolver {
    private static final Map<UUID, String> cache = new HashMap<>();

    public static String getNameFromUUID(UUID uuid) {
        // Stub: You can load from saved metadata, a custom lookup file, or just return UUID.toString()
        return cache.getOrDefault(uuid, uuid.toString());
    }

    public static void setName(UUID uuid, String name) {
        cache.put(uuid, name);
    }
}


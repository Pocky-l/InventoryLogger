package com.pocky.invbackups.ui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatUI {

    /**
     * Creates a translatable component with formatting
     */
    private static Component t(String key, Object... args) {
        return Component.translatable(key, args);
    }

    /**
     * Gets prefix with proper formatting
     */
    private static Component getPrefix() {
        return Component.literal("§8[§6").append(t("invbackups.prefix").copy().withStyle(style -> style.withColor(ChatFormatting.GOLD))).append(Component.literal("§8] §r"));
    }

    /**
     * Creates a header for the chat interface
     */
    public static Component createHeader(String translationKey) {
        return Component.literal("\n§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                .append(Component.literal("§6§l").append(t(translationKey)).append(Component.literal("\n")))
                .append(Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"));
    }

    /**
     * Creates a footer for the chat interface
     */
    public static Component createFooter() {
        return Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Displays a list of backups with interactive buttons and pagination
     * Works for both online and offline players
     */
    public static void showBackupList(ServerPlayer executor, UUID targetUuid, String targetName, String filter, int page) {
        File folder = new File("InventoryLog/inventory/" + targetUuid + "/");
        File[] listOfFiles = folder.listFiles();

        executor.sendSystemMessage(createHeader("invbackups.header.backup_list"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            executor.sendSystemMessage(Component.literal("✖ ")
                    .withStyle(ChatFormatting.RED)
                    .append(t("invbackups.error.no_backups").copy().withStyle(ChatFormatting.RED)));
            executor.sendSystemMessage(createFooter());
            return;
        }

        List<File> matchingFiles = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".json") &&
                (filter.isEmpty() || file.getName().startsWith(filter))) {
                matchingFiles.add(file);
            }
        }

        if (matchingFiles.isEmpty()) {
            executor.sendSystemMessage(Component.literal("✖ ")
                    .withStyle(ChatFormatting.RED)
                    .append(t("invbackups.error.no_match", Component.literal(filter).withStyle(ChatFormatting.YELLOW))
                            .copy().withStyle(ChatFormatting.RED)));
            executor.sendSystemMessage(createFooter());
            return;
        }

        // Sort files by name (newest first)
        matchingFiles.sort((a, b) -> b.getName().compareTo(a.getName()));

        // Calculate pagination
        final int ITEMS_PER_PAGE = 10;
        int totalPages = (int) Math.ceil((double) matchingFiles.size() / ITEMS_PER_PAGE);
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, matchingFiles.size());

        // Show header info
        executor.sendSystemMessage(t("invbackups.info.player",
                Component.literal(targetName).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY));
        executor.sendSystemMessage(t("invbackups.info.total_backups",
                Component.literal(String.valueOf(matchingFiles.size())).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY));
        executor.sendSystemMessage(t("invbackups.info.page",
                Component.literal(String.valueOf(currentPage)).withStyle(ChatFormatting.WHITE),
                Component.literal(String.valueOf(totalPages)).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY).append(Component.literal("\n")));

        // Show quick date filters
        showQuickFilters(executor, targetName);

        // Show backups for current page
        for (int i = startIndex; i < endIndex; i++) {
            File file = matchingFiles.get(i);
            String fileName = file.getName().replace(".json", "");

            MutableComponent line = Component.literal((i + 1) + ". ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(formatFileNameComponent(fileName).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" "));

            // View button
            MutableComponent viewBtn = Component.literal("[👁]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/inventory view " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.view").copy().withStyle(ChatFormatting.GREEN))));

            // Restore button
            MutableComponent restoreBtn = Component.literal(" [↻]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.YELLOW)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/inventory set " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.restore").copy().withStyle(ChatFormatting.YELLOW))));

            // Copy to self button
            MutableComponent copyBtn = Component.literal(" [📥]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/inventory copy " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.copy").copy().withStyle(ChatFormatting.AQUA))));

            line.append(viewBtn).append(restoreBtn).append(copyBtn);
            executor.sendSystemMessage(line);
        }

        executor.sendSystemMessage(Component.literal(""));

        // Show pagination controls
        showPaginationControls(executor, targetName, filter, currentPage, totalPages);

        executor.sendSystemMessage(createFooter());
    }

    /**
     * Shows quick date filter buttons
     */
    private static void showQuickFilters(ServerPlayer executor, String targetName) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String today = now.format(formatter);
        String yesterday = now.minusDays(1).format(formatter);
        String weekAgo = now.minusDays(7).format(formatter);
        String monthStart = now.withDayOfMonth(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

        MutableComponent filterLine = t("invbackups.filter.quick").copy()
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" "));

        // Today button
        MutableComponent todayBtn = Component.literal("[📅 ")
                .append(t("invbackups.filter.today"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/inventory list " + targetName + " " + today + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.today").copy().withStyle(ChatFormatting.GRAY))));

        // Yesterday button
        MutableComponent yesterdayBtn = Component.literal(" [")
                .append(t("invbackups.filter.yesterday"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/inventory list " + targetName + " " + yesterday + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.yesterday").copy().withStyle(ChatFormatting.GRAY))));

        // This month button
        MutableComponent monthBtn = Component.literal(" [")
                .append(t("invbackups.filter.this_month"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/inventory list " + targetName + " " + monthStart + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.this_month").copy().withStyle(ChatFormatting.GRAY))));

        // All backups button
        MutableComponent allBtn = Component.literal(" [")
                .append(t("invbackups.filter.all"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.GRAY)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/inventory list " + targetName + " \"\" 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.all").copy().withStyle(ChatFormatting.GRAY))));

        filterLine.append(todayBtn).append(yesterdayBtn).append(monthBtn).append(allBtn);
        executor.sendSystemMessage(filterLine);
        executor.sendSystemMessage(Component.literal(""));
    }

    /**
     * Shows pagination navigation buttons
     */
    private static void showPaginationControls(ServerPlayer executor, String targetName, String filter, int currentPage, int totalPages) {
        if (totalPages <= 1) {
            return; // No need for pagination if only one page
        }

        MutableComponent navLine = Component.literal("[").withStyle(ChatFormatting.DARK_GRAY);

        // Previous page button
        if (currentPage > 1) {
            MutableComponent prevBtn = Component.literal("◀ ")
                    .append(t("invbackups.button.previous"))
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/inventory list " + targetName + " " + filter + " " + (currentPage - 1)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.previous", currentPage - 1).copy().withStyle(ChatFormatting.GREEN))));
            navLine.append(prevBtn);
        } else {
            navLine.append(Component.literal("◀ ")
                    .append(t("invbackups.button.previous"))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        navLine.append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("|").withStyle(ChatFormatting.DARK_GRAY))
                .append(t("invbackups.info.page",
                        Component.literal(String.valueOf(currentPage)).withStyle(ChatFormatting.WHITE),
                        Component.literal(String.valueOf(totalPages)).withStyle(ChatFormatting.WHITE))
                        .copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY));

        // Next page button
        if (currentPage < totalPages) {
            MutableComponent nextBtn = t("invbackups.button.next").copy()
                    .append(Component.literal(" ▶"))
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/inventory list " + targetName + " " + filter + " " + (currentPage + 1)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.next", currentPage + 1).copy().withStyle(ChatFormatting.GREEN))));
            navLine.append(nextBtn);
        } else {
            navLine.append(t("invbackups.button.next").copy()
                    .append(Component.literal(" ▶"))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        navLine.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
        executor.sendSystemMessage(navLine);
    }

    /**
     * Formats filename to be more readable with proper component formatting
     */
    private static MutableComponent formatFileNameComponent(String fileName) {
        String cleanName;
        Component typeLabel;
        String emoji;
        ChatFormatting color;

        if (fileName.endsWith("-death")) {
            cleanName = fileName.replace("-death", "");
            typeLabel = t("invbackups.type.death");
            emoji = "💀 ";
            color = ChatFormatting.RED;
        } else if (fileName.endsWith("-join")) {
            cleanName = fileName.replace("-join", "");
            typeLabel = t("invbackups.type.join");
            emoji = "➡ ";
            color = ChatFormatting.GREEN;
        } else if (fileName.endsWith("-quit")) {
            cleanName = fileName.replace("-quit", "");
            typeLabel = t("invbackups.type.quit");
            emoji = "⬅ ";
            color = ChatFormatting.GRAY;
        } else {
            cleanName = fileName;
            typeLabel = t("invbackups.type.auto");
            emoji = "⏰ ";
            color = ChatFormatting.YELLOW;
        }

        return Component.literal(emoji + cleanName + " ")
                .append(typeLabel.copy().withStyle(color));
    }

    /**
     * Formats filename to string (legacy method for compatibility)
     */
    private static String formatFileName(String fileName) {
        return formatFileNameComponent(fileName).getString();
    }

    /**
     * Shows success message
     */
    public static void showSuccess(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.empty()
                .append(getPrefix())
                .append(Component.literal("✔ " + message).withStyle(ChatFormatting.GREEN)));
    }

    /**
     * Shows error message
     */
    public static void showError(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.empty()
                .append(getPrefix())
                .append(Component.literal("✖ " + message).withStyle(ChatFormatting.RED)));
    }

    /**
     * Shows info message
     */
    public static void showInfo(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.empty()
                .append(getPrefix())
                .append(Component.literal(message).withStyle(ChatFormatting.GRAY)));
    }

    /**
     * Shows help message with all available commands
     */
    public static void showHelp(ServerPlayer player) {
        player.sendSystemMessage(createHeader("invbackups.header.help"));

        player.sendSystemMessage(t("invbackups.help.player").copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.player.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.help.list").copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.list.desc").copy().withStyle(ChatFormatting.GRAY)));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.list.desc2").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.help.view").copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.view.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.help.set").copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.set.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.help.copy").copy().withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.copy.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.help.filters").copy().withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.filter.month").copy().withStyle(ChatFormatting.WHITE)));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.filter.day").copy().withStyle(ChatFormatting.WHITE)));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.help.filter.page").copy().withStyle(ChatFormatting.WHITE)).append(Component.literal("\n")));

        player.sendSystemMessage(createFooter());
    }

    // ==================== ENDER CHEST UI METHODS ====================

    /**
     * Displays a list of ender chest backups with interactive buttons and pagination
     * Works for both online and offline players
     */
    public static void showEnderChestBackupList(ServerPlayer executor, UUID targetUuid, String targetName, String filter, int page) {
        File folder = new File("InventoryLog/enderchest/" + targetUuid + "/");
        File[] listOfFiles = folder.listFiles();

        executor.sendSystemMessage(createHeader("invbackups.enderchest.header.backup_list"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            executor.sendSystemMessage(Component.literal("✖ ")
                    .withStyle(ChatFormatting.RED)
                    .append(t("invbackups.error.no_enderchest_backups").copy().withStyle(ChatFormatting.RED)));
            executor.sendSystemMessage(createFooter());
            return;
        }

        List<File> matchingFiles = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".json") &&
                (filter.isEmpty() || file.getName().startsWith(filter))) {
                matchingFiles.add(file);
            }
        }

        if (matchingFiles.isEmpty()) {
            executor.sendSystemMessage(Component.literal("✖ ")
                    .withStyle(ChatFormatting.RED)
                    .append(t("invbackups.error.no_match", Component.literal(filter).withStyle(ChatFormatting.YELLOW))
                            .copy().withStyle(ChatFormatting.RED)));
            executor.sendSystemMessage(createFooter());
            return;
        }

        // Sort files by name (newest first)
        matchingFiles.sort((a, b) -> b.getName().compareTo(a.getName()));

        // Calculate pagination
        final int ITEMS_PER_PAGE = 10;
        int totalPages = (int) Math.ceil((double) matchingFiles.size() / ITEMS_PER_PAGE);
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, matchingFiles.size());

        // Show header info
        executor.sendSystemMessage(t("invbackups.info.player",
                Component.literal(targetName).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY));
        executor.sendSystemMessage(t("invbackups.enderchest.info.total_backups",
                Component.literal(String.valueOf(matchingFiles.size())).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY));
        executor.sendSystemMessage(t("invbackups.info.page",
                Component.literal(String.valueOf(currentPage)).withStyle(ChatFormatting.WHITE),
                Component.literal(String.valueOf(totalPages)).withStyle(ChatFormatting.WHITE))
                .copy().withStyle(ChatFormatting.GRAY).append(Component.literal("\n")));

        // Show quick date filters
        showEnderChestQuickFilters(executor, targetName);

        // Show backups for current page
        for (int i = startIndex; i < endIndex; i++) {
            File file = matchingFiles.get(i);
            String fileName = file.getName().replace(".json", "");

            MutableComponent line = Component.literal((i + 1) + ". ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(formatFileNameComponent(fileName).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" "));

            // View button
            MutableComponent viewBtn = Component.literal("[👁]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/enderchest view " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.view").copy().withStyle(ChatFormatting.GREEN))));

            // Restore button
            MutableComponent restoreBtn = Component.literal(" [↻]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.YELLOW)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/enderchest set " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.restore").copy().withStyle(ChatFormatting.YELLOW))));

            // Copy to self button
            MutableComponent copyBtn = Component.literal(" [📥]")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/enderchest copy " + targetName + " " + fileName))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.copy").copy().withStyle(ChatFormatting.AQUA))));

            line.append(viewBtn).append(restoreBtn).append(copyBtn);
            executor.sendSystemMessage(line);
        }

        executor.sendSystemMessage(Component.literal(""));

        // Show pagination controls
        showEnderChestPaginationControls(executor, targetName, filter, currentPage, totalPages);

        executor.sendSystemMessage(createFooter());
    }

    /**
     * Shows quick date filter buttons for ender chest
     */
    private static void showEnderChestQuickFilters(ServerPlayer executor, String targetName) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String today = now.format(formatter);
        String yesterday = now.minusDays(1).format(formatter);
        String monthStart = now.withDayOfMonth(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

        MutableComponent filterLine = t("invbackups.filter.quick").copy()
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" "));

        // Today button
        MutableComponent todayBtn = Component.literal("[📅 ")
                .append(t("invbackups.filter.today"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/enderchest list " + targetName + " " + today + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.today").copy().withStyle(ChatFormatting.GRAY))));

        // Yesterday button
        MutableComponent yesterdayBtn = Component.literal(" [")
                .append(t("invbackups.filter.yesterday"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/enderchest list " + targetName + " " + yesterday + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.yesterday").copy().withStyle(ChatFormatting.GRAY))));

        // This month button
        MutableComponent monthBtn = Component.literal(" [")
                .append(t("invbackups.filter.this_month"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/enderchest list " + targetName + " " + monthStart + " 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.this_month").copy().withStyle(ChatFormatting.GRAY))));

        // All backups button
        MutableComponent allBtn = Component.literal(" [")
                .append(t("invbackups.filter.all"))
                .append(Component.literal("]"))
                .withStyle(style -> style
                        .withColor(ChatFormatting.GRAY)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/enderchest list " + targetName + " \"\" 1"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                t("invbackups.filter.hover.all").copy().withStyle(ChatFormatting.GRAY))));

        filterLine.append(todayBtn).append(yesterdayBtn).append(monthBtn).append(allBtn);
        executor.sendSystemMessage(filterLine);
        executor.sendSystemMessage(Component.literal(""));
    }

    /**
     * Shows pagination navigation buttons for ender chest
     */
    private static void showEnderChestPaginationControls(ServerPlayer executor, String targetName, String filter, int currentPage, int totalPages) {
        if (totalPages <= 1) {
            return; // No need for pagination if only one page
        }

        MutableComponent navLine = Component.literal("[").withStyle(ChatFormatting.DARK_GRAY);

        // Previous page button
        if (currentPage > 1) {
            MutableComponent prevBtn = Component.literal("◀ ")
                    .append(t("invbackups.button.previous"))
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/enderchest list " + targetName + " " + filter + " " + (currentPage - 1)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.previous", currentPage - 1).copy().withStyle(ChatFormatting.GREEN))));
            navLine.append(prevBtn);
        } else {
            navLine.append(Component.literal("◀ ")
                    .append(t("invbackups.button.previous"))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        navLine.append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("|").withStyle(ChatFormatting.DARK_GRAY))
                .append(t("invbackups.info.page",
                        Component.literal(String.valueOf(currentPage)).withStyle(ChatFormatting.WHITE),
                        Component.literal(String.valueOf(totalPages)).withStyle(ChatFormatting.WHITE))
                        .copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY));

        // Next page button
        if (currentPage < totalPages) {
            MutableComponent nextBtn = t("invbackups.button.next").copy()
                    .append(Component.literal(" ▶"))
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/enderchest list " + targetName + " " + filter + " " + (currentPage + 1)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    t("invbackups.button.hover.next", currentPage + 1).copy().withStyle(ChatFormatting.GREEN))));
            navLine.append(nextBtn);
        } else {
            navLine.append(t("invbackups.button.next").copy()
                    .append(Component.literal(" ▶"))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        navLine.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
        executor.sendSystemMessage(navLine);
    }

    /**
     * Shows help message for ender chest commands
     */
    public static void showEnderChestHelp(ServerPlayer player) {
        player.sendSystemMessage(createHeader("invbackups.enderchest.header.help"));

        player.sendSystemMessage(t("invbackups.enderchest.help.player").copy().withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.enderchest.help.player.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.enderchest.help.list").copy().withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.enderchest.help.list.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.enderchest.help.view").copy().withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.enderchest.help.view.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.enderchest.help.set").copy().withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.enderchest.help.set.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(t("invbackups.enderchest.help.copy").copy().withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.literal("  ").append(t("invbackups.enderchest.help.copy.desc").copy().withStyle(ChatFormatting.GRAY)).append(Component.literal("\n")));

        player.sendSystemMessage(createFooter());
    }
}
